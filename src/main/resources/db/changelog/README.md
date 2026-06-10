# Liquibase

## Структура

- `db.changelog-master.xml` — changelog (XML)
- `changes/001-schema-init.sql` — полный дамп схемы с dev (`pg_dump --schema-only`), **без** `messages.unique_code`
- `changes/002-messages-unique-code.sql` — колонка, backfill UUID, unique index (отдельно для prod)

## Prod (БД уже есть)

| Changeset | Условие | Действие |
|-----------|---------|----------|
| `schema-init` | таблица `users` есть | MARK_RAN |
| `messages-unique-code` | колонки `unique_code` нет | выполняется миграция |

## Пустая БД

1. `schema-init` — создаёт всю схему
2. `messages-unique-code` — добавляет `unique_code` и индекс

## Обновление schema-init

Переснять дамп с dev, **убрать** из `001-schema-init.sql` блоки `unique_code` и `uk_messages_sender_unique_code` — они живут в `002-messages-unique-code.sql`.
