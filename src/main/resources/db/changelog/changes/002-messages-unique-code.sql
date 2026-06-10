ALTER TABLE messages ADD COLUMN IF NOT EXISTS unique_code VARCHAR(64);

UPDATE messages
SET unique_code = gen_random_uuid()::text
WHERE unique_code IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_messages_sender_unique_code
    ON messages (sender_user_id, unique_code);
