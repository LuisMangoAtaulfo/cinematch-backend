# Requerimientos Backend — CineMatch

> Documento derivado del análisis de `robot/ESTADO_TESTS.md` y la suite de pruebas Robot Framework.
> Objetivo: que todos los tests automatizados pasen sin intervención manual.

---

## Resumen Ejecutivo

Actualmente **~45 tests están marcados como manuales o bloqueados** por dependencias no resueltas del backend. Estos requerimientos buscan habilitar la automatización completa del ciclo de vida de una sala: creación → espera → filtros → swipe → match → chat → resultados → calificación.

---

## R-BE-01: Sistema de Fixtures para Tests Automatizados

### Contexto
Los tests de salas, chat, filtros, swipe y E2E requieren una sala activa con datos precargados. No existe actualmente un mecanismo para crear salas de prueba de forma automática vía API.

### Requerimiento
Implementar endpoints o mutations GraphQL de administración que permitan crear salas de prueba con estado controlado desde los scripts de Robot Framework.

### Criterios de Aceptación
| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Mutation `crearSalaTest` que cree una sala y devuelva su código | La mutation devuelve un código alfanumérico de 6 caracteres |
| 2 | Mutation `unirUsuarioTest` que simule un segundo usuario uniéndose | La sala pasa a estado ACTIVA en < 2 segundos |
| 3 | Mutation `finalizarSalaTest` que cierre una sala | La sala pasa a estado FINALIZADA |
| 4 | Las mutations solo son accesibles con rol ADMINISTRADOR o usuario de prueba | Requests sin token devuelven HTTP 401 |
| 5 | Script SQL de seed para datos de prueba (catálogo de contenido) | Al ejecutar el script, existen ≥ 20 contenidos disponibles para swipe |

### Tests que desbloquea
- TC-SALA-001 a TC-SALA-012 (automatizables)
- TC-CHAT-001 a TC-CHAT-013 (automatizables)
- TC-FILTRO-001 a TC-FILTRO-010 (automatizables)
- TC-SWIPE-001 a TC-SWIPE-013 (automatizables)
- TC-E2E-001, TC-E2E-002, TC-E2E-003

---

## R-BE-02: Gestión del Ciclo de Vida de Salas

### Contexto
Los tests TC-SALA-011 a TC-SALA-015 requieren que el backend maneje correctamente las transiciones de estado de una sala y que el frontend pueda detectar estos cambios.

### Requerimiento
Implementar y exponer las transiciones de estado de sala de forma confiable: `CREADA → ESPERANDO_COMPAÑERO → ACTIVA → FINALIZADA → CANCELADA`.

### Criterios de Aceptación
| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Crear sala genera código alfanumérico de 6 caracteres en mayúsculas | 100 creaciones → todas cumplen regex `^[A-Z0-9]{6}$` |
| 2 | Cuando un segundo usuario se une, la sala cambia a ACTIVA | La query `obtenerSala` refleja el cambio en < 3 segundos |
| 3 | Solo el creador puede finalizar la sala | Un mutation `finalizarSala` ejecutada por el invitado retorna error de autorización |
| 4 | El creador puede cancelar una sala en estado ESPERANDO_COMPAÑERO | La sala pasa a CANCELADA y los recursos se liberan |
| 5 | El invitado puede cancelar desde ESPERANDO_COMPAÑERO | Redirección funciona y la sala se limpia correctamente |
| 6 | Sala con código inexistente retorna error descriptivo | El mensaje contiene "Código de sala inválido" |
| 7 | Unirse con campo vacío retorna error de validación | El mensaje contiene "Ingresa un código" o similar |

### Tests que desbloquea
- TC-SALA-002 (formato de código)
- TC-SALA-004 (cancelar sala en espera)
- TC-SALA-006 (unirse con código válido)
- TC-SALA-007 (código inválido)
- TC-SALA-011, TC-SALA-012 (espera de filtros)
- TC-SALA-013 (polling / sincronización)
- TC-SALA-014, TC-SALA-015 (finalizar sala)

---

## R-BE-03: Comunicación en Tiempo Real vía WebSocket/GraphQL Subscriptions

### Contexto
Los tests de chat (TC-CHAT) y match detection (TC-SWIPE-009, TC-SWIPE-010) requieren comunicación en tiempo real entre dos usuarios en una sala activa. Actualmente los tests están marcados como manuales porque no existe infraestructura de tiempo real.

### Requerimiento
Implementar GraphQL Subscriptions (o WebSocket nativo) para:
1. Mensajería en tiempo real del chat
2. Notificación de matches detectados
3. Notificación de desconexión de compañero

### Criterios de Aceptación
| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Mensaje enviado por Usuario A aparece en Usuario B en < 2 segundos | Medido con dos clientes WebSocket simultáneos |
| 2 | Cuando ambos usuarios dan "like" al mismo contenido, se genera un match y se notifica a ambos | La subscription `matchDetectado` emite evento en < 3 segundos |
| 3 | Si un usuario pierde conexión, el otro recibe notificación de desconexión | La subscription `estadoCompanero` emite evento `DESCONECTADO` |
| 4 | Al reconectar, el historial de mensajes se carga correctamente | La query `obtenerMensajes` retorna todos los mensajes de la sala |
| 5 | Las subscriptions requieren autenticación (token JWT en WebSocket handshake) | Conexión sin token es rechazada con código 4401 |
| 6 | Las subscriptions solo funcionan dentro de una sala activa | Subscription sin sala activa retorna error descriptivo |

### Tests que desbloquea
- TC-CHAT-001 (enviar mensaje)
- TC-CHAT-002 (enviar con Enter)
- TC-CHAT-005 (recibir en tiempo real)
- TC-CHAT-006 (historial de chat)
- TC-CHAT-007, TC-CHAT-008 (alineación de mensajes)
- TC-CHAT-010 (reconexión)
- TC-CHAT-011 (banner de match)
- TC-SWIPE-009 (match detectado)
- TC-SWIPE-010 (no match con dislike)
- TC-E2E-002, TC-E2E-003, TC-E2E-009 (flujos E2E con match/chat)

---

## R-BE-04: Catálogo de Contenido para Pruebas

### Contexto
Los tests de swipe (TC-SWIPE) y filtros (TC-FILTRO) requieren contenido en la base de datos con variedad de tipos, géneros, plataformas y años. Actualmente la base de datos puede no tener datos suficientes para probar todos los filtros.

### Requerimiento
Crear un script de seed que inserte un catálogo diverso de contenido (películas y series) en la base de datos de desarrollo/pruebas.

### Criterios de Aceptación
| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | El catálogo contiene ≥ 20 contenidos | `SELECT COUNT(*) FROM contenidos` retorna ≥ 20 |
| 2 | Hay al menos 2 tipos: Película y Serie | `SELECT DISTINCT tipo FROM contenidos` retorna ambos |
| 3 | Hay al menos 5 géneros diferentes (acción, comedia, drama, terror, ciencia ficción) | `SELECT DISTINCT genero FROM contenidos` retorna ≥ 5 |
| 4 | Hay al menos 4 plataformas habilitadas (Netflix, Disney+, HBO, Prime Video) | `SELECT COUNT(*) FROM plataformas WHERE activa = true` retorna ≥ 4 |
| 5 | Los años van de 1980 al año actual | `MIN(anio)` ≥ 1980 y `MAX(anio)` ≥ 2024 |
| 6 | Cada contenido tiene título, año, género y al menos una plataforma asociada | No hay NULLs en campos obligatorios |

### Tests que desbloquea
- TC-SWIPE-001 a TC-SWIPE-005 (evaluación de contenido)
- TC-FILTRO-001 a TC-FILTRO-008 (aplicación de filtros)
- TC-SWIPE-007 (catálogo agotado)
- TC-SWIPE-008 (sin resultados para filtros)

---

## R-BE-05: Lógica de Match Detection

### Contexto
Los tests TC-SWIPE-009 y TC-SWIPE-010 verifican que el sistema detecte matches cuando ambos usuarios dan "like" al mismo contenido y no genere match si alguno da "dislike".

### Requerimiento
Implementar la lógica de detección de matches en el backend que compare las evaluaciones de ambos usuarios en una sala activa.

### Criterios de Aceptación
| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Match se detecta cuando ambos usuarios evalúan el mismo contenido con LIKE | Al dar ambos LIKE al contenido X, la tabla `matches` tiene un registro para ese contenido y sala |
| 2 | No hay match si al menos un usuario da DISLIKE | No se crea registro en `matches` |
| 3 | La detección ocurre en tiempo real (< 3 segundos) | La subscription de match notifica en < 3s |
| 4 | El match incluye información del contenido (título, año, plataformas) | El objeto match retornado tiene los campos completos |
| 5 | Los matches se persisten y son consultables después | La query `obtenerMatches` retorna los matches de la sala |

### Tests que desbloquea
- TC-SWIPE-009 (banner de match)
- TC-SWIPE-010 (no match con dislike)
- TC-MATCH-001 (ver lista de matches)
- TC-E2E-002 (flujo con match)

---

## R-BE-06: Filtrado de Contenido en Backend

### Contexto
Los tests TC-FILTRO-001 a TC-FILTRO-010 verifican que los filtros (tipo, género, plataforma, año) funcionen correctamente y devuelvan solo el contenido que coincide.

### Requerimiento
Implementar un endpoint/mutation que acepte filtros y retorne el contenido que coincida. Los filtros deben ser combinables.

### Criterios de Aceptación
| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Filtrar por tipo "Película" retorna solo películas | Todos los resultados tienen `tipo = 'PELICULA'` |
| 2 | Filtrar por tipo "Serie" retorna solo series | Todos los resultados tienen `tipo = 'SERIE'` |
| 3 | Filtrar por género retorna solo contenidos de ese género | Todos los resultados tienen el género solicitado |
| 4 | Filtrar por plataforma retorna solo contenidos disponibles en esa plataforma | Cada resultado está disponible en la plataforma filtrada |
| 5 | Filtrar por año retorna solo contenidos de ese año | Todos los resultados tienen el año solicitado |
| 6 | Filtros combinados funcionan correctamente (tipo + género + año) | La intersección de criterios se respeta |
| 7 | Sin filtros retorna todo el catálogo habilitado | Retorna ≥ 20 contenidos |
| 8 | Solo se muestran plataformas habilitadas en el dropdown | Query `plataformas` retorna solo `activa = true` |
| 9 | El dropdown de años va del año actual a 1980 | `SELECT DISTINCT anio ORDER BY anio DESC` → primero 2026, último 1980 |
| 10 | Si no hay resultados para los filtros, se retorna array vacío con indicador | La respuesta incluye `totalResultados: 0` |

### Tests que desbloquea
- TC-FILTRO-001 a TC-FILTRO-010 (excepto TC-FILTRO-009 que ya funciona)

---

## R-BE-07: Protección de Operations GraphQL

### Contexto
Los tests TC-SEG-020, TC-SEG-021 y TC-SEG-022 verifican la seguridad a nivel de GraphQL. Actualmente están marcados como manuales.

### Requerimiento
Implementar validación de autenticación y autorización en las operaciones GraphQL (queries, mutations y subscriptions).

### Criterios de Aceptación
| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Mutations de usuario (login, registro, actualizarPerfil) requieren token válido JWT | Request sin header `Authorization` retorna error `UNAUTHORIZED` |
| 2 | Mutation `cambiarEstadoPlataforma` requiere rol ADMINISTRADOR | Request con token de USUARIO retorna error `FORBIDDEN` |
| 3 | Subscriptions requieren token JWT en el handshake de WebSocket | Conexión WebSocket sin token es rechazada |
| 4 | Queries de datos sensibles (perfil, sala propia) requieren autenticación | Request sin token retorna error `UNAUTHORIZED` |
| 5 | Queries públicas (plataformas habilitadas) no requieren autenticación | Request sin token retorna datos correctamente |

### Tests que desbloquea
- TC-SEG-020 (mutations requieren auth)
- TC-SEG-021 (cambiarEstadoPlataforma requiere admin)
- TC-SEG-022 (subscriptions requieren auth)

---

## R-BE-08: API de Resultados y Calificación

### Contexto
Los tests TC-RES-001 a TC-RES-004 y TC-CAL-001 a TC-CAL-006 verifican el flujo de resultados post-sesión y calificación de la experiencia.

### Requerimiento
Implementar las operations GraphQL para obtener resultados de una sesión finalizada y para enviar calificación.

### Criterios de Aceptación
| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Query `obtenerResultados` retorna los matches de la sesión finalizada | Contiene lista de matches con títulos y plataformas |
| 2 | Si no hubo matches, retorna lista vacía con indicador | `matches: []` y `totalMatches: 0` |
| 3 | Mutation `calificarExperiencia` acepta valores del 1 al 10 | Valor fuera de rango retorna error de validación |
| 4 | La calificación se persiste y es consultable por el admin | Query `obtenerRetroalimentacion` incluye la nueva calificación |
| 5 | La calificación requiere que la sala esté FINALIZADA | Calificar en sala ACTIVA retorna error |

### Tests que desbloquea
- TC-RES-001 a TC-RES-004
- TC-CAL-001 a TC-CAL-006

---

## Priorización

| Prioridad | Requerimiento | Impacto en Tests |
|-----------|---------------|-----------------|
| 🔴 P0 | R-BE-02 (Ciclo de vida de salas) | 12 tests de salas |
| 🔴 P0 | R-BE-03 (WebSocket/Subscriptions) | 13 tests de chat + matches |
| 🔴 P0 | R-BE-04 (Seed de catálogo) | 13 tests de swipe + 10 de filtros |
| 🟡 P1 | R-BE-01 (Fixtures para tests) | Habilita automatización de todos los suites |
| 🟡 P1 | R-BE-05 (Match detection) | 4 tests de match |
| 🟡 P1 | R-BE-06 (Filtrado de contenido) | 9 tests de filtros |
| 🟢 P2 | R-BE-07 (Protección GraphQL) | 3 tests de seguridad |
| 🟢 P2 | R-BE-08 (Resultados y calificación) | 10 tests de resultados/calificación |

---

## Dependencias Técnicas

- **PostgreSQL** en puerto 5433 (base de datos `cinematch`)
- **Spring Boot** backend en puerto 8080
- **GraphQL** como capa de API
- **GraphQL Subscriptions** (necesita configuración de `graphql-java-kickstart` o `Netflix DGS`)
- **WebSocket** (STOMP sobre WebSocket o GraphQL WS protocol)

## Notas

- Los usuarios de prueba ya existen: `test1@cinematch.com`, `test2@cinematch.com`, `admin@cinematch.com`
- El `GraphQLExceptionResolver` ya está creado para mapear excepciones a errores GraphQL
- El seed de usuarios ya está implementado en `seed_usuarios_robot.sql`
