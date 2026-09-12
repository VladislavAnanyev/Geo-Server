# Образ Ollama с предзагруженной моделью для стенда/prod (без ручного ollama pull).
#
# Сборка и публикация (пример для Yandex Container Registry):
#   docker build -f docker/ollama/Dockerfile -t cr.yandex/<registry>/meetings-ollama:qwen2.5-3b-instruct .
#   docker push cr.yandex/<registry>/meetings-ollama:qwen2.5-3b-instruct
#
# На стенде в compose задайте OLLAMA_IMAGE на этот тег.

FROM ollama/ollama:latest

ARG OLLAMA_MODEL=qwen2.5:3b-instruct
ENV OLLAMA_MODEL=${OLLAMA_MODEL}

COPY docker/ollama/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Модель кладётся в /root/.ollama внутри образа (~1 GiB слой)
RUN /bin/sh -c '\
    ollama serve & \
    SERVER_PID=$!; \
    i=0; \
    while [ "$i" -lt 120 ]; do \
      if ollama list >/dev/null 2>&1; then break; fi; \
      i=$((i + 1)); sleep 1; \
    done; \
    ollama pull "${OLLAMA_MODEL}"; \
    kill "${SERVER_PID}" 2>/dev/null || true; \
    wait "${SERVER_PID}" 2>/dev/null || true \
    '

ENTRYPOINT ["/entrypoint.sh"]
