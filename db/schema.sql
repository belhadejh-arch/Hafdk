CREATE TABLE IF NOT EXISTS users (
  id TEXT PRIMARY KEY,
  username VARCHAR(32) NOT NULL UNIQUE,
  email VARCHAR(254) NOT NULL UNIQUE,
  password_hash TEXT NOT NULL,
  role VARCHAR(10) NOT NULL DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),
  phone VARCHAR(40) NOT NULL DEFAULT '',
  full_name VARCHAR(120) NOT NULL DEFAULT '',
  beneficiary_type VARCHAR(40) NOT NULL DEFAULT 'لنفسي',
  subscription_plan VARCHAR(20) NOT NULL DEFAULT 'MONTHLY 6',
  status VARCHAR(12) NOT NULL DEFAULT 'INACTIVE',
  registration_date DATE NOT NULL DEFAULT CURRENT_DATE,
  start_date DATE,
  end_date DATE,
  license_key VARCHAR(64),
  receipt_file_name VARCHAR(180),
  receipt_content BYTEA,
  receipt_content_type VARCHAR(80),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM pg_constraint
    WHERE conrelid = 'users'::regclass
      AND conname = 'users_status_check'
      AND pg_get_constraintdef(oid) LIKE '%BANNED%'
  ) THEN
    ALTER TABLE users DROP CONSTRAINT IF EXISTS users_status_check;
    ALTER TABLE users
      ADD CONSTRAINT users_status_check
      CHECK (status IN ('INACTIVE', 'PENDING', 'ACTIVE', 'SUSPENDED', 'BANNED'));
  END IF;
END $$;

ALTER TABLE users ADD COLUMN IF NOT EXISTS receipt_content BYTEA;
ALTER TABLE users ADD COLUMN IF NOT EXISTS receipt_content_type VARCHAR(80);

CREATE INDEX IF NOT EXISTS users_role_created_at_idx ON users (role, created_at DESC);