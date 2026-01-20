const express = require('express');
const fs = require('fs');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;
const OPENAI_API_KEY = process.env.OPENAI_API_KEY;

// Middleware
app.use(express.json({ limit: '10mb' }));

// Simple rate limiting (in-memory)
const requestCounts = new Map();
const RATE_LIMIT_WINDOW_MS = 60000; // 1 minute
const RATE_LIMIT_MAX_REQUESTS = 30; // 30 requests per minute

function checkRateLimit(req, res, next) {
  const ip = req.ip || req.connection.remoteAddress;
  const now = Date.now();
  
  if (!requestCounts.has(ip)) {
    requestCounts.set(ip, { count: 1, resetTime: now + RATE_LIMIT_WINDOW_MS });
    return next();
  }
  
  const record = requestCounts.get(ip);
  
  if (now > record.resetTime) {
    record.count = 1;
    record.resetTime = now + RATE_LIMIT_WINDOW_MS;
    return next();
  }
  
  if (record.count >= RATE_LIMIT_MAX_REQUESTS) {
    return res.status(429).json({ 
      error: 'Too many requests', 
      message: 'Rate limit exceeded. Please try again later.' 
    });
  }
  
  record.count++;
  next();
}

// Load prompt from prompt.md
let systemPrompt = '';
try {
  const promptPath = path.join(__dirname, 'prompt.md');
  systemPrompt = fs.readFileSync(promptPath, 'utf8');
} catch (err) {
  console.error('Failed to load prompt.md:', err.message);
  process.exit(1);
}

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({ 
    status: 'ok', 
    timestamp: new Date().toISOString(),
    apiKeyConfigured: !!OPENAI_API_KEY
  });
});

// Main re-parser endpoint
app.post('/reparse', checkRateLimit, async (req, res) => {
  try {
    const { text, questionNumbers } = req.body;
    
    // Validate input
    if (!text || typeof text !== 'string') {
      return res.status(400).json({ 
        error: 'Invalid input', 
        message: 'Field "text" is required and must be a string' 
      });
    }
    
    if (!OPENAI_API_KEY) {
      return res.status(500).json({ 
        error: 'Configuration error', 
        message: 'OPENAI_API_KEY not configured' 
      });
    }
    
    // Prepare context from question numbers if provided
    let contextInfo = '';
    if (questionNumbers && Array.isArray(questionNumbers) && questionNumbers.length > 0) {
      contextInfo = `\n\nExpected question numbers: ${questionNumbers.join(', ')}`;
    }
    
    // Call OpenAI API
    const response = await callOpenAI(text, contextInfo);
    
    // Parse and validate response
    const parsedAnswers = parseOpenAIResponse(response);
    
    // Return in the expected format
    res.json({ answers: parsedAnswers });
    
  } catch (error) {
    console.error('Error processing re-parse request:', error);
    
    if (error.message.includes('OpenAI API')) {
      return res.status(502).json({ 
        error: 'External API error', 
        message: 'Failed to communicate with AI service' 
      });
    }
    
    res.status(500).json({ 
      error: 'Internal server error', 
      message: 'Failed to process request' 
    });
  }
});

// Call OpenAI API
async function callOpenAI(text, contextInfo) {
  const fetch = (await import('node-fetch')).default;
  
  const messages = [
    {
      role: 'system',
      content: systemPrompt + contextInfo
    },
    {
      role: 'user',
      content: `Parse the following OCR text and extract student answers:\n\n${text}`
    }
  ];
  
  const requestBody = {
    model: 'gpt-4o-mini',
    messages: messages,
    temperature: 0.1,
    max_tokens: 2000,
    response_format: { type: 'json_object' }
  };
  
  const response = await fetch('https://api.openai.com/v1/chat/completions', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${OPENAI_API_KEY}`
    },
    body: JSON.stringify(requestBody)
  });
  
  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`OpenAI API error ${response.status}: ${errorText}`);
  }
  
  const data = await response.json();
  
  if (!data.choices || !data.choices[0] || !data.choices[0].message) {
    throw new Error('Invalid response from OpenAI API');
  }
  
  return data.choices[0].message.content;
}

// Parse and validate OpenAI response
function parseOpenAIResponse(responseText) {
  try {
    const parsed = JSON.parse(responseText);
    
    // Validate structure
    if (!parsed.answers || typeof parsed.answers !== 'object') {
      console.warn('Invalid response structure, returning empty answers');
      return {};
    }
    
    // Ensure all keys are strings and values are strings
    const validated = {};
    for (const [key, value] of Object.entries(parsed.answers)) {
      if (typeof value === 'string' && value.trim()) {
        validated[key] = value.trim();
      }
    }
    
    return validated;
    
  } catch (error) {
    console.error('Failed to parse OpenAI response:', error);
    return {};
  }
}

// Error handling middleware
app.use((err, req, res, next) => {
  console.error('Unhandled error:', err);
  res.status(500).json({ 
    error: 'Internal server error', 
    message: 'An unexpected error occurred' 
  });
});

// Start server
app.listen(PORT, () => {
  console.log(`AI Re-parser server running on port ${PORT}`);
  console.log(`Health check: http://localhost:${PORT}/health`);
  console.log(`Endpoint: POST http://localhost:${PORT}/reparse`);
  console.log(`API Key configured: ${!!OPENAI_API_KEY}`);
});
