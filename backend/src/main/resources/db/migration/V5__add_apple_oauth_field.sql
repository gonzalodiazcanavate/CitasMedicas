-- V5: Añadir campo apple_id para Sign in with Apple
ALTER TABLE users ADD COLUMN apple_id VARCHAR(255) UNIQUE;

-- Crear índice para búsquedas por apple_id
CREATE INDEX idx_users_apple_id ON users(apple_id);
