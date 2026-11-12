-- Script para sembrar plataformas iniciales para los tests de Robot Framework
-- Uso: PGPASSWORD=1234 psql -h localhost -p 5433 -U postgres -d cinematch -f seed_plataformas_robot.sql

INSERT INTO plataformas (nombre, habilitada)
VALUES 
    ('Netflix', TRUE),
    ('Disney+', TRUE),
    ('HBO Max', TRUE),
    ('Amazon Prime Video', TRUE),
    ('Apple TV+', TRUE),
    ('Paramount+', TRUE),
    ('Hulu', TRUE),
    ('Crunchyroll', TRUE)
ON CONFLICT (nombre) DO NOTHING;

-- Verificar
SELECT 'Plataformas creadas:' AS mensaje;
SELECT nombre, habilitada FROM plataformas WHERE habilitada = TRUE ORDER BY nombre;
