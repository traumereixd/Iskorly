#!/bin/bash
# Manual test script for the AI re-parser server
# This demonstrates how the server works without requiring an actual OpenAI API key

echo "=== AI Re-parser Server Manual Test ==="
echo ""

# Test 1: Health check
echo "Test 1: Health Check"
echo "curl http://localhost:3000/health"
echo ""

# Test 2: Valid request with text
echo "Test 2: Valid request (will fail without real API key, but validates input handling)"
cat << 'EOF' > /tmp/test-request.json
{
  "text": "LYCEUM HIGH SCHOOL\nNAME: _____ SECTION: _____\n\n1. A\n2. B\n3. C\n4. True\n5. False",
  "questionNumbers": [1, 2, 3, 4, 5]
}
EOF
echo "curl -X POST http://localhost:3000/reparse -H 'Content-Type: application/json' -d @/tmp/test-request.json"
echo ""

# Test 3: Invalid request - missing text field
echo "Test 3: Invalid request - missing text field (should return 400)"
echo "curl -X POST http://localhost:3000/reparse -H 'Content-Type: application/json' -d '{}'"
echo ""

# Test 4: Invalid request - empty text
echo "Test 4: Invalid request - empty text (should return 400)"
echo "curl -X POST http://localhost:3000/reparse -H 'Content-Type: application/json' -d '{\"text\": \"\"}'"
echo ""

# Test 5: Compressed format
cat << 'EOF' > /tmp/test-compressed.json
{
  "text": "1.A2.B3.C4.D5.E",
  "questionNumbers": [1, 2, 3, 4, 5]
}
EOF
echo "Test 5: Compressed format"
echo "curl -X POST http://localhost:3000/reparse -H 'Content-Type: application/json' -d @/tmp/test-compressed.json"
echo ""

# Test 6: Answer-first format
cat << 'EOF' > /tmp/test-answer-first.json
{
  "text": "True 1.\nFalse 2.\nA 3.\nB 4.",
  "questionNumbers": [1, 2, 3, 4]
}
EOF
echo "Test 6: Answer-first format"
echo "curl -X POST http://localhost:3000/reparse -H 'Content-Type: application/json' -d @/tmp/test-answer-first.json"
echo ""

echo "=== How to Run Tests ==="
echo "1. Start the server: cd server/reparse && OPENAI_API_KEY=your-key npm start"
echo "2. In another terminal, run the curl commands above"
echo "3. For full testing with real API, set a valid OPENAI_API_KEY"
echo ""
echo "=== Expected API Response Format ==="
cat << 'EOF'
{
  "answers": {
    "1": "A",
    "2": "B",
    "3": "C",
    "4": "True",
    "5": "False"
  }
}
EOF
