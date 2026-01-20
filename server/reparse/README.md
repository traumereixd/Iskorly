# AI Re-Parser Server

A lightweight Express.js server that provides an AI-powered endpoint to re-parse low-confidence OCR results from Iskorly answer sheets.

## Overview

This server is an optional component that can be used to improve OCR accuracy when:
- The OCR fill ratio is below the threshold (default: 50%)
- BuildConfig.REPARSE_ENDPOINT is configured in the Android app
- Teachers want to self-host or run locally for better accuracy

The server uses OpenAI's GPT models to intelligently parse noisy OCR text, handling:
- Compressed inline formats (1.A2.B3.C)
- Answer-first formats (True 30.)
- Cross-line linking
- Roman numerals
- Lyceum-style answer sheet formats

## Prerequisites

- Node.js 18+ (with npm)
- OpenAI API key

## Installation

1. Navigate to the server directory:
```bash
cd server/reparse
```

2. Install dependencies:
```bash
npm install
```

3. Set up environment variables:

Create a `.env` file or export variables:
```bash
export OPENAI_API_KEY="sk-your-api-key-here"
export PORT=3000  # Optional, defaults to 3000
```

Or create a `.env` file in the `server/reparse` directory:
```
OPENAI_API_KEY=sk-your-api-key-here
PORT=3000
```

To use the `.env` file, you'll need to install `dotenv`:
```bash
npm install dotenv
```

And add this to the top of `index.js`:
```javascript
require('dotenv').config();
```

## Running the Server

### Development Mode (with auto-restart on Node 18+)
```bash
npm run dev
```

### Production Mode
```bash
npm start
```

The server will start on the configured port (default: 3000).

## API Endpoints

### Health Check
```
GET /health
```

Response:
```json
{
  "status": "ok",
  "timestamp": "2026-01-20T17:43:19.655Z",
  "apiKeyConfigured": true
}
```

### Re-parse Endpoint
```
POST /reparse
Content-Type: application/json
```

Request body:
```json
{
  "text": "1.A2.B3.C",
  "questionNumbers": [1, 2, 3, 4, 5]
}
```

Response:
```json
{
  "answers": {
    "1": "A",
    "2": "B",
    "3": "C"
  }
}
```

**Fields:**
- `text` (required): Raw OCR text to re-parse
- `questionNumbers` (optional): Array of expected question numbers for context

## Configuring the Android App

To connect the Iskorly Android app to this server:

1. Edit `local.properties` in the Android project root:
```properties
REPARSE_ENDPOINT=http://localhost:3000/reparse
```

Or for a remote server:
```properties
REPARSE_ENDPOINT=https://your-server.com/reparse
```

2. Rebuild the app - the endpoint URL will be injected into BuildConfig

3. The app will automatically call this endpoint when:
   - OCR fill ratio < 50% (configurable via REPARSE_MIN_FILLED_THRESHOLD)
   - REPARSE_ENDPOINT is configured

## Rate Limiting

The server includes simple in-memory rate limiting:
- 30 requests per minute per IP address
- Configurable in `index.js` (RATE_LIMIT_MAX_REQUESTS, RATE_LIMIT_WINDOW_MS)

## Security Notes

- No authentication is implemented (suitable for local/trusted environments)
- For production deployment, consider adding:
  - API key authentication
  - HTTPS/TLS
  - More sophisticated rate limiting (e.g., Redis-based)
  - Input sanitization and validation
- The server does **not** receive images or personally identifiable information (PII)
- Only raw OCR text is sent to the endpoint

## Deployment Options

### Local (Development/Testing)
Run on localhost as described above. Configure the app with:
```
REPARSE_ENDPOINT=http://localhost:3000/reparse
```

### Self-Hosted (Teachers)
Deploy to any Node.js hosting platform:
- Heroku
- Vercel
- Railway
- Google Cloud Run
- AWS Lambda + API Gateway

Example Docker deployment:
```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
EXPOSE 3000
CMD ["node", "index.js"]
```

### Cloud Functions
The code can be adapted for serverless deployment (AWS Lambda, Google Cloud Functions, etc.)

## Troubleshooting

### "OPENAI_API_KEY not configured" error
- Ensure the environment variable is set before starting the server
- Check for typos in the variable name
- Verify the API key is valid

### Rate limit errors
- Adjust RATE_LIMIT_MAX_REQUESTS in index.js
- Implement distributed rate limiting for multi-instance deployments

### Connection refused from Android app
- Ensure the server is running
- Check the endpoint URL in local.properties
- For localhost on Android emulator, use `http://10.0.2.2:3000/reparse`
- For localhost on physical device, use your computer's IP address

### OpenAI API errors
- Check your API key has sufficient credits
- Verify network connectivity
- Check OpenAI API status (status.openai.com)

## Cost Considerations

Using OpenAI's gpt-4o-mini model:
- Approximately $0.00015 per request (varies by text length)
- 100 re-parse requests ≈ $0.015
- Monitor usage via OpenAI dashboard

For cost optimization:
- Only enable for low-confidence results (already implemented)
- Consider caching common patterns
- Use the cheapest suitable model

## Monitoring

Add logging to track:
- Request counts
- Parse success rates
- Average response times
- Error rates

Example with Morgan (HTTP request logger):
```bash
npm install morgan
```

Add to index.js:
```javascript
const morgan = require('morgan');
app.use(morgan('combined'));
```

## License

Same as the main Iskorly project.
