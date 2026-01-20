// Vercel Serverless Function
// POST /api/reparse  { text: string } -> { answers: { "1": "A", "2": "Apple", ... } }
export default async function handler(req, res) {
  if (req.method !== 'POST') return res.status(405).json({ error: 'method_not_allowed' });

  try {
    const { text } = req.body || {};
    if (!text || typeof text !== 'string') return res.status(400).json({ error: 'missing_text' });

    const openaiKey = process.env.OPENAI_API_KEY;
    if (!openaiKey) return res.status(500).json({ error: 'server_not_configured' });

    // Light pre-clean to help the model
    const cleaned = text
      .replace(/\u00a0/g, ' ')
      .replace(/[^\S\r\n]+/g, ' ')
      .trim()
      .slice(0, 8000); // guardrail

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

    // Minimal call to OpenAI (compatible with fetch API)
    const resp = await fetch('https://api.openai.com/v1/chat/completions', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${openaiKey}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        model: 'gpt-4o-mini',
        messages: [
          { role: 'system', content: 'You only output valid JSON. No prose.' },
          { role: 'user', content: prompt }
        ],
        temperature: 0.1
      })
    });

    if (!resp.ok) {
      const errTxt = await resp.text().catch(() => '');
      return res.status(502).json({ error: 'llm_failed', details: errTxt.slice(0, 500) });
    }

    const data = await resp.json();
    const raw = data?.choices?.[0]?.message?.content || '{}';

    let json;
    try {
      json = JSON.parse(raw);
    } catch {
      // Fallback: try to salvage a JSON object substring
      const match = raw.match(/\{[\s\S]*\}$/);
      json = match ? JSON.parse(match[0]) : { answers: {} };
    }

    // Normalize: ensure answers is an object of string keys to string values
    const out = { answers: {} };
    const ans = json && typeof json === 'object' ? json.answers : null;
    if (ans && typeof ans === 'object') {
      for (const [k, v] of Object.entries(ans)) {
        if (!k) continue;
        const kk = String(k).trim();
        if (!/^\d+$/.test(kk)) continue;
        const val = typeof v === 'string' ? v.trim() : '';
        if (!val) continue;
        // Normalize single letters
        const normalized = (val.length === 1 && /[A-Za-z]/.test(val)) ? val.toUpperCase() : val.replace(/[.,;!?]+$/,'').slice(0, 40);
        out.answers[kk] = normalized;
      }
    }

    // CORS (optional)
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
    res.status(200).json(out);
  } catch (e) {
    console.error(e);
    res.status(500).json({ error: 'server_error' });
  }
}

export const config = {
  api: { bodyParser: { sizeLimit: '2mb' } } // allow big texts
};