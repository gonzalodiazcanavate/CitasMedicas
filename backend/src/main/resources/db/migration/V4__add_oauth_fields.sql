-- Añadir campos para OAuth2 (Google Login)
ALTER TABLE users ADD COLUMN google_id VARCHAR(255) UNIQUE;
ALTER TABLE users ADD COLUMN auth_provider VARCHAR(50) NOT NULL DEFAULT 'LOCAL';

-- Hacer password nullable para usuarios OAuth (no tienen password local)
ALTER TABLE users ALTER COLUMN password DROP NOT NULL;

-- Índice para búsquedas rápidas por google_id
CREATE INDEX idx_users_google_id ON users(google_id);
