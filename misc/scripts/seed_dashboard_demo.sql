-- ============================================================
-- CineMatch · Datos demo para los dashboards
-- ============================================================
-- ADVERTENCIA: este script BORRA los datos transaccionales locales
-- (salas, evaluaciones, matches, calificaciones, mensajes, filtros,
-- estados de conexión y simulaciones) y los reemplaza por 90 días
-- de actividad sintética.
--
-- No modifica usuarios, contenidos, plataformas ni administradores.
-- Los usuarios test1@cinematch.com y test2@cinematch.com no participan
-- en los datos sembrados para no agotar el catálogo de las pruebas E2E.
--
-- Uso:
--   psql -h localhost -p 5433 -U postgres -d cinematch -f seed_dashboard_demo.sql
-- ============================================================

\set ON_ERROR_STOP on

BEGIN;

-- 1. Limpiar solo datos transaccionales
TRUNCATE TABLE
    estados_conexion,
    test_simulaciones,
    mensajes,
    filtros,
    calificaciones,
    matches,
    evaluaciones,
    salas
RESTART IDENTITY CASCADE;

-- 2. Crear usuarios demo (idempotente)
DO $$
DECLARE
    hash_password text;
BEGIN
    SELECT password INTO hash_password
    FROM usuarios
    WHERE correo = 'test1@cinematch.com'
    LIMIT 1;

    IF hash_password IS NULL THEN
        RAISE EXCEPTION 'No existe test1@cinematch.com; ejecuta seed_usuarios_robot.sql primero';
    END IF;

    INSERT INTO usuarios (nombre, correo, password, rol, descripcion)
    SELECT
        'Demo ' || lpad(i::text, 2, '0'),
        'demo' || lpad(i::text, 2, '0') || '@cinematch.com',
        hash_password,
        'USUARIO',
        'Usuario de datos demo para dashboards'
    FROM generate_series(1, 12) AS i
    ON CONFLICT (correo) DO NOTHING;
END $$;

-- 3. Sembrar 90 días de actividad
DO $$
DECLARE
    dias              constant int := 90;
    v_indice          int;
    v_fecha           date;
    v_dia_semana      int;
    v_salas_dia       int;
    v_sala            int;
    v_sala_id         bigint;
    v_usuario1_id     bigint;
    v_usuario2_id     bigint;
    v_usuarios        bigint[];
    v_contenidos      text[];
    v_contenido       text;
    v_estado          text;
    v_sorteo          double precision;
    v_satisfaccion    double precision;
    v_nota            int;
    v_hora            timestamp;
    v_mensajes        int;
    v_mensaje         int;
    v_evaluaciones    int;
    v_evaluacion      int;
    v_plataforma      text;
BEGIN
    PERFORM setseed(0.4242);

    SELECT array_agg(id ORDER BY id)
      INTO v_usuarios
      FROM usuarios
     WHERE correo LIKE 'demo%@cinematch.com';

    SELECT array_agg(contenido_id ORDER BY contenido_id)
      INTO v_contenidos
      FROM contenidos;

    IF array_length(v_usuarios, 1) < 2 THEN
        RAISE EXCEPTION 'Se necesitan al menos 2 usuarios demo';
    END IF;

    IF array_length(v_contenidos, 1) < 20 THEN
        RAISE EXCEPTION 'Se necesitan al menos 20 contenidos cargados';
    END IF;

    SELECT nombre INTO v_plataforma FROM plataformas ORDER BY id LIMIT 1;

    FOR v_indice IN 0..dias - 1 LOOP
        v_fecha := current_date - (dias - 1 - v_indice);
        v_dia_semana := extract(dow FROM v_fecha);

        -- Crecimiento gradual + refuerzo de viernes/sábado + ruido
        v_salas_dia := 7
            + floor(v_indice * 8.0 / dias)::int
            + CASE WHEN v_dia_semana IN (5, 6) THEN 4
                   WHEN v_dia_semana IN (0, 4) THEN 2
                   ELSE 0 END
            + floor(random() * 4)::int;

        FOR v_sala IN 1..v_salas_dia LOOP
            -- Dos usuarios distintos
            v_usuario1_id := v_usuarios[1 + floor(random() * array_length(v_usuarios, 1))::int];
            LOOP
                v_usuario2_id := v_usuarios[1 + floor(random() * array_length(v_usuarios, 1))::int];
                EXIT WHEN v_usuario2_id <> v_usuario1_id;
            END LOOP;

            v_sorteo := random();
            v_estado := CASE
                WHEN v_sorteo < 0.60 THEN 'FINALIZADA'
                WHEN v_sorteo < 0.75 THEN 'ACTIVA'
                WHEN v_sorteo < 0.87 THEN 'CANCELADA'
                WHEN v_sorteo < 0.95 THEN 'ESPERANDO'
                ELSE 'CREADA'
            END;

            v_hora := v_fecha::timestamp
                + interval '8 hours'
                + random() * interval '13 hours';

            INSERT INTO salas (codigo, estado, fecha_creacion, usuario1_id, usuario2_id)
            VALUES (
                'DASH-' || to_char(v_fecha, 'YYYYMMDD') || '-' || lpad(v_sala::text, 3, '0'),
                v_estado,
                v_hora,
                v_usuario1_id,
                CASE WHEN v_estado IN ('CREADA', 'ESPERANDO') AND random() < 0.5
                     THEN NULL ELSE v_usuario2_id END
            )
            RETURNING id INTO v_sala_id;

            -- Filtros en ~40 % de las salas
            IF random() < 0.40 THEN
                INSERT INTO filtros (sala_id, tipo, genero, plataforma, anio, fecha_inicio, fecha_fin)
                VALUES (
                    v_sala_id,
                    CASE WHEN random() < 0.5 THEN 'PELICULA' ELSE 'SERIE' END,
                    (ARRAY['ACCION', 'COMEDIA', 'DRAMA', 'CIENCIA_FICCION', 'THRILLER', 'ANIMACION', 'ROMANCE'])[1 + floor(random() * 7)::int],
                    v_plataforma,
                    1990 + floor(random() * 37)::int,
                    v_hora,
                    v_hora + interval '2 hours'
                )
                ON CONFLICT (sala_id) DO NOTHING;
            END IF;

            -- Evaluaciones coherentes: mismos contenidos para ambos usuarios
            v_evaluaciones := 3 + floor(random() * 4)::int;
            FOR v_evaluacion IN 1..v_evaluaciones LOOP
                v_contenido := v_contenidos[1 + ((v_sala_id * 31 + v_evaluacion * 37) % array_length(v_contenidos, 1))::int];

                INSERT INTO evaluaciones (decision, contenido_id, sala_id, usuario_id)
                VALUES (random() < 0.58, v_contenido, v_sala_id, v_usuario1_id);

                INSERT INTO evaluaciones (decision, contenido_id, sala_id, usuario_id)
                VALUES (random() < 0.58, v_contenido, v_sala_id, v_usuario2_id);
            END LOOP;

            IF v_estado = 'FINALIZADA' THEN
                -- 0/1 match por sala finalizada
                IF random() < (0.55 + 0.15 * v_indice::numeric / dias) THEN
                    v_contenido := v_contenidos[1 + ((v_sala_id * 17 + 11) % array_length(v_contenidos, 1))::int];
                    INSERT INTO matches (fecha_deteccion, contenido_id, sala_id)
                    VALUES (v_hora + random() * interval '2 hours', v_contenido, v_sala_id);
                END IF;

                -- ~65 % de las salas finalizadas deja calificación
                IF random() < 0.65 THEN
                    v_satisfaccion := 0.58 + 0.16 * v_indice::numeric / dias;
                    v_sorteo := random();

                    IF v_sorteo < v_satisfaccion THEN
                        v_nota := 8 + floor(random() * 3)::int;         -- 8–10
                    ELSIF v_sorteo < v_satisfaccion + (1 - v_satisfaccion) * 0.75 THEN
                        v_nota := 6 + floor(random() * 2)::int;         -- 6–7
                    ELSE
                        v_nota := 1 + floor(random() * 5)::int;         -- 1–5
                    END IF;

                    INSERT INTO calificaciones (fecha, valor, sala_id, usuario_id)
                    VALUES (v_hora + interval '2 hours', v_nota, v_sala_id, v_usuario1_id);
                END IF;
            END IF;

            -- Mensajes de ejemplo
            v_mensajes := floor(random() * 5)::int;
            FOR v_mensaje IN 1..v_mensajes LOOP
                INSERT INTO mensajes (fecha_envio, texto, sala_id, usuario_id)
                VALUES (
                    v_hora + interval '1 minute' * v_mensaje,
                    (ARRAY[
                        '¿Qué te parece esta película?',
                        'Esa serie la tengo pendiente',
                        'Vamos con algo de acción',
                        'Me gustó mucho la última',
                        '¿La vemos el fin de semana?',
                        'Yo prefiero una comedia'
                    ])[1 + floor(random() * 6)::int],
                    v_sala_id,
                    CASE WHEN random() < 0.5 THEN v_usuario1_id ELSE v_usuario2_id END
                );
            END LOOP;
        END LOOP;
    END LOOP;
END $$;

COMMIT;

-- 4. Resumen de verificación
SELECT 'salas' AS tabla, count(*) FROM salas
UNION ALL SELECT 'matches', count(*) FROM matches
UNION ALL SELECT 'calificaciones', count(*) FROM calificaciones
UNION ALL SELECT 'evaluaciones', count(*) FROM evaluaciones
UNION ALL SELECT 'mensajes', count(*) FROM mensajes
UNION ALL SELECT 'filtros', count(*) FROM filtros;

SELECT min(fecha_creacion)::date AS desde,
       max(fecha_creacion)::date AS hasta,
       count(DISTINCT fecha_creacion::date) AS dias,
       round(avg(valor), 2) AS promedio_calificacion
FROM salas
LEFT JOIN calificaciones ON calificaciones.sala_id = salas.id;
