-- ══════════════════════════════════════════════════════════════
-- SEED CATÁLOGO DE CONTENIDO COMPLETO PARA PRUEBAS (R-BE-04)
-- ══════════════════════════════════════════════════════════════
-- ≥ 30 contenidos, 3 tipos (PELICULA, SERIE, DOCUMENTAL),
-- 8+ géneros, 5+ plataformas, años 1980–2026.
-- Idempotente: ON CONFLICT evita duplicados.
--
-- Uso:
--   psql -h localhost -p 5433 -U postgres -d cinematch -f misc/scripts/seed_catalogo.sql
-- ══════════════════════════════════════════════════════════════

BEGIN;

-- ── PLATAFORMAS ──────────────────────────────────────────────
INSERT INTO plataformas (nombre, habilitada) VALUES
    ('Netflix',      TRUE),
    ('Disney+',      TRUE),
    ('HBO Max',      TRUE),
    ('Prime Video',  TRUE),
    ('Apple TV+',    TRUE),
    ('Paramount+',   TRUE)
ON CONFLICT (nombre) DO UPDATE SET habilitada = EXCLUDED.habilitada;

-- ── CONTENIDO ────────────────────────────────────────────────
-- 35 títulos: 20 películas, 8 series, 7 documentales

INSERT INTO contenidos (contenido_id, titulo, anio, tipo, genero, imagen, sinopsis) VALUES
    -- PELICULAS (20)
    ('movie_1',  'El Padrino',             1980, 'PELICULA', 'CRIMEN',          'https://image.tmdb.org/padrino.jpg',     'La historia de la familia Corleone, una de las más poderosas dinastías del crimen organizado de Nueva York.'),
    ('movie_2',  'Blade Runner',            1982, 'PELICULA', 'CIENCIA_FICCION', 'https://image.tmdb.org/blade.jpg',       'En un futuro distópico, un cazador de replicantes debe eliminar a cuatro androides que han robado una nave.'),
    ('movie_3',  'Die Hard',                1988, 'PELICULA', 'ACCION',          'https://image.tmdb.org/diehard.jpg',     'Un policía de Nueva York lucha contra un grupo de terroristas que han tomado un rascacielos de Los Ángeles.'),
    ('movie_4',  'El Silencio de los Inocentes', 1991, 'PELICULA', 'THRILLER',  'https://image.tmdb.org/silence.jpg',     'Una agente del FBI recurre a un psiquiatra encarcelado para atrapar a un asesino en serie.'),
    ('movie_5',  'Parque Jurásico',         1993, 'PELICULA', 'AVENTURA',        'https://image.tmdb.org/jurassic.jpg',    'Un parque temático con dinosaurios clonados se convierte en una pesadilla cuando las criaturas escapan.'),
    ('movie_6',  'El Rey León',             1994, 'PELICULA', 'ANIMACION',       'https://image.tmdb.org/lionking.jpg',    'Un cachorro de león huye tras la muerte de su padre y debe regresar para reclamar su trono.'),
    ('movie_7',  'Titanic',                 1997, 'PELICULA', 'DRAMA',           'https://image.tmdb.org/titanic.jpg',     'Un joven artista y una aristócrata se enamoran a bordo del RMS Titanic en su viaje inaugural.'),
    ('movie_8',  'The Matrix',              1999, 'PELICULA', 'CIENCIA_FICCION', 'https://image.tmdb.org/matrix.jpg',      'Un hacker descubre que la realidad es una simulación creada por máquinas para esclavizar a la humanidad.'),
    ('movie_9',  'El Señor de los Anillos: La Comunidad del Anillo', 2001, 'PELICULA', 'FANTASIA', 'https://image.tmdb.org/lotr.jpg', 'Un hobbit emprende un viaje para destruir un anillo mágico y salvar la Tierra Media.'),
    ('movie_10', 'Harry Potter y la Piedra Filosofal', 2001, 'PELICULA', 'FANTASIA', 'https://image.tmdb.org/hp1.jpg', 'Un niño descubre que es mago y comienza sus estudios en Hogwarts.'),
    ('movie_11', 'Batman Inicia',           2005, 'PELICULA', 'ACCION',          'https://image.tmdb.org/batman.jpg',      'Bruce Wayne regresa a Gotham para combatir el crimen como Batman.'),
    ('movie_12', 'El Laberinto del Fauno',  2006, 'PELICULA', 'FANTASIA',        'https://image.tmdb.org/fauno.jpg',       'Una niña descubre un mundo de fantasía en los bosques de la España de posguerra.'),
    ('movie_13', 'Inception',               2010, 'PELICULA', 'CIENCIA_FICCION', 'https://image.tmdb.org/inception.jpg',   'Un ladrón que roba secretos corporativos a través de los sueños recibe la tarea de implantar una idea.'),
    ('movie_14', 'La La Land',              2016, 'PELICULA', 'ROMANCE',         'https://image.tmdb.org/lalaland.jpg',    'Una aspirante a actriz y un músico de jazz se enamoran en Los Ángeles.'),
    ('movie_15', 'Get Out',                 2017, 'PELICULA', 'HORROR',          'https://image.tmdb.org/getout.jpg',      'Un joven afroamericano descubre perturbadores secretos al visitar a la familia de su novia blanca.'),
    ('movie_16', 'Parasite',                2019, 'PELICULA', 'THRILLER',        'https://image.tmdb.org/parasite.jpg',    'Una familia pobre se infiltra en la vida de una familia rica con consecuencias inesperadas.'),
    ('movie_17', 'Dune',                    2021, 'PELICULA', 'CIENCIA_FICCION', 'https://image.tmdb.org/dune.jpg',        'Un joven noble debe viajar al planeta más peligroso del universo para asegurar el futuro de su familia.'),
    ('movie_18', 'Todo en Todas Partes al Instante', 2022, 'PELICULA', 'ACCION', 'https://image.tmdb.org/eeaao.jpg',      'Una mujer descubre que puede acceder a versiones de sí misma de universos paralelos.'),
    ('movie_19', 'Oppenheimer',             2023, 'PELICULA', 'DRAMA',           'https://image.tmdb.org/oppenheimer.jpg',  'La historia del físico que lideró el desarrollo de la bomba atómica.'),
    ('movie_20', 'Un Buen Día para Morir',  2025, 'PELICULA', 'WESTERN',         'https://image.tmdb.org/western.jpg',     'Un vaquero retirado debe enfrentarse a su pasado cuando un forajido llega al pueblo.'),

    -- SERIES (8)
    ('tv_1',    'Breaking Bad',             2008, 'SERIE',    'CRIMEN',          'https://image.tmdb.org/bb.jpg',          'Un profesor de química diagnosticado con cáncer se convierte en fabricante de metanfetamina.'),
    ('tv_2',    'Game of Thrones',          2011, 'SERIE',    'FANTASIA',        'https://image.tmdb.org/got.jpg',         'Nobles familias luchan por el control del Trono de Hierro en los Siete Reinos.'),
    ('tv_3',    'Stranger Things',          2016, 'SERIE',    'CIENCIA_FICCION', 'https://image.tmdb.org/st.jpg',          'Un grupo de niños descubre fuerzas sobrenaturales y un laboratorio secreto en su pueblo.'),
    ('tv_4',    'The Mandalorian',          2019, 'SERIE',    'ACCION',          'https://image.tmdb.org/mando.jpg',       'Un cazarrecompensas viaja por los confines de la galaxia con un niño misterioso.'),
    ('tv_5',    'The Bear',                 2022, 'SERIE',    'COMEDIA',         'https://image.tmdb.org/bear.jpg',        'Un chef de alta cocina regresa a Chicago para dirigir el restaurante de su familia.'),
    ('tv_6',    'The Last of Us',           2023, 'SERIE',    'DRAMA',           'https://image.tmdb.org/tlou.jpg',        'Un sobreviviente escolta a una joven a través de un Estados Unidos postapocalíptico.'),
    ('tv_7',    'Succession',               2018, 'SERIE',    'DRAMA',           'https://image.tmdb.org/succession.jpg',  'Los hijos de un magnate de medios luchan por el control de la empresa familiar.'),
    ('tv_8',    'Wednesday',                2022, 'SERIE',    'COMEDIA',         'https://image.tmdb.org/wednesday.jpg',   'La hija de la familia Addams investiga una serie de asesinatos en su nueva academia.'),

    -- DOCUMENTALES (7)
    ('doc_1',   'Planeta Tierra',           2006, 'DOCUMENTAL', 'DOCUMENTAL',    'https://image.tmdb.org/planeta.jpg',     'Un recorrido por los ecosistemas más extraordinarios del planeta.'),
    ('doc_2',   'Cosmos: Un Viaje Personal', 2014, 'DOCUMENTAL', 'DOCUMENTAL',   'https://image.tmdb.org/cosmos.jpg',      'Un viaje por el universo de la mano de Neil deGrasse Tyson.'),
    ('doc_3',   'Nuestro Planeta',          2019, 'DOCUMENTAL', 'DOCUMENTAL',    'https://image.tmdb.org/nuestro.jpg',     'Documental sobre la belleza natural y el impacto del cambio climático.'),
    ('doc_4',  'The Social Dilemma',        2020, 'DOCUMENTAL', 'DOCUMENTAL',    'https://image.tmdb.org/social.jpg',      'Expertos en tecnología revelan los peligros de las redes sociales.'),
    ('doc_5',  'Mi Octavo Maestro',         2021, 'DOCUMENTAL', 'DOCUMENTAL',    'https://image.tmdb.org/octavo.jpg',      'La historia de un maestro que transforma la vida de sus alumnos.'),
    ('doc_6',  '14 de Noviembre',           2024, 'DOCUMENTAL', 'DOCUMENTAL',    'https://image.tmdb.org/14nov.jpg',       'Investigación sobre los eventos del 14 de noviembre.'),
    ('doc_7',  'Vida Salvaje',              2025, 'DOCUMENTAL', 'DOCUMENTAL',    'https://image.tmdb.org/salvaje.jpg',     'Exploración de la fauna más extraordinaria del planeta.')
ON CONFLICT (contenido_id) DO UPDATE SET
    titulo   = EXCLUDED.titulo,
    anio     = EXCLUDED.anio,
    tipo     = EXCLUDED.tipo,
    genero   = EXCLUDED.genero,
    imagen   = EXCLUDED.imagen,
    sinopsis = EXCLUDED.sinopsis;

-- ── CONTENIDO ↔ PLATAFORMAS ─────────────────────────────────
DELETE FROM contenido_plataforma;

INSERT INTO contenido_plataforma (contenido_id, plataforma_id) VALUES
    -- Netflix
    ('movie_5',  (SELECT id FROM plataformas WHERE nombre = 'Netflix')),
    ('movie_8',  (SELECT id FROM plataformas WHERE nombre = 'Netflix')),
    ('movie_14', (SELECT id FROM plataformas WHERE nombre = 'Netflix')),
    ('movie_16', (SELECT id FROM plataformas WHERE nombre = 'Netflix')),
    ('movie_18', (SELECT id FROM plataformas WHERE nombre = 'Netflix')),
    ('tv_1',     (SELECT id FROM plataformas WHERE nombre = 'Netflix')),
    ('tv_3',     (SELECT id FROM plataformas WHERE nombre = 'Netflix')),
    ('tv_5',     (SELECT id FROM plataformas WHERE nombre = 'Netflix')),
    ('tv_8',     (SELECT id FROM plataformas WHERE nombre = 'Netflix')),
    ('doc_4',    (SELECT id FROM plataformas WHERE nombre = 'Netflix')),
    ('doc_5',    (SELECT id FROM plataformas WHERE nombre = 'Netflix')),
    -- Disney+
    ('movie_6',  (SELECT id FROM plataformas WHERE nombre = 'Disney+')),
    ('movie_9',  (SELECT id FROM plataformas WHERE nombre = 'Disney+')),
    ('movie_10', (SELECT id FROM plataformas WHERE nombre = 'Disney+')),
    ('tv_4',     (SELECT id FROM plataformas WHERE nombre = 'Disney+')),
    ('doc_1',    (SELECT id FROM plataformas WHERE nombre = 'Disney+')),
    ('doc_3',    (SELECT id FROM plataformas WHERE nombre = 'Disney+')),
    -- HBO Max
    ('movie_1',  (SELECT id FROM plataformas WHERE nombre = 'HBO Max')),
    ('movie_4',  (SELECT id FROM plataformas WHERE nombre = 'HBO Max')),
    ('movie_11', (SELECT id FROM plataformas WHERE nombre = 'HBO Max')),
    ('movie_12', (SELECT id FROM plataformas WHERE nombre = 'HBO Max')),
    ('movie_17', (SELECT id FROM plataformas WHERE nombre = 'HBO Max')),
    ('tv_2',     (SELECT id FROM plataformas WHERE nombre = 'HBO Max')),
    ('tv_6',     (SELECT id FROM plataformas WHERE nombre = 'HBO Max')),
    ('tv_7',     (SELECT id FROM plataformas WHERE nombre = 'HBO Max')),
    -- Prime Video
    ('movie_3',  (SELECT id FROM plataformas WHERE nombre = 'Prime Video')),
    ('movie_7',  (SELECT id FROM plataformas WHERE nombre = 'Prime Video')),
    ('movie_13', (SELECT id FROM plataformas WHERE nombre = 'Prime Video')),
    ('movie_15', (SELECT id FROM plataformas WHERE nombre = 'Prime Video')),
    ('movie_19', (SELECT id FROM plataformas WHERE nombre = 'Prime Video')),
    ('tv_1',     (SELECT id FROM plataformas WHERE nombre = 'Prime Video')),
    ('doc_2',    (SELECT id FROM plataformas WHERE nombre = 'Prime Video')),
    ('doc_6',    (SELECT id FROM plataformas WHERE nombre = 'Prime Video')),
    -- Apple TV+
    ('movie_17', (SELECT id FROM plataformas WHERE nombre = 'Apple TV+')),
    ('movie_19', (SELECT id FROM plataformas WHERE nombre = 'Apple TV+')),
    ('doc_7',    (SELECT id FROM plataformas WHERE nombre = 'Apple TV+')),
    -- Paramount+
    ('movie_2',  (SELECT id FROM plataformas WHERE nombre = 'Paramount+')),
    ('movie_5',  (SELECT id FROM plataformas WHERE nombre = 'Paramount+')),
    ('movie_20', (SELECT id FROM plataformas WHERE nombre = 'Paramount+'));

COMMIT;

-- ── VERIFICACIÓN ─────────────────────────────────────────────
SELECT 'Total contenidos' AS metrica, COUNT(*) AS valor FROM contenidos
UNION ALL
SELECT 'Películas', COUNT(*) FROM contenidos WHERE tipo = 'PELICULA'
UNION ALL
SELECT 'Series', COUNT(*) FROM contenidos WHERE tipo = 'SERIE'
UNION ALL
SELECT 'Documentales', COUNT(*) FROM contenidos WHERE tipo = 'DOCUMENTAL'
UNION ALL
SELECT 'Géneros distintos', COUNT(DISTINCT genero) FROM contenidos
UNION ALL
SELECT 'Plataformas habilitadas', COUNT(*) FROM plataformas WHERE habilitada = TRUE
UNION ALL
SELECT 'Año mínimo', MIN(anio) FROM contenidos
UNION ALL
SELECT 'Año máximo', MAX(anio) FROM contenidos;
