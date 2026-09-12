# Ollama для стенда (модель в образе)

## Зачем отдельный образ

`ollama/ollama:latest` не содержит моделей. Без `ollama pull` API отвечает `model ... not found`.

Свой образ решает это: модель скачивается **при сборке** в CI или на машине разработчика, пушится в registry, на стенде только `docker compose up`.

## Сборка и push

```bash
# из корня репозитория
docker build -f docker/ollama/Dockerfile \
  -t cr.yandex/<registry-id>/meetings-ollama:qwen2.5-3b-instruct .

docker push cr.yandex/<registry-id>/meetings-ollama:qwen2.5-3b-instruct
```

Пересобирайте образ при смене модели (`ARG OLLAMA_MODEL` / `OLLAMA_MODEL` в compose).

## Запуск на стенде

В `docker-compose.yml` (или override на сервере):

```yaml
environment:
  OLLAMA_IMAGE: cr.yandex/<registry-id>/meetings-ollama:qwen2.5-3b-instruct
```

**Не монтируйте** пустой volume на `/root/.ollama` — он перекроет модели из образа. В compose для `ollama` volume убран намеренно.

`entrypoint.sh` на старте проверяет наличие модели и делает `pull` только если её нет (запасной вариант, если случайно вернули volume).

## Локальная разработка

- Либо соберите тот же образ локально и укажите `OLLAMA_IMAGE`.
- Либо оставьте `ollama/ollama:latest` и один раз:  
  `docker compose exec ollama ollama pull qwen2.5:3b-instruct`  
  (с включённым volume данные сохранятся между перезапусками).
