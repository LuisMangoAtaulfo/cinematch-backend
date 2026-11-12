# 🎬 CineMatch — Backend

API de CineMatch: plataforma para que dos personas encuentren, en tiempo real y por consenso, qué película o serie ver.

**Stack:** Java 21 · Spring Boot 4 · Spring GraphQL · Spring Security + JWT · WebSocket · PostgreSQL · Maven

> Este repositorio trabaja junto con [`cinematch-frontend`](https://github.com/LuisMangoAtaulfo/cinematch-frontend),
> donde también viven las pruebas E2E con **Robot Framework**.

---

## 📑 Contenido

1. [Requisitos](#-requisitos)
2. [Clonar el proyecto (WSL recomendado)](#-clonar-el-proyecto-wsl-recomendado)
3. [Base de datos (PostgreSQL en puerto 5433)](#-base-de-datos-postgresql-en-puerto-5433)
4. [Configuración](#-configuración)
5. [Ejecutar en desarrollo](#-ejecutar-en-desarrollo)
6. [Pruebas JUnit](#-pruebas-junit)
7. [Preparar el backend para Robot Framework](#-preparar-el-backend-para-robot-framework)
8. [Despliegue](#-despliegue)
9. [Estructura del proyecto](#-estructura-del-proyecto)
10. [Solución de problemas](#-solución-de-problemas)

---

## ✅ Requisitos

| Herramienta | Versión |
|---|---|
| Git | cualquiera reciente |
| JDK | **21** |
| PostgreSQL | 14 o superior (o Docker) |
| Maven | No es necesario: se usa el wrapper `./mvnw` |

---

## 📥 Clonar el proyecto (WSL recomendado)

### 1. Instalar WSL2 con Ubuntu (solo la primera vez)

En **PowerShell como administrador**:

```powershell
wsl --install -d Ubuntu
```

Reinicia, abre "Ubuntu" y crea tu usuario Linux.

### 2. Configurar Git dentro de WSL

```bash
sudo apt update && sudo apt install -y git
git config --global user.name  "Tu Nombre"
git config --global user.email "tu@correo.com"
# Evita finales de línea CRLF que rompen ./mvnw y los scripts .sh
git config --global core.autocrlf input
```

### 3. Clonar ambos repositorios lado a lado

> [!WARNING]
> Clona **dentro del sistema de archivos de Linux** (`~/…`), **no** en `/mnt/c/…`.
> En `/mnt/c` Maven es muy lento y `./mvnw` puede perder el permiso de ejecución.

```bash
mkdir -p ~/cinematch && cd ~/cinematch
git clone https://github.com/LuisMangoAtaulfo/cinematch-backend.git  backend
git clone https://github.com/LuisMangoAtaulfo/cinematch-frontend.git frontend
```

```
~/cinematch/
├── backend/    ← cinematch-backend (este repo)
└── frontend/   ← cinematch-frontend (incluye e2e/robot)
```

La estructura de carpetas hermanas es necesaria: el script de Robot del frontend busca los seeds en `../backend/misc/scripts/`.

### 4. Instalar JDK 21

```bash
sudo apt install -y openjdk-21-jdk
java -version          # debe mostrar 21
cd ~/cinematch/backend
chmod +x mvnw          # solo si aparece "Permission denied"
```

### 5. Abrir en el IDE (opcional)

- **VS Code:** extensión *WSL* → `code .`
- **IntelliJ IDEA:** *File → Open* → `\\wsl$\Ubuntu\home\<usuario>\cinematch\backend` y seleccionar el JDK 21 de WSL.

---

## 🐘 Base de datos (PostgreSQL en puerto 5433)

El backend espera la BD `cinematch` en `localhost:5433`. Elige **una** de las dos opciones.

### Opción A — Docker (más sencilla)

Requiere Docker Desktop con integración WSL activada.

```bash
docker run -d --name cinematch-db \
  -e POSTGRES_PASSWORD=<contraseña> \
  -e POSTGRES_DB=cinematch \
  -p 5433:5432 \
  postgres:16

docker start cinematch-db   # en sesiones posteriores
```

### Opción B — PostgreSQL instalado en WSL

```bash
sudo apt install -y postgresql postgresql-client
# Cambiar el puerto a 5433
sudo sed -i 's/^#\?port = .*/port = 5433/' /etc/postgresql/*/main/postgresql.conf
sudo service postgresql restart

sudo -u postgres psql -p 5433 -c "ALTER USER postgres PASSWORD '<contraseña>';"
sudo -u postgres psql -p 5433 -c "CREATE DATABASE cinematch;"
```

> En WSL el servicio no arranca solo: ejecuta `sudo service postgresql start` en cada sesión.

Verificar:

```bash
pg_isready -h localhost -p 5433
```

---

## ⚙️ Configuración

Archivo: `src/main/resources/application.properties`. Claves principales:

| Propiedad | Valor de desarrollo | Descripción |
|---|---|---|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5433/cinematch` | Conexión a la BD |
| `spring.datasource.username` / `password` | `postgres` / *(la que configuraste)* | Credenciales |
| `spring.jpa.hibernate.ddl-auto` | `update` | Hibernate crea/actualiza las tablas al arrancar |
| `jwt.expiration` | `86400000` | Vigencia del token (ms) |
| `app.test-mode` | `true` | Habilita las mutations de prueba que usa Robot. **`false` en producción** |
| `server.port` | `8080` | Puerto HTTP |

> [!CAUTION]
> No subas contraseñas reales ni secretos de producción a este archivo.

---

## ▶️ Ejecutar en desarrollo

```bash
cd ~/cinematch/backend
./mvnw spring-boot:run
```

| Recurso | URL |
|---|---|
| Endpoint GraphQL | http://localhost:8080/graphql |
| GraphiQL (explorador) | http://localhost:8080/graphiql |
| Swagger UI | http://localhost:8080/swagger-ui.html |

El primer arranque crea las tablas automáticamente.

---

## 🧪 Pruebas JUnit

```bash
./mvnw test                     # todas
./mvnw package -DskipTests      # generar el JAR sin pruebas
```

Unitarias, integración, sistema y regresión usan **H2 en memoria** (perfil `test`).
`CinematchBackendApplicationTests`, `CargaUsuariosInicialesTest` y `CargaUsuariosRobotTest` **necesitan PostgreSQL** encendido.

Detalle completo: **[misc/docs/PRUEBAS.md](misc/docs/PRUEBAS.md)**.

---

## 🤖 Preparar el backend para Robot Framework

Las pruebas E2E se ejecutan desde el **frontend** (`frontend/e2e/robot`), pero dependen de este backend.
Guía de ejecución: [README del frontend → Pruebas automatizadas](https://github.com/LuisMangoAtaulfo/cinematch-frontend#-pruebas-automatizadas-robot-framework).

### Checklist

1. **BD lista** en `localhost:5433` (sección anterior).
2. **Arrancar el backend una vez** para que se creen las tablas.
3. **Cargar los datos de prueba:**
   ```bash
   cd ~/cinematch/backend
   export PGPASSWORD=<contraseña>
   psql -h localhost -p 5433 -U postgres -d cinematch -f misc/scripts/seed_usuarios_robot.sql
   psql -h localhost -p 5433 -U postgres -d cinematch -f misc/scripts/seed_plataformas_robot.sql
   ```
   | Usuario | Contraseña | Rol |
   |---|---|---|
   | `test1@cinematch.com` | `Test1234` | Usuario |
   | `test2@cinematch.com` | `Test1234` | Usuario |
   | `admin@cinematch.com` | `Admin1234` | Administrador |
4. **`app.test-mode=true`** en `application.properties`.
5. **Backend corriendo:** `./mvnw spring-boot:run`.
6. Levantar el frontend y ejecutar la suite:
   ```bash
   cd ~/cinematch/frontend && npm start          # en otra terminal
   cd ~/cinematch/frontend/e2e/robot && ./run_tests.sh
   ```

> [!IMPORTANT]
> Robot asume que `test1` y `test2` tienen los IDs **5** y **6**. En una BD nueva verifica los IDs y,
> si no coinciden, consulta [misc/docs/PRUEBAS.md](misc/docs/PRUEBAS.md#22-datos-de-prueba-seeds).
> Para limpiar salas/evaluaciones entre corridas usa la mutation `limpiarDatosTest`
> ([instrucciones](misc/docs/PRUEBAS.md#23-reiniciar-el-estado-entre-corridas)).

---

## 🚀 Despliegue

Cada push a `master` dispara el workflow [`.github/workflows/deploy-backend.yml`](.github/workflows/deploy-backend.yml):
compila con Maven, copia el JAR a la VM por SSH, reinicia el servicio y verifica que responda.
Requiere los *secrets* de GitHub configurados (host, usuario y llave SSH de la VM).
Rollback: conectarse a la VM y restaurar uno de los JAR anteriores (consultar la guía de despliegue del equipo).

---

## 🗂️ Estructura del proyecto

```
src/main/java/com/cinematchbackend/
├── config/        # Seguridad, CORS, GraphQL, manejo de errores
├── graphql/       # Controladores GraphQL (@QueryMapping / @MutationMapping)
├── controllers/   # Endpoints REST / WebSocket
├── services/      # Lógica de negocio (interfaces + impl)
├── repositories/  # Spring Data JPA
├── entities/ dto/ enums/ exceptions/
├── observer/ strategy/   # Patrones de diseño
src/main/resources/
├── application.properties
└── graphql/*.graphqls    # Esquema GraphQL (test.graphqls = mutations de prueba)
misc/
├── docs/          # PRUEBAS.md, checklist de aceptación
└── scripts/       # Seeds SQL (usuarios/plataformas para Robot, catálogo)
```

---

## 🛠️ Solución de problemas

| Problema | Solución |
|---|---|
| `Connection to localhost:5433 refused` | PostgreSQL apagado o en otro puerto: `sudo service postgresql start` o `docker start cinematch-db` |
| `./mvnw test` falla y no se genera el JAR | Las pruebas que usan PostgreSQL no tienen BD; levántala o usa `-DskipTests` |
| `./mvnw: Permission denied` | `chmod +x mvnw` |
| `/bin/sh^M: bad interpreter` | Finales CRLF: `sed -i 's/\r$//' mvnw` y `git config --global core.autocrlf input` |
| `Port 8080 already in use` | `sudo lsof -i :8080` y detén el proceso, o cambia `server.port` |
| `TestModeNotEnabledException` en Robot | Activa `app.test-mode=true` y reinicia |
| `password authentication failed for user "postgres"` | La contraseña en `application.properties` no coincide con la de la BD |
