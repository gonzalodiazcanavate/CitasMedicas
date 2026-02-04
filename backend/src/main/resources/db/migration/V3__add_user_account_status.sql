-- ===============================
-- ADD USER ACCOUNT STATUS COLUMNS
-- ===============================

ALTER TABLE users
ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT TRUE,
ADD COLUMN account_locked BOOLEAN NOT NULL DEFAULT FALSE;

-- Crear índice para búsquedas de usuarios activos
CREATE INDEX idx_users_enabled_locked ON users(enabled, account_locked);
