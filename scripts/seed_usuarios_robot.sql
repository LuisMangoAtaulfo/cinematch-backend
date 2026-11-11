-- ══════════════════════════════════════════════════════════════
-- SEED USUARIOS DE PRUEBA (R-BE-01)
-- ══════════════════════════════════════════════════════════════
-- Crea los usuarios de prueba necesarios para Robot Framework.
-- Password: "test1234" (BCrypt hash)
--
-- Uso:
--   psql -h localhost -p 5433 -U postgres -d cinematch -f scripts/seed_usuarios_robot.sql
-- ══════════════════════════════════════════════════════════════

BEGIN;

INSERT INTO usuarios (nombre, correo, password, rol, descripcion) VALUES
    ('Usuario Test 1', 'test1@cinematch.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USUARIO', 'Usuario de prueba 1 para Robot Framework'),
    ('Usuario Test 2', 'test2@cinematch.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USUARIO', 'Usuario de prueba 2 para Robot Framework'),
    ('Admin Test',     'admin@cinematch.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMINISTRADOR', 'Administrador de prueba para Robot Framework')
ON CONFLICT (correo) DO UPDATE SET
    nombre = EXCLUDED.nombre,
    rol    = EXCLUDED.rol;

-- También crear en la tabla de administradores (para loginAdmin)
INSERT INTO administradores (correo, password, rol) VALUES
    ('admin@cinematch.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMINISTRADOR')
ON CONFLICT (correo) DO UPDATE SET
    rol = EXCLUDED.rol;

COMMIT;

SELECT 'Usuarios de prueba creados' AS estado, COUNT(*) AS total FROM usuarios
WHERE correo IN ('test1@cinematch.com', 'test2@cinematch.com', 'admin@cinematch.com');
