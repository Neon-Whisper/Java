curl http://58a39caa684c41bf9bed-deepseek-r1-llm-api.gcs-xy1a.jdcloud.com/api/generate \
  -H "Content-Type: application/json" \
  -d '{
        "model": "qwen2.5:7b",
        "prompt": "1+1",
        "stream": false
      }'