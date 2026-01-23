// Vercel Serverless Function
// POST /api/reparse  { text: string, questionNumbers?: number[] } -> { answers: { "1": "A", "2": "Apple", ... }, note?: string }
// Environment Variables (set in Vercel):
//   REPARSE_PROVIDER=openrouter (default: openrouter)
//   OPENROUTER_API_KEY=sk-or-v1-... (required for OpenRouter)
//   OPENROUTER_MODEL=meta-llama/llama-3.3-70b-instruct:free (default)
//   OPENROUTER_SITE_URL=https://iskorlyapp.vercel.app
//   OPENROUTER_APP_NAME=Iskorly Reparser

// Lite regex-based fallback parser
function liteFallbackParser(text) {
  const answers = {};
  const lines = text.split('\n');
  
  for (const line of lines) {
    const trimmed = line.trim();
    if (!trimmed) continue;
    
    // Check for compressed pattern first: 1.A2.B3.C
    const hasCompressed = /\d+[.):\-]?[A-Z]\d+[.):\-]?[A-Z]/.test(trimmed);
    if (hasCompressed) {
      const compressed = trimmed.matchAll(/(\d+)[.):\-]?([A-Z])/g);
      for (const [, num, ans] of compressed) {
        answers[num] = ans;
      }
      continue;
    }
    
    // Pattern: 1.A or 1)B or 1-C or 1:D or 1 WORD
    const match1 = trimmed.match(/^(\d+)[.):\-\s]+([A-Za-z0-9][A-Za-z0-9\s]*)/);
    if (match1) {
      const [, num, ans] = match1;
      answers[num] = ans.trim().slice(0, 40);
      continue;
    }
    
    // Pattern: A 1. or True 30)
    const match2 = trimmed.match(/^([A-Za-z][A-Za-z0-9\s]*?)\s+(\d+)[.):\-]/);
    if (match2) {
      const [, ans, num] = match2;
      answers[num] = ans.trim().slice(0, 40);
      continue;
    }
  }
  
  return answers;
}

// Normalize answer values
function normalizeAnswer(value) {
  if (!value) return '';
  const val = String(value).trim();
  if (!val) return '';
  
  // Uppercase single letters
  const normalized = (val.length === 1 && /[A-Za-z]/.test(val))
    ? val.toUpperCase()
    : val.replace(/[.,;!?]+$/, '').slice(0, 40);
  
  return normalized;
}

// Filter answers by questionNumbers if provided
function filterAnswers(answers, questionNumbers) {
  if (!questionNumbers || !Array.isArray(questionNumbers) || questionNumbers.length === 0) {
    return answers;
  }
  
  const filtered = {};
  const numSet = new Set(questionNumbers.map(n => String(n)));
  
  for (const [k, v] of Object.entries(answers)) {
    if (numSet.has(k)) {
      filtered[k] = v;
    }
  }
  
  return filtered;
}

export default async function handler(req, res) {
  if (req.method !== 'POST') return res.status(405).json({ error: 'method_not_allowed' });

  try {
    const { text, questionNumbers } = req.body || {};
    if (!text || typeof text !== 'string') return res.status(400).json({ error: 'missing_text' });

    // Light pre-clean to help the model
    const cleaned = text
      .replace(/\u00a0/g, ' ')
      .replace(/[^\S\r\n]+/g, ' ')
      .trim()
      .slice(0, 8000); // guardrail

    const provider = process.env.REPARSE_PROVIDER || 'openrouter';
    
    // Try LLM provider first
    if (provider === 'openrouter') {
      const apiKey = process.env.OPENROUTER_API_KEY;
      
      if (apiKey) {
        try {
          const model = process.env.OPENROUTER_MODEL || 'meta-llama/llama-3.3-70b-instruct:free';
          const siteUrl = process.env.OPENROUTER_SITE_URL || 'https://iskorlyapp.vercel.app';
          const appName = process.env.OPENROUTER_APP_NAME || 'Iskorly Reparser';
          
          // Prompt: force pure JSON object with answers mapping
          const prompt = `
You are a strict JSON generator. Extract answers from OCR text that may look like:
1. Apple
2) A
3 - B
4: TRUE
C 5.
II) B

Important formats to handle:
- Headers and instructions: Ignore school name/address lines, NAME/SECTION/DATE/TEACHER fields, and INSTRUCTIONS blocks. Start parsing from the first numbered item.
- Number-first patterns: 1.A, 1)B, 1-C, 1:D, 1 WORD
- Answer-first patterns: "True 30.", "A 1.", "Apple 2)"
- Compressed multi-items: "1.A2.B3.C" or "1.A  3.Z  5.C" (split and map each)
- Cross-line linking: Number on one line ("___ 5."), answer on next ("C")
- Roman numerals: Convert I->1, II->2, III->3, IV->4, V->5, etc.
- Matching type: Blank line with letter answer nearby (e.g., "___23. C" -> 23:C)
- Identification words: Single words as answers (e.g., "31) Apple" -> 31:Apple)
- True/False variants: Normalize T->TRUE, F->FALSE, Y->YES, N->NO

Rules:
- Output ONLY JSON with shape: {"answers":{"1":"...", "2":"..."}}
- Accept single letters (A..Z) and short text (words/numbers), max 40 chars
- Do not invent questions not present; if unsure, omit
- Preserve case for words; uppercase single letters
- If lines have no numbers, map in order they appear starting from 1
- Remove trailing punctuation from answers

OCR TEXT:
${cleaned}
`.trim();

          const resp = await fetch('https://openrouter.ai/api/v1/chat/completions', {
            method: 'POST',
            headers: {
              'Authorization': `Bearer ${apiKey}`,
              'Content-Type': 'application/json',
              'HTTP-Referer': siteUrl,
              'X-Title': appName
            },
            body: JSON.stringify({
              model: model,
              messages: [
                { role: 'system', content: 'You only output valid JSON. No prose.' },
                { role: 'user', content: prompt }
              ],
              temperature: 0.1,
              response_format: { type: 'json_object' }
            })
          });

          if (resp.ok) {
            const data = await resp.json();
            let raw = data?.choices?.[0]?.message?.content ?? '{}';

            // Clean typical code fences if any slip through
            if (typeof raw === 'string') {
              raw = raw.trim()
                .replace(/^```json\s*/i, '')
                .replace(/^```\s*/i, '')
                .replace(/\s*```$/i, '')
                .trim();
            }

            // Parse robustly
            let json;
            try {
              json = typeof raw === 'string' ? JSON.parse(raw) : raw;
            } catch {
              // Fallback: try to find the largest {...} block
              const first = raw.indexOf('{');
              const last = raw.lastIndexOf('}');
              if (first !== -1 && last !== -1 && last > first) {
                const slice = raw.slice(first, last + 1);
                try {
                  json = JSON.parse(slice);
                } catch {
                  json = { answers: {} };
                }
              } else {
                json = { answers: {} };
              }
            }

            // Normalize and filter answers
            const out = { answers: {} };
            const ans = json && typeof json === 'object' ? json.answers : null;
            if (ans && typeof ans === 'object') {
              for (const [k, v] of Object.entries(ans)) {
                if (!k) continue;
                const kk = String(k).trim();
                if (!/^\d+$/.test(kk)) continue;
                const normalized = normalizeAnswer(v);
                if (normalized) {
                  out.answers[kk] = normalized;
                }
              }
            }

            // Filter by questionNumbers if provided
            out.answers = filterAnswers(out.answers, questionNumbers);

            // Return successful LLM response
            res.setHeader('Access-Control-Allow-Origin', '*');
            res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
            return res.status(200).json(out);
          } else {
            // Non-OK response, fall through to lite fallback
            const errText = await resp.text().catch(() => 'no body');
            console.error('OpenRouter API error:', resp.status, errText.slice(0, 200));
          }
        } catch (error) {
          // Any error during OpenRouter call, fall through to lite fallback
          console.error('OpenRouter error:', error.message);
        }
      }
    }

    // Lite fallback parser (used when provider unavailable, no key, or error)
    const liteAnswers = liteFallbackParser(cleaned);
    
    // Normalize answers
    const normalized = {};
    for (const [k, v] of Object.entries(liteAnswers)) {
      const norm = normalizeAnswer(v);
      if (norm) {
        normalized[k] = norm;
      }
    }
    
    // Filter by questionNumbers if provided
    const filtered = filterAnswers(normalized, questionNumbers);
    
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
    return res.status(200).json({ 
      answers: filtered, 
      note: 'lite_fallback' 
    });
    
  } catch (e) {
    console.error('Unexpected error:', e.message);
    return res.status(500).json({ error: 'server_error' });
  }
}

export const config = {
  api: { bodyParser: { sizeLimit: '2mb' } } // allow big texts
};