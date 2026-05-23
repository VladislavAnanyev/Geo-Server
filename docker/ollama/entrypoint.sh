#!/bin/sh
set -e

MODEL="${OLLAMA_MODEL:-qwen2.5:3b-instruct}"

ollama serve &
OLLAMA_PID=$!

# Ждём готовности API
i=0
while [ "$i" -lt 60 ]; do
  if ollama list >/dev/null 2>&1; then
    break
  fi
  i=$((i + 1))
  sleep 1
done

if ! ollama list 2>/dev/null | grep -qF "${MODEL}"; then
  echo "Model ${MODEL} not found, pulling..."
  ollama pull "${MODEL}"
fi

kill "${OLLAMA_PID}" 2>/dev/null || true
wait "${OLLAMA_PID}" 2>/dev/null || true

exec ollama serve
