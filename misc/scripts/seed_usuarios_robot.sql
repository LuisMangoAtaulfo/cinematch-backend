-- Script para sembrar usuarios de prueba para Robot Framework
-- Usuarios:
--   test1@cinematch.com / Test1234
--   test2@cinematch.com / Test1234
--   admin@cinematch.com / Admin1234
--
-- Uso: PGPASSWORD=1234 psql -h localhost -p 5433 -U postgres -d cinematch -f seed_usuarios_robot.sql

-- Usuario test1
INSERT INTO usuarios (correo, password, nombre, descripcion, rol)
VALUES ('test1@cinematch.com', '$2a$12$R.VBML2hJcq8mvKNXb/J6uUFpEA3iETKoCZM9k.p.hZ6pwcUK5SFq', 'Usuario Test 1', NULL, 'USUARIO')
ON CONFLICT (correo) DO NOTHING;

-- Usuario test2
INSERT INTO usuarios (correo, password, nombre, descripcion, rol)
VALUES ('test2@cinematch.com', '$2a$12$R.VBML2hJcq8mvKNXb/J6uUFpEA3iETKoCZM9k.p.hZ6pwcUK5SFq', 'Usuario Test 2', NULL, 'USUARIO')
ON CONFLICT (correo) DO NOTHING;

-- Admin
INSERT INTO administradores (correo, password, rol)
VALUES ('admin@cinematch.com', '$2a$12$x4R2X3qwin6KSPZCOCW7geMRi6AKi9ALRtk3d9DgKkA91H0RvSUXi', 'ADMINISTRADOR')
ON CONFLICT (correo) DO NOTHING;

-- Verificar
SELECT 'Usuarios de prueba creados:' AS mensaje;
SELECT correo, nombre, rol FROM usuarios WHERE correo LIKE '%@cinematch.com';
SELECT correo, rol FROM administradores WHERE correo = 'admin@cinematch.com';
