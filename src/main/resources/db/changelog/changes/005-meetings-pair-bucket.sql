ALTER TABLE meetings ADD COLUMN IF NOT EXISTS bucket VARCHAR(64);

ALTER TABLE meetings ADD CONSTRAINT uk_meetings_pair_bucket UNIQUE (first_user_id, second_user_id, bucket);
