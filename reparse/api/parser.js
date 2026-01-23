// Vercel Serverless Function
// POST /api/reparse  { text: string } -> { answers: { "1": "A", "2": "Apple", ... } }
export default async function handler(req, res) {
  if (req.method === 'OPTIONS') {
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type, x-app-key');
    res.status(204).end();
    return;
  }
  if (req.method !== 'POST') return res.status(405).json({ error: 'method_not_allowed' });

  try {
    const openaiKey = process.env.OPENAI_API_KEY;
    if (!openaiKey) return res.status(500).json({ error: 'server_not_configured' });

    // Optional simple shared key (set APP_MOBILE_KEY in Vercel; send x-app-key header from app)
    const appKey = process.env.APP_MOBILE_KEY || '';
    if (appKey && req.headers['x-app-key'] !== appKey) {
      return res.status(401).json({ error: 'unauthorized' });
    }

    const { text } = req.body || {};
    if (!text || typeof text !== 'string') return res.status(400).json({ error: 'missing_text' });

    const cleaned = text.replace(/\u00a0/g, ' ').replace(/[^\S\r\n]+/g, ' ').trim().slice(0, 8000);

    const prompt = `
You are a strict JSON generator. Extract answers from OCR text that may look like:
1. Apple
2) A
3 - B
Ignore the leading numbering and map to question numbers.

Rules:
- Output ONLY JSON with shape: {"answers":{"1":"...", "2":"..."}}
- Accept single letters (A..Z) and short text (words/numbers).
- Do not invent questions; if unsure, omit.
- Preserve case for words; uppercase single letters.
- If lines have no numbers, map in order they appear starting from 1.

OCR TEXT:
${cleaned}
`.trim();

    const resp = await fetch('https://api.openai.com/v1/chat/completions', {
      method: 'POST',
      headers: { Authorization: `Bearer ${openaiKey}`, 'Content-Type': 'application/json' },
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
      const match = raw.match(/\{[\s\S]*\}$/);
      json = match ? JSON.parse(match[0]) : { answers: {} };
    }

    const out = { answers: {} };
    const ans = json && typeof json === 'object' ? json.answers : null;
    if (ans && typeof ans === 'object') {
      for (const [k, v] of Object.entries(ans)) {
        const kk = String(k || '').trim();
        if (!/^\d+$/.test(kk)) continue;
        const val = typeof v === 'string' ? v.trim() : '';
        if (!val) continue;
        const normalized = (val.length === 1 && /[A-Za-z]/.test(val)) ? val.toUpperCase()
                          : val.replace(/[.,;!?]+$/,'').slice(0, 40);
        out.answers[kk] = normalized;
      }
    }

    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type, x-app-key');
    return res.status(200).json(out);
  } catch (e) {
    console.error(e);
    return res.status(500).json({ error: 'server_error' });
  }
}