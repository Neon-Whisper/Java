curl http://117.72.115.188:11434/api/generate \
  -H "Content-Type: application/json" \
  -d '{
        "model": "qwen2.5:7b",
        "prompt": "1+1",
        "stream": false
      }'