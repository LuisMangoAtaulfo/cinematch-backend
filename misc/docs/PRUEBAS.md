# Pruebas — CineMatch Backend

Este documento cubre dos cosas:

1. Las **pruebas JUnit** del backend (`src/test/java`).
2. Lo que el backend **aporta a las pruebas E2E con Robot Framework**, que viven en el repositorio del frontend
   ([`cinematch-frontend/e2e/robot`](https://github.com/LuisMangoAtaulfo/cinematch-frontend/tree/master/e2e/robot)).

---

## 1. Pruebas JUnit

```bash
./mvnw test                                   # todas
./mvnw test -Dtest=MetricasServiceImplTest    # una clase
./mvnw package -DskipTests                    # generar el JAR sin pruebas
```

| Paquete / clase | Tipo | Base de datos |
|---|---|---|
| `unit/services/MatchServiceImplTest` | Unitaria (Mockito) | — |
| `unit/services/MetricasServiceImplTest` | Unitaria (Mockito) | — |
| `integration/MetricasGraphQLIntegrationTest` | Integración (MockMvc + GraphQL) | H2 en memoria (perfil `test`) |
| `system/SistemaEndToEndTest` | Sistema | H2 en memoria (perfil `test`) |
| `regression/RegresionGraphQLTest` | Regresión | H2 en memoria (perfil `test`) |
| `CinematchBackendApplicationTests` | Arranque del contexto | **PostgreSQL real** (5433) |
| `CargaUsuariosInicialesTest` | Utilidad: crea usuarios de desarrollo | **PostgreSQL real** (5433) |
| `CargaUsuariosRobotTest` | Utilidad: crea usuarios de Robot | **PostgreSQL real** (5433) |

> [!IMPORTANT]
> Las tres últimas clases no usan el perfil `test`, así que **requieren PostgreSQL encendido**.
> Si la BD no está disponible, `./mvnw test` falla con `Connection to localhost:5433 refused` y el JAR no se genera.
> En ese caso levanta la BD o usa `-DskipTests`.

Las pruebas que consultan operaciones protegidas con `@PreAuthorize` deben simular el rol, por ejemplo:

```java
@Test
@WithMockUser(roles = "ADMINISTRADOR")
void consultarHistorialMetricas_CuandoSeLlama_DeberiaRetornarLista() { … }
```

---

## 2. Soporte del backend a Robot Framework

### 2.1 Modo test

Robot usa *mutations* GraphQL exclusivas de pruebas (`src/main/resources/graphql/test.graphqls`),
implementadas en `TestFixtureGraphQLController` y `SimulacionService`. Solo responden si:

```properties
app.test-mode=true
```

En caso contrario lanzan `TestModeNotEnabledException`. **Debe estar en `false` en producción.**

| Mutation | Uso en Robot |
|---|---|
| `crearSalaTest(usuario1Id, usuario2Id, estado)` | Crear una sala ya emparejada sin pasar por la UI |
| `unirUsuarioTest`, `simularUnion` | Unir al segundo usuario |
| `simularEvaluacion` | Forzar likes/dislikes (matches) |
| `simularMensaje` | Mensajes del compañero en el chat |
| `simularDesconexion` / `simularReconexion` | Estados de conexión |
| `finalizarSalaTest` | Pasar la sala a `FINALIZADA` |
| `insertarContenidoTest` | Agregar contenido al catálogo |
| `limpiarDatosTest` | **Borra todas las salas** (y en cascada matches, mensajes, evaluaciones y filtros) |

Todas requieren un usuario autenticado (`Authorization: Bearer <token>`).

### 2.2 Datos de prueba (seeds)

Ubicados en [`misc/scripts/`](../scripts):

| Script | Crea |
|---|---|
| `seed_usuarios_robot.sql` | `test1@cinematch.com` / `Test1234`, `test2@cinematch.com` / `Test1234`, `admin@cinematch.com` / `Admin1234` |
| `seed_plataformas_robot.sql` | 8 plataformas habilitadas (Netflix, Disney+, HBO Max, …) |

```bash
export PGPASSWORD=<contraseña_postgres>
psql -h localhost -p 5433 -U postgres -d cinematch -f misc/scripts/seed_usuarios_robot.sql
psql -h localhost -p 5433 -U postgres -d cinematch -f misc/scripts/seed_plataformas_robot.sql
```

Orden recomendado: **arrancar el backend al menos una vez** (Hibernate crea las tablas con `ddl-auto=update`), luego ejecutar los seeds.

Alternativa sin `psql`: `./mvnw test -Dtest=CargaUsuariosRobotTest`.

> [!WARNING]
> Robot asume que `test1` y `test2` tienen los IDs **5** y **6** (`USER1_ID` / `USER2_ID` en `variables.robot`).
> Verifícalo con:
> ```sql
> SELECT id, correo FROM usuarios WHERE correo IN ('test1@cinematch.com','test2@cinematch.com');
> ```
> Si son distintos, ejecuta Robot con `--variable USER1_ID:<id> --variable USER2_ID:<id>`.

### 2.3 Reiniciar el estado entre corridas

Las pruebas de filtros y swipe fallan (terminan en `/resultados`) si la BD ya tiene evaluaciones de corridas anteriores.
Para limpiar salas y todo lo asociado sin borrar usuarios ni catálogo:

```bash
TOKEN=$(curl -s http://localhost:8080/graphql -H 'Content-Type: application/json' \
  -d '{"query":"mutation { login(input:{correo:\"test1@cinematch.com\", password:\"Test1234\"}) { token } }"}' \
  | python3 -c 'import sys,json; print(json.load(sys.stdin)["data"]["login"]["token"])')

curl -s http://localhost:8080/graphql -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"query":"mutation { limpiarDatosTest }"}'
```

Para empezar desde cero (borra **todo**):

```bash
psql -h localhost -p 5433 -U postgres -c 'DROP DATABASE cinematch;' -c 'CREATE DATABASE cinematch;'
# arrancar el backend para recrear tablas y volver a ejecutar los seeds
```

### 2.4 Checklist para correr Robot

- [ ] PostgreSQL en `localhost:5433`, BD `cinematch`
- [ ] Seeds de usuarios y plataformas cargados
- [ ] `app.test-mode=true`
- [ ] Backend en `http://localhost:8080` (`./mvnw spring-boot:run`)
- [ ] Frontend en `http://localhost:4200`
- [ ] Ejecutar desde el frontend: `cd ../frontend/e2e/robot && ./run_tests.sh`
