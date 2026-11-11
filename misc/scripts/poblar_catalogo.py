#!/usr/bin/env python3
"""Pobla la tabla `contenidos` de Cinematch con títulos de TMDb.

El backend nunca consulta TMDb en línea: los filtros y el swipe leen de
PostgreSQL. Este script es el único punto de contacto con TMDb y lo hace
de forma pausada para respetar el límite de 40 consultas / 10 s.

Uso:
    export TMDB_API_KEY=<tu_api_key>
    python3 backend/misc/scripts/poblar_catalogo.py

Requisitos:
    pip install -r backend/misc/scripts/requirements.txt

La primera vez debe haberse arrancado el backend una vez para que
Hibernate cree las tablas (ddl-auto=update). El script es idempotente:
re-ejecutarlo actualiza los títulos existentes sin duplicar nada.
"""

import os
import sys
import time

import psycopg2
import requests

# ── TMDb ────────────────────────────────────────────────────────────────────
TMDB_API_KEY = os.environ.get("TMDB_API_KEY", "")
TMDB_BASE_URL = "https://api.themoviedb.org/3"
TMDB_IMAGE_BASE = "https://image.tmdb.org/t/p/w500"
IDIOMA = "es-MX"
REGION_PLATAFORMAS = "MX"

# ── Metas de catálogo ───────────────────────────────────────────────────────
META_PELICULAS = 130
META_SERIES = 130
MINIMO_TOTAL = 250          # requisito: al menos 250 títulos entre ambos tipos
MAX_PAGINAS = 50            # tope de páginas por tipo (20 títulos c/u)

# ── Ritmo de consultas ──────────────────────────────────────────────────────
PAUSA_ENTRE_CONSULTAS = 0.35     # 2.8 req/s < límite de TMDb (40 req / 10 s)
MAX_REINTENTOS = 3
TIMEOUT_CONSULTA = 15            # segundos

# ── Base de datos (mismos valores que application.properties en dev) ────────
DB_CONFIG = {
    "host": os.environ.get("DB_HOST", "localhost"),
    "port": os.environ.get("DB_PORT", "5433"),
    "dbname": os.environ.get("DB_NAME", "cinematch"),
    "user": os.environ.get("DB_USER", "postgres"),
    "password": os.environ.get("DB_PASSWORD", "1234"),
}

# TMDB id de género → enum GeneroContenido. Los géneros de series que no
# caen en el enum (Kids, Reality, War & Politics…) se omiten: si ningún
# género del título mapea, el título se descarta.
GENEROS_TMDB = {
    # Películas: los 14 valores clásicos del enum
    28: "ACCION",
    12: "AVENTURA",
    16: "ANIMACION",
    35: "COMEDIA",
    80: "CRIMEN",
    99: "DOCUMENTAL",
    18: "DRAMA",
    14: "FANTASIA",
    27: "HORROR",
    9648: "MISTERIO",
    10749: "ROMANCE",
    878: "CIENCIA_FICCION",
    53: "THRILLER",
    37: "WESTERN",
    # Series: géneros combinados de TMDb sin equivalente exacto
    10759: "ACCION",              # "Action & Adventure"
    10765: "CIENCIA_FICCION",     # "Sci-Fi & Fantasy"
}


def consultar_tmdb(ruta: str, params: dict) -> dict:
    """GET pausado a TMDb con reintentos ante 429 y errores transitorios."""
    url = f"{TMDB_BASE_URL}{ruta}"
    consulta = {**params, "api_key": TMDB_API_KEY}
    ultimo_error = "sin respuesta"

    for intento in range(1, MAX_REINTENTOS + 1):
        try:
            respuesta = requests.get(url, params=consulta, timeout=TIMEOUT_CONSULTA)
        except requests.RequestException as error:
            ultimo_error = f"error de red: {error}"
            time.sleep(PAUSA_ENTRE_CONSULTAS * intento)
            continue

        if respuesta.status_code == 429:
            espera = int(respuesta.headers.get("Retry-After", 5))
            print(f"  límite de TMDb alcanzado (429), esperando {espera}s…")
            ultimo_error = "límite de consultas (429)"
            time.sleep(espera)
            continue

        if respuesta.status_code >= 500:
            ultimo_error = f"error interno de TMDb ({respuesta.status_code})"
            time.sleep(PAUSA_ENTRE_CONSULTAS * intento)
            continue

        if not respuesta.ok:
            raise SystemExit(
                f"TMDb rechazó {ruta}: HTTP {respuesta.status_code} — {respuesta.text[:200]}"
            )

        time.sleep(PAUSA_ENTRE_CONSULTAS)
        return respuesta.json()

    raise SystemExit(
        f"TMDb no respondió tras {MAX_REINTENTOS} reintentos ({ruta}): {ultimo_error}"
    )


def genero_valido(genre_ids: list) -> str | None:
    """Primer género de TMDb que cae en nuestro enum GeneroContenido."""
    for genre_id in genre_ids:
        if genre_id in GENEROS_TMDB:
            return GENEROS_TMDB[genre_id]
    return None


def plataformas_de(recurso: str, id_tmdb: int) -> list:
    """Nombres de proveedores con suscripción (flatrate) disponibles en México."""
    datos = consultar_tmdb(f"/{recurso}/{id_tmdb}/watch/providers", {})
    en_region = datos.get("results", {}).get(REGION_PLATAFORMAS) or {}
    return [p["provider_name"] for p in en_region.get("flatrate") or []]


def guardar_titulo(conn, contenido_id, titulo, anio, tipo, genero, imagen, plataformas):
    """Upsert del título y de sus plataformas; commitea al final."""
    with conn.cursor() as cur:
        cur.execute(
            """
            INSERT INTO contenidos (contenido_id, titulo, anio, tipo, genero, imagen)
            VALUES (%s, %s, %s, %s, %s, %s)
            ON CONFLICT (contenido_id) DO UPDATE SET
                titulo = EXCLUDED.titulo,
                anio    = EXCLUDED.anio,
                tipo    = EXCLUDED.tipo,
                genero  = EXCLUDED.genero,
                imagen  = EXCLUDED.imagen
            """,
            (contenido_id, titulo, anio, tipo, genero, imagen),
        )
        # Sin constraint única en (contenido_id, plataforma_id): reemplazar
        # los vínculos es la forma idempotente de refrescarlos.
        cur.execute(
            "DELETE FROM contenido_plataforma WHERE contenido_id = %s",
            (contenido_id,),
        )
        for nombre in plataformas:
            cur.execute(
                """
                INSERT INTO plataformas (nombre, habilitada)
                VALUES (%s, TRUE)
                ON CONFLICT (nombre) DO UPDATE SET nombre = EXCLUDED.nombre
                RETURNING id
                """,
                (nombre,),
            )
            plataforma_id = cur.fetchone()[0]
            cur.execute(
                """
                INSERT INTO contenido_plataforma (contenido_id, plataforma_id)
                VALUES (%s, %s)
                """,
                (contenido_id, plataforma_id),
            )
    conn.commit()


def contenido_id_de(recurso: str, id_tmdb) -> str:
    """Identificador único en la BD: los IDs de movie y tv comparten rango."""
    return f"{recurso}_{id_tmdb}"


def procesar_item(conn, recurso, tipo, item) -> tuple:
    """Valida un resultado de discover y lo guarda. Devuelve (guardado, detalle)."""
    titulo = item.get("title") or item.get("name") or ""
    if not titulo:
        return False, "sin título"

    fecha = item.get("release_date") or item.get("first_air_date") or ""
    anio = int(fecha[:4]) if len(fecha) >= 4 and fecha[:4].isdigit() else None
    if anio is None:
        return False, "sin fecha"

    poster = item.get("poster_path")
    if not poster:
        return False, "sin póster"

    genero = genero_valido(item.get("genre_ids") or [])
    if genero is None:
        return False, "sin género mapeable"

    contenido_id = contenido_id_de(recurso, item["id"])
    plataformas = plataformas_de(recurso, item["id"])
    imagen = f"{TMDB_IMAGE_BASE}{poster}"

    guardar_titulo(conn, contenido_id, titulo, anio, tipo, genero, imagen, plataformas)
    detalle = f"{contenido_id} · {titulo} ({anio}) · {genero} · {len(plataformas)} plat."
    return True, detalle


def procesar_tipo(conn, recurso, tipo, meta) -> tuple:
    """Pagina discover hasta completar la meta. Devuelve (guardados, descartados)."""
    guardados = 0
    descartados = {}
    vistos = set()
    pagina = 1

    while guardados < meta and pagina <= MAX_PAGINAS:
        datos = consultar_tmdb(
            f"/discover/{recurso}",
            {
                "include_adult": "false",
                "language": IDIOMA,
                "page": pagina,
                "sort_by": "popularity.desc",
            },
        )
        resultados = datos.get("results", [])
        if not resultados:
            break

        for item in resultados:
            if guardados >= meta:
                break
            # El ranking de popularidad cambia entre páginas y TMDB repite
            # títulos: dedup antes de consultar watch/providers.
            clave = contenido_id_de(recurso, item["id"])
            if clave in vistos:
                continue
            guardado, detalle = procesar_item(conn, recurso, tipo, item)
            if guardado:
                vistos.add(clave)
                guardados += 1
                print(f"  [{tipo}] {guardados}/{meta} — {detalle}", flush=True)
            else:
                descartados[detalle] = descartados.get(detalle, 0) + 1
        pagina += 1

    return guardados, descartados


def conectar():
    """Conecta a PostgreSQL y verifica que el esquema ya exista."""
    try:
        conn = psycopg2.connect(**DB_CONFIG)
    except psycopg2.OperationalError as error:
        raise SystemExit(
            f"No se pudo conectar a PostgreSQL en "
            f"{DB_CONFIG['host']}:{DB_CONFIG['port']}: {error}"
        )

    with conn.cursor() as cur:
        cur.execute("SELECT to_regclass('public.contenidos')")
        if cur.fetchone()[0] is None:
            conn.close()
            raise SystemExit(
                "La tabla 'contenidos' no existe. Arranca el backend una vez para "
                "que Hibernate cree el esquema (ddl-auto=update) y vuelve a "
                "ejecutar este script."
            )
    return conn


def mostrar_resumen(conn, resultados) -> bool:
    """Imprime el resumen de la ejecución. Devuelve True si se cumplió la meta."""
    peli_ok, peli_desc = resultados["PELICULA"]
    serie_ok, serie_desc = resultados["SERIE"]

    # Fusionar contadores del mismo motivo en ambos tipos
    descartados = {}
    for motivo, cantidad in list(peli_desc.items()) + list(serie_desc.items()):
        descartados[motivo] = descartados.get(motivo, 0) + cantidad

    with conn.cursor() as cur:
        cur.execute("SELECT tipo, count(*) FROM contenidos GROUP BY tipo")
        por_tipo = dict(cur.fetchall())
        cur.execute("SELECT count(*) FROM contenidos")
        total_contenidos = cur.fetchone()[0]
        cur.execute("SELECT count(*) FROM plataformas")
        total_plataformas = cur.fetchone()[0]
        cur.execute(
            "SELECT count(DISTINCT contenido_id) FROM contenido_plataforma"
        )
        con_plataformas = cur.fetchone()[0]

    total = int(total_contenidos)
    print()
    print("═══ Resumen ═══")
    print(f"Películas guardadas: {peli_ok}/{META_PELICULAS}")
    print(f"Series guardadas:    {serie_ok}/{META_SERIES}")
    if descartados:
        desglose = ", ".join(
            f"{motivo}: {cantidad}"
            for motivo, cantidad in sorted(descartados.items(), key=lambda x: -x[1])
            if cantidad
        )
        print(f"Descartados:         {sum(descartados.values())} ({desglose})")
    print()
    print("En PostgreSQL:")
    print(
        f"  contenidos:                {total_contenidos} "
        f"(PELICULA: {por_tipo.get('PELICULA', 0)}, SERIE: {por_tipo.get('SERIE', 0)})"
    )
    print(f"  plataformas:               {total_plataformas}")
    print(f"  títulos con ≥1 plataforma: {con_plataformas}")

    if total < MINIMO_TOTAL:
        print(
            f"\nAviso: solo {total} títulos distintos en la BD "
            f"(mínimo requerido: {MINIMO_TOTAL}).",
            file=sys.stderr,
        )
        return False
    return True


def main() -> int:
    if not TMDB_API_KEY:
        print(
            "Error: define la variable de entorno TMDB_API_KEY.\n"
            "Uso: export TMDB_API_KEY=<tu_api_key> && python3 poblar_catalogo.py",
            file=sys.stderr,
        )
        return 1

    print("Poblando catálogo desde TMDb (modo pausado, ~3 min)…")
    print(f"  meta: {META_PELICULAS} películas + {META_SERIES} series\n")

    conn = conectar()
    exito = False
    try:
        resultados = {
            "PELICULA": procesar_tipo(conn, "movie", "PELICULA", META_PELICULAS),
            "SERIE": procesar_tipo(conn, "tv", "SERIE", META_SERIES),
        }
        exito = mostrar_resumen(conn, resultados)
    except KeyboardInterrupt:
        conn.rollback()
        print(
            "\nInterrumpido: los títulos ya guardados permanecen en la BD.",
            file=sys.stderr,
        )
        return 130
    except psycopg2.Error as error:
        conn.rollback()
        print(f"\nError de base de datos: {error}", file=sys.stderr)
        return 1
    finally:
        conn.close()

    return 0 if exito else 1


if __name__ == "__main__":
    sys.exit(main())
