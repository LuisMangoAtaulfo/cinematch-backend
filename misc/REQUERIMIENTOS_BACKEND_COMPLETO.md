# Requerimientos Backend Completos — CineMatch
## Automatización Total de Tests

> **Objetivo:** Implementar todo lo necesario en el backend para que el 100% de los tests de Robot Framework se ejecuten automáticamente sin intervención manual.
> 
> **Tests actualmente manuales:** ~45 tests
> **Tests objetivo automatizados:** 100%

---

## Índice

1. [Infraestructura de Testing](#r-be-01-infraestructura-de-testing)
2. [Gestión de Salas](#r-be-02-gestión-completa-de-salas)
3. [Comunicación en Tiempo Real](#r-be-03-comunicación-en-tiempo-real)
4. [Catálogo de Contenido](#r-be-04-catálogo-de-contenido)
5. [Match Detection](#r-be-05-match-detection)
6. [Filtros de Contenido](#r-be-06-filtros-de-contenido)
7. [Seguridad GraphQL](#r-be-07-seguridad-graphql)
8. [Resultados y Calificación](#r-be-08-resultados-y-calificación)
9. [Endpoints de Prueba](#r-be-09-endpoints-de-prueba-para-automatización)
10. [Gestión de Estado de Conexión](#r-be-10-gestión-de-estado-de-conexión)

---

## R-BE-01: Infraestructura de Testing

### Contexto
Actualmente no existe forma de preparar el estado de la aplicación para los tests de forma automática. Los tests que requieren salas activas, contenido en BD o estados específicos no pueden ejecutarse sin intervención manual.

### Requerimiento
Implementar un sistema completo de fixtures y datos de prueba accesible vía API para preparar el estado antes de cada test.

### Criterios de Aceptación

| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Mutation `crearSalaTest(codigo, estado, creadorId, invitadoId)` | Permite crear sala con estado específico (CREADA, ESPERANDO, ACTIVA, FINALIZADA) |
| 2 | Mutation `limpiarDatosTest()` | Elimina todas las salas, matches y mensajes de prueba en < 5 segundos |
| 3 | Mutation `insertarContenidoTest(contenido)` | Permite insertar contenido específico para tests |
| 4 | Mutation `simularDesconexion(usuarioId)` | Marca un usuario como desconectado en una sala activa |
| 5 | Mutation `simularReconexion(usuarioId)` | Marca un usuario como reconectado |
| 6 | Todas las mutations de test requieren rol ADMINISTRADOR | Requests sin token admin retornan error `FORBIDDEN` |
| 7 | Script SQL `seed_test_data.sql` con datos mínimos | Al ejecutar, existen 20+ contenidos, 2 usuarios de prueba y 1 admin |
| 8 | Las mutations de test NO están disponibles en producción | Variable de entorno `TEST_MODE=true` required |

### Tests que desbloquea
- **Todos** los tests actualmente manuales que requieren estado específico

---

## R-BE-02: Gestión Completa de Salas

### Contexto
Los tests TC-SALA-013, TC-SALA-014, TC-SALA-015 requieren transiciones de estado completas y detección de cambios por polling/WebSocket.

### Requerimiento
Implementar todas las transiciones de estado de sala con notificaciones en tiempo real.

### Criterios de Aceptación

| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Crear sala → estado CREADA | Query `obtenerSala` retorna estado `CREADA` |
| 2 | Segundo usuario se une → estado ACTIVA | Estado cambia automáticamente cuando `invitadoId != null` |
| 3 | Creador finaliza sala → estado FINALIZADA | Solo el creador puede ejecutar `finalizarSala` |
| 4 | Invitado intenta finalizar → error | Mutation retorna error `Solo el creador puede finalizar la sala` |
| 5 | Creador cancela sala en ESPERANDO → estado CANCELADA | Sala se elimina o marca como CANCELADA |
| 6 | Invitado cancela desde espera → redirige a home | La sala se limpia y el invitado sale |
| 7 | Subscription `cambiosSala(salaId)` notifica cambios de estado | Evento emitido en < 2 segundos tras cada cambio |
| 8 | Polling alternativo: Query `obtenerSala` con cache buster | Retorna estado actualizado en < 3 segundos |
| 9 | Código de sala es único y alfanumérico de 6 caracteres | 1000 creaciones → 0 colisiones, todos cumplen `^[A-Z0-9]{6}$` |
| 10 | Unirse con código inexistente → error descriptivo | Mensaje contiene "Código de sala inválido" |
| 11 | Unirse con campo vacío → error de validación | Mensaje contiene "Ingresa un código" |
| 12 | Sala FINALIZADA no permite unirse | Error "La sala ya no está disponible" |

### Tests que desbloquea
- **TC-SALA-013**: Creador navega a filtros cuando compañero se une
- **TC-SALA-014**: Solo el creador puede finalizar sala
- **TC-SALA-015**: Finalizar sala desde swipe

---

## R-BE-03: Comunicación en Tiempo Real (WebSocket/GraphQL Subscriptions)

### Contexto
Los tests de chat (TC-CHAT-001 a TC-CHAT-013), match detection (TC-SWIPE-009, TC-SWIPE-010) y E2E (TC-E2E-002, TC-E2E-003, TC-E2E-009) requieren comunicación bidireccional en tiempo real.

### Requerimiento
Implementar GraphQL Subscriptions completas para:
1. Mensajería de chat
2. Notificación de matches
3. Estado de conexión del compañero
4. Evaluaciones de swipe (like/dislike)

### Criterios de Aceptación

| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Subscription `mensajesRecibidos(salaId)` | Mensaje enviado por Usuario A aparece en Usuario B en < 2 segundos |
| 2 | Mutation `enviarMensaje(salaId, texto)` | Persiste mensaje y notifica al compañero |
| 3 | Subscription `matchDetectado(salaId)` | Notifica cuando ambos usuarios dan LIKE al mismo contenido en < 3 segundos |
| 4 | Subscription `estadoCompanero(salaId)` | Notifica cuando el compañero se desconecta/reconecta en < 2 segundos |
| 5 | Subscription `evaluacionRecibida(salaId)` | Notifica cuando el compañero evalúa contenido (like/dislike) |
| 6 | Conexión WebSocket requiere JWT válido | Conexión sin token es rechazada con código 4401 |
| 7 | Conexión sin sala activa retorna error | Subscription sin sala retorna error descriptivo |
| 8 | Desconexión detectada en < 10 segundos | Si un cliente pierde conexión, el otro recibe notificación en < 10s |
| 9 | Reconexión restaura estado | Al reconectar, se recibe estado actual de la sala |
| 10 | Mensajes se persisten en BD | Query `obtenerMensajes(salaId)` retorna historial completo |
| 11 | Match detection es atómica | No se generan matches duplicados |
| 12 | Evaluaciones se persisten | Query `obtenerEvaluaciones(salaId)` retorna todas las evaluaciones |

### Tests que desbloquea
- **TC-CHAT-001**: Enviar mensaje
- **TC-CHAT-002**: Enviar mensaje con Enter
- **TC-CHAT-005**: Recibir mensaje en tiempo real
- **TC-CHAT-006**: Ver historial de chat
- **TC-CHAT-007**: Mensajes propios alineados a la derecha
- **TC-CHAT-008**: Mensajes del compañero alineados a la izquierda
- **TC-CHAT-010**: Banner de reconexión
- **TC-CHAT-011**: Banner de match notificado
- **TC-SWIPE-009**: Match detectado muestra banner
- **TC-SWIPE-010**: No hay match cuando uno da dislike
- **TC-E2E-002**: Flujo con match
- **TC-E2E-003**: Flujo sin matches
- **TC-E2E-009**: Flujo de chat en tiempo real
- **TC-E2E-010**: Flujo de desconexión y reconexión

---

## R-BE-04: Catálogo de Contenido

### Contexto
Los tests de filtros (TC-FILTRO-001 a TC-FILTRO-010) y swipe (TC-SWIPE-001 a TC-SWIPE-013) requieren contenido diverso en la base de datos.

### Requerimiento
Crear script de seed con catálogo diverso y endpoint para consultar contenido filtrado.

### Criterios de Aceptación

| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Script `seed_contenido_test.sql` crea ≥ 30 contenidos | `SELECT COUNT(*) FROM contenidos` retorna ≥ 30 |
| 2 | Al menos 3 tipos: PELICULA, SERIE, DOCUMENTAL | `SELECT DISTINCT tipo` retorna 3 valores |
| 3 | Al menos 8 géneros diferentes | Acción, Comedia, Drama, Terror, Sci-Fi, Romance, Animación, Documental |
| 4 | Al menos 5 plataformas habilitadas | Netflix, Disney+, HBO Max, Prime Video, Apple TV+ |
| 5 | Años distribuidos de 1980 a 2026 | `MIN(anio) = 1980`, `MAX(anio) ≥ 2024` |
| 6 | Cada contenido tiene título, año, género, sinopsis | No hay NULLs en campos obligatorios |
| 7 | Cada contenido tiene al menos 1 plataforma asociada | Relación N:M con plataformas |
| 8 | Query `contenidosFiltrados(tipo, genero, plataforma, anio)` | Retorna solo contenidos que cumplen TODOS los filtros |
| 9 | Sin filtros retorna todo el catálogo habilitado | Retorna ≥ 30 contenidos |
| 10 | Query `plataformasHabilitadas` | Retorna solo plataformas con `activa = true` |
| 11 | Query `generosDisponibles` | Retorna lista de géneros únicos ordenados alfabéticamente |
| 12 | Query `aniosDisponibles` | Retorna años únicos ordenados DESC (del actual a 1980) |

### Tests que desbloquea
- **TC-FILTRO-001**: Aplicar filtro por tipo Película
- **TC-FILTRO-002**: Aplicar filtro por tipo Serie
- **TC-FILTRO-003**: Aplicar filtro por género
- **TC-FILTRO-004**: Aplicar filtro por plataforma
- **TC-FILTRO-005**: Aplicar filtro por año
- **TC-FILTRO-006**: Aplicar filtros combinados
- **TC-FILTRO-007**: Omitir filtros
- **TC-FILTRO-008**: Solo se muestran plataformas habilitadas
- **TC-FILTRO-010**: Rango de años es correcto
- **TC-SWIPE-001**: Evaluar Like con botón
- **TC-SWIPE-002**: Evaluar Dislike con botón
- **TC-SWIPE-003**: Evaluar Like arrastrando derecha
- **TC-SWIPE-004**: Evaluar Dislike arrastrando izquierda
- **TC-SWIPE-005**: Tarjeta muestra información completa
- **TC-SWIPE-007**: Catálogo agotado muestra opciones
- **TC-SWIPE-008**: Sin resultados muestra opciones

---

## R-BE-05: Match Detection

### Contexto
Los tests TC-SWIPE-009, TC-SWIPE-010, TC-MATCH-001 verifican que el sistema detecte matches cuando ambos usuarios dan LIKE al mismo contenido.

### Requerimiento
Implementar lógica de detección de matches con persistencia y notificación en tiempo real.

### Criterios de Aceptación

| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Mutation `evaluarContenido(salaId, contenidoId, evaluacion)` | Persiste evaluación (LIKE o DISLIKE) |
| 2 | Cuando ambos usuarios evalúan LIKE el mismo contenido → match | Se crea registro en tabla `matches` automáticamente |
| 3 | Cuando al menos uno evalúa DISLIKE → no hay match | No se crea registro en `matches` |
| 4 | Match se detecta en < 3 segundos | Subscription `matchDetectado` emite evento en < 3s |
| 5 | No se generan matches duplicados | Evaluar LIKE dos veces no crea 2 matches |
| 6 | Match incluye información completa del contenido | Objeto match tiene: titulo, anio, genero, plataformas |
| 7 | Query `obtenerMatches(salaId)` | Retorna todos los matches de la sala |
| 8 | Query `obtenerMatches(usuarioId)` | Retorna todos los matches históricos del usuario |
| 9 | Match notifica a ambos usuarios simultáneamente | Ambos reciben evento en < 3 segundos |
| 10 | Evaluaciones son inmutables | No se puede cambiar una evaluación una vez hecha |

### Tests que desbloquea
- **TC-SWIPE-009**: Match detectado muestra banner
- **TC-SWIPE-010**: No hay match cuando uno da dislike
- **TC-MATCH-001**: Ver lista de matches con datos

---

## R-BE-06: Filtros de Contenido

### Contexto
Los tests TC-FILTRO-001 a TC-FILTRO-010 verifican que los filtros funcionen correctamente en el backend.

### Requerimiento
Implementar query GraphQL que acepte múltiples filtros combinables.

### Criterios de Aceptación

| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Query acepta filtro por `tipo` (PELICULA, SERIE, DOCUMENTAL) | Solo retorna contenidos del tipo especificado |
| 2 | Query acepta filtro por `genero` | Solo retorna contenidos del género especificado |
| 3 | Query acepta filtro por `plataformaId` | Solo retorna contenidos disponibles en esa plataforma |
| 4 | Query acepta filtro por `anio` | Solo retorna contenidos del año especificado |
| 5 | Filtros son combinables (AND lógico) | tipo=PELICULA AND genero=ACCION → solo películas de acción |
| 6 | Sin filtros retorna todo el catálogo habilitado | Retorna todos los contenidos con plataformas activas |
| 7 | Si no hay resultados, retorna array vacío con `totalResultados: 0` | Respuesta incluye metadata de total |
| 8 | Solo se incluyen contenidos de plataformas habilitadas | Contenidos de plataformas deshabilitadas no aparecen |
| 9 | Resultados ordenados por popularidad o año (configurable) | Parámetro `orderBy` acepta `popularidad` o `anio` |
| 10 | Paginación con `limit` y `offset` | Soporta paginación para catálogos grandes |

### Tests que desbloquea
- **TC-FILTRO-001 a TC-FILTRO-008** (excepto TC-FILTRO-009 que ya funciona)
- **TC-FILTRO-010**

---

## R-BE-07: Seguridad GraphQL

### Contexto
Los tests TC-SEG-020, TC-SEG-021, TC-SEG-022 verifican la seguridad a nivel de operaciones GraphQL.

### Requerimiento
Implementar validación de autenticación y autorización en todas las operaciones GraphQL.

### Criterios de Aceptación

| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Mutations de autenticación (login, registro) son públicas | No requieren token JWT |
| 2 | Todas las demás mutations requieren token JWT válido | Request sin header `Authorization` retorna `UNAUTHORIZED` |
| 3 | Mutation `cambiarEstadoPlataforma` requiere rol ADMINISTRADOR | Token con rol USUARIO retorna `FORBIDDEN` |
| 4 | Queries de datos propios (perfil, sala propia) requieren auth | Request sin token retorna `UNAUTHORIZED` |
| 5 | Queries públicas (plataformas, géneros) no requieren auth | Request sin token retorna datos correctamente |
| 6 | Subscriptions requieren token JWT en handshake WebSocket | Conexión sin token es rechazada con código 4401 |
| 7 | Token expirado retorna `UNAUTHORIZED` | Token con `exp` < tiempo actual es rechazado |
| 8 | Token de usuario normal no puede acceder a endpoints admin | Error `FORBIDDEN` con mensaje descriptivo |
| 9 | Rate limiting en mutations de autenticación | Máximo 10 intentos por minuto por IP |
| 10 | Validación de inputs en todas las mutations | Inputs inválidos retornan errores específicos (no `INTERNAL_ERROR`) |

### Tests que desbloquea
- **TC-SEG-020**: Operaciones GraphQL requieren autenticación
- **TC-SEG-021**: Cambiar estado plataforma requiere rol administrador
- **TC-SEG-022**: Subscriptions GraphQL requieren autenticación

---

## R-BE-08: Resultados y Calificación

### Contexto
Los tests TC-RES-001 a TC-RES-004 y TC-CAL-001 a TC-CAL-006 verifican el flujo post-sesión.

### Requerimiento
Implementar queries y mutations para obtener resultados y enviar calificación.

### Criterios de Aceptación

| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Query `obtenerResultados(salaId)` | Retorna matches, evaluaciones y estadísticas de la sala |
| 2 | Si no hubo matches, retorna `matches: []` y `totalMatches: 0` | Respuesta incluye array vacío |
| 3 | Mutation `calificarExperiencia(salaId, calificacion)` | Acepta valores del 1 al 10 |
| 4 | Calificación fuera de rango retorna error | Valor 0 o 11 retorna error `Calificación debe estar entre 1 y 10` |
| 5 | Calificación requiere sala FINALIZADA | Calificar en sala ACTIVA retorna error `La sala aún no ha finalizado` |
| 6 | Calificación se persiste y es consultable por admin | Query admin `obtenerRetroalimentacion` incluye la calificación |
| 7 | Query `obtenerRetroalimentacion` calcula promedio y distribución | Retorna `promedio`, `distribucion` (array de 10 elementos) |
| 8 | Filtros de fecha en retroalimentación | Parámetros `desde` y `hasta` filtran correctamente |
| 9 | Cada usuario puede calificar solo una vez por sala | Segunda calificación retorna error `Ya calificaste esta sesión` |

### Tests que desbloquea
- **TC-RES-001**: Ver resultados con matches
- **TC-RES-002**: Ver resultados sin matches
- **TC-RES-003**: Ir a calificación desde resultados
- **TC-RES-004**: Salir sin calificar desde resultados
- **TC-CAL-001 a TC-CAL-006**: Todos los tests de calificación

---

## R-BE-09: Endpoints de Prueba para Automatización

### Contexto
Los tests que requieren dos navegadores simultáneos (TC-SALA-013, TC-CHAT-005, TC-E2E-002, etc.) necesitan una forma de simular el comportamiento del segundo usuario desde el backend.

### Requerimiento
Implementar endpoints de administración que permitan simular acciones del segundo usuario para tests automatizados.

### Criterios de Aceptación

| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Mutation `simularEvaluacion(salaId, usuarioId, contenidoId, evaluacion)` | Simula que un usuario evaluó contenido |
| 2 | Mutation `simularMensaje(salaId, usuarioId, texto)` | Simula que un usuario envió un mensaje |
| 3 | Mutation `simularUnion(salaId, usuarioId)` | Simula que un usuario se unió a la sala |
| 4 | Mutation `simularDesconexion(salaId, usuarioId)` | Simula desconexión del usuario |
| 5 | Mutation `simularReconexion(salaId, usuarioId)` | Simula reconexión del usuario |
| 6 | Todas las simulaciones disparan las mismas notificaciones que acciones reales | El otro usuario recibe eventos WebSocket normalmente |
| 7 | Solo accesible con rol ADMINISTRADOR o en modo TEST | Producción no tiene estos endpoints |
| 8 | Logs detallados de todas las simulaciones | Cada simulación se registra en tabla `test_simulaciones` |

### Tests que desbloquea
- **TC-SALA-013**: Creador navega a filtros cuando compañero se une
- **TC-CHAT-005**: Recibir mensaje en tiempo real
- **TC-CHAT-008**: Mensajes del compañero alineados a la izquierda
- **TC-SWIPE-009**: Match detectado muestra banner
- **TC-SWIPE-010**: No hay match cuando uno da dislike
- **TC-E2E-002**: Flujo con match
- **TC-E2E-003**: Flujo sin matches
- **TC-E2E-009**: Flujo de chat en tiempo real
- **TC-E2E-010**: Flujo de desconexión y reconexión

---

## R-BE-10: Gestión de Estado de Conexión

### Contexto
Los tests TC-SWIPE-006, TC-CHAT-010, TC-E2E-010 verifican el comportamiento cuando un usuario se desconecta y reconecta.

### Requerimiento
Implementar detección de estado de conexión y notificación al compañero.

### Criterios de Aceptación

| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | heartbeat cada 5 segundos | Cliente WebSocket envía ping cada 5s |
| 2 | Desconexión detectada en < 15 segundos | Si no hay heartbeat en 15s, se marca como DESCONECTADO |
| 3 | Subscription `estadoCompanero` notifica cambio de estado | Evento `DESCONECTADO` emitido en < 2 segundos tras detección |
| 4 | Reconexión restaura sesión | Al reconectar, usuario recibe estado actual de la sala |
| 5 | Subscription notifica reconexión | Evento `RECONECTADO` emitido en < 2 segundos |
| 6 | Estado de conexión se persiste | Query `obtenerEstadoSala` incluye estado de cada usuario |
| 7 | Usuario desconectado no puede evaluar | Mutation `evaluarContenido` retorna error `Usuario desconectado` |
| 8 | Evaluaciones pendientes se sincronizan al reconectar | Si el usuario evaluó mientras estaba desconectado, se sincroniza |

### Tests que desbloquea
- **TC-SWIPE-006**: Botones deshabilitados cuando compañero desconectado
- **TC-CHAT-010**: Banner de reconexión
- **TC-E2E-010**: Flujo de desconexión y reconexión

---

## Resumen de Tests Desbloqueados

### Tests actualmente manuales → automatizables

| Suite | Tests Manuales | Requerimiento Backend Principal |
|-------|----------------|--------------------------------|
| test_salas.robot | TC-SALA-013, 014, 015 | R-BE-02, R-BE-09 |
| test_chat.robot | TC-CHAT-001, 002, 005, 006, 007, 008, 010, 011 | R-BE-03, R-BE-09 |
| test_filtros.robot | TC-FILTRO-001 a 008, 010 | R-BE-04, R-BE-06 |
| test_swipe.robot | TC-SWIPE-006, 007, 008, 009, 010 | R-BE-03, R-BE-05, R-BE-10 |
| test_e2e.robot | TC-E2E-001, 002, 003, 009, 010 | R-BE-03, R-BE-09 |
| test_admin.robot | TC-ADMIN-015 | R-BE-06 (ya existe, solo falta confirmar) |
| test_seguridad.robot | TC-SEG-020, 021, 022 | R-BE-07 |
| test_perfil_resultados.robot | TC-MATCH-001, TC-RES-001 a 004 | R-BE-05, R-BE-08 |

**Total: 45 tests manuales → 100% automatizables**

---

## Priorización de Implementación

### Fase 1: Fundamentos (Sprint 1-2)
- **R-BE-01**: Infraestructura de testing
- **R-BE-04**: Catálogo de contenido
- **R-BE-07**: Seguridad GraphQL (parcial)

### Fase 2: Salas y Filtros (Sprint 3-4)
- **R-BE-02**: Gestión completa de salas
- **R-BE-06**: Filtros de contenido
- **R-BE-07**: Seguridad GraphQL (completo)

### Fase 3: Tiempo Real (Sprint 5-6)
- **R-BE-03**: Comunicación en tiempo real
- **R-BE-05**: Match detection
- **R-BE-10**: Gestión de estado de conexión

### Fase 4: Completar Flujo (Sprint 7-8)
- **R-BE-08**: Resultados y calificación
- **R-BE-09**: Endpoints de prueba para automatización

---

## Dependencias Técnicas

| Componente | Versión Mínima | Propósito |
|------------|----------------|-----------|
| Spring Boot | 3.0+ | Framework backend |
| GraphQL Java | 20+ | API GraphQL |
| GraphQL Java Kickstart | 15+ | Subscriptions WebSocket |
| PostgreSQL | 14+ | Base de datos |
| Redis (opcional) | 7+ | Pub/Sub para múltiples instancias |
| Docker | 20+ | Ambiente de pruebas |

---

## Variables de Entorno Requeridas

```bash
# Modo de prueba
TEST_MODE=true                    # Habilita endpoints de test
SEED_TEST_DATA=true               # Ejecuta seed al iniciar

# WebSocket
WEBSOCKET_ENABLED=true
WEBSOCKET_PATH=/graphql
WEBSOCKET_PING_INTERVAL=5000      # 5 segundos

# Seguridad
JWT_SECRET=secret-key-for-tests
JWT_EXPIRATION=3600000            # 1 hora

# Base de datos
DB_HOST=localhost
DB_PORT=5433
DB_NAME=cinematch
DB_USER=postgres
DB_PASSWORD=1234
```

---

## Checklist de Verificación

Para validar que todos los requerimientos están implementados:

```bash
# 1. Verificar que el seed funciona
PGPASSWORD=1234 psql -h localhost -p 5433 -U postgres -d cinematch -f seed_test_data.sql
# Esperado: 30+ contenidos, 2 usuarios, 1 admin

# 2. Verificar que las subscriptions funcionan
# Conectar 2 clientes WebSocket y verificar que los mensajes se propagan

# 3. Ejecutar todos los tests de Robot Framework
cd documentacion/robot
./run_tests.sh
# Esperado: 100% de tests pasan, 0 manuales

# 4. Verificar seguridad GraphQL
# Intentar mutation sin token → error UNAUTHORIZED
# Intentar mutation admin con token usuario → error FORBIDDEN
```

---

## Notas Finales

- **No eliminar** los tests marcados como `manual`, solo remover la marca `[Tags] manual` y el `Pass Execution`
- **Mantener compatibilidad** con los tests que ya funcionan (auth, admin, seguridad básica)
- **Documentar** todos los endpoints de test en Swagger/GraphQL schema
- **Tests de integración** adicionales para validar cada requerimiento antes de aceptar
