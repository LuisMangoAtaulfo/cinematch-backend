# Requerimientos de Corrección — BACKEND
## Basado en ejecución del 10 de noviembre de 2026, 13:36

**Estado:** 107 pasan ✅ | 36 fallan ❌  
**Tests a recuperar con backend:** 8 (el resto son problemas de frontend)

---

## Resumen de fallos que requieren backend

| # | Problema | Error | Tests | Prioridad |
|---|----------|-------|-------|-----------|
| 1 | Mensajes de error GraphQL genéricos | Element 'xpath=...' not visible | 4 | 🟡 |
| 2 | API de métricas sin soporte de filtros | "Filtro activo" no aparece | 2 | 🟡 |
| 3 | API de retroalimentación sin filtros | "Filtro activo" no aparece | 1 | 🟡 |
| 4 | Query de perfil no retorna datos | '' should not be empty | 1 | 🟢 |

---

## R-BE-01: Mensajes de error específicos en GraphQL

### Estado
🟡 **IMPORTANTE** — 4 tests fallan

### Problema
El backend retorna errores genéricos tipo `"INTERNAL_ERROR"` o mensajes que no coinciden con los textos esperados por los tests.

### Tests afectados

| Test | Escenario | Mensaje esperado | Error actual |
|------|-----------|------------------|--------------|
| TC-ADMIN-002 | Login admin con credenciales incorrectas | "Credenciales incorrectas" | No visible después de 10s |
| TC-SALA-007 | Unirse con código inexistente | "Código de sala inválido" | No visible después de 10s |
| TC-PERF-004 | Actualizar perfil con nombre vacío | "El nombre es requerido" | No visible después de 10s |
| TC-E2E-006 | Unirse a sala inexistente (E2E) | "Código de sala inválido" | Hereda de TC-SALA-007 |

### Diagnóstico

El `GraphQLExceptionResolver` probablemente captura las excepciones pero retorna mensajes genéricos. Se necesita verificar qué retorna realmente el backend:

```bash
# Probar login admin con credenciales incorrectas
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { adminLogin(correo: \"admin@cinematch.com\", password: \"WrongPassword\") { token } }"
  }'

# Probar unirse con código inválido
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "query": "mutation { unirseASala(codigo: \"ZZZZZZ\") { id } }"
  }'
```

### Solución

Archivo: `backend/src/main/java/com/cinematchbackend/config/GraphQLExceptionResolver.java`

```java
@Component
public class GraphQLExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        // Login con credenciales incorrectas
        if (ex instanceof BadCredentialsException) {
            return GraphqlErrorBuilder.newError(env)
                .message("Credenciales incorrectas")
                .errorType(ErrorType.BAD_REQUEST)
                .build();
        }
        
        // Código de sala inválido
        if (ex instanceof CodigoSalaInvalidoException 
            || ex instanceof SalaNoEncontradaException) {
            return GraphqlErrorBuilder.newError(env)
                .message("Código de sala inválido")
                .errorType(ErrorType.BAD_REQUEST)
                .build();
        }
        
        // Nombre requerido en perfil
        if (ex instanceof NombreRequeridoException 
            || ex instanceof CampoRequeridoException) {
            return GraphqlErrorBuilder.newError(env)
                .message("El nombre es requerido")
                .errorType(ErrorType.BAD_REQUEST)
                .build();
        }
        
        // Usuario no encontrado
        if (ex instanceof UsuarioNoEncontradoException) {
            return GraphqlErrorBuilder.newError(env)
                .message("Usuario no encontrado")
                .errorType(ErrorType.BAD_REQUEST)
                .build();
        }
        
        // Correo duplicado
        if (ex instanceof EmailAlreadyExistsException) {
            return GraphqlErrorBuilder.newError(env)
                .message("El correo ya está en uso")
                .errorType(ErrorType.BAD_REQUEST)
                .build();
        }
        
        // Fallback genérico
        return GraphqlErrorBuilder.newError(env)
            .message(ex.getMessage())
            .errorType(ErrorType.INTERNAL_ERROR)
            .build();
    }
}
```

Archivos de excepciones a crear/verificar:

```java
// backend/src/main/java/com/cinematchbackend/exception/CodigoSalaInvalidoException.java
public class CodigoSalaInvalidoException extends RuntimeException {
    public CodigoSalaInvalidoException() {
        super("Código de sala inválido");
    }
    public CodigoSalaInvalidoException(String message) {
        super(message);
    }
}

// backend/src/main/java/com/cinematchbackend/exception/NombreRequeridoException.java
public class NombreRequeridoException extends RuntimeException {
    public NombreRequeridoException() {
        super("El nombre es requerido");
    }
}
```

Validaciones en los resolvers:

```java
// AdminResolver.java
@MutationMapping
public AdminLoginResponse adminLogin(
    @Argument String correo,
    @Argument String password
) {
    try {
        // ... autenticación
    } catch (BadCredentialsException e) {
        throw new BadCredentialsException("Credenciales incorrectas");
    }
}

// SalaResolver.java
@MutationMapping
public Sala unirseASala(
    @Argument String codigo,
    @AuthenticationPrincipal Usuario usuario
) {
    Sala sala = salaRepository.findByCodigo(codigo.toUpperCase())
        .orElseThrow(CodigoSalaInvalidoException::new);
    // ...
}

// PerfilResolver.java
@MutationMapping
public Usuario actualizarPerfil(
    @Argument String nombre,
    @Argument String descripcion,
    @AuthenticationPrincipal Usuario usuario
) {
    if (nombre == null || nombre.isBlank()) {
        throw new NombreRequeridoException();
    }
    usuario.setNombre(nombre);
    usuario.setDescripcion(descripcion);
    return usuarioRepository.save(usuario);
}
```

### Criterios de aceptación

| # | Criterio | Verificación |
|---|----------|-------------|
| 1 | Login admin con password incorrecto retorna "Credenciales incorrectas" | GraphQL `errors[0].message = "Credenciales incorrectas"` |
| 2 | Unirse con código inexistente retorna "Código de sala inválido" | GraphQL `errors[0].message = "Código de sala inválido"` |
| 3 | Actualizar perfil con nombre vacío retorna "El nombre es requerido" | GraphQL `errors[0].message = "El nombre es requerido"` |
| 4 | Ningún error retorna "INTERNAL_ERROR" para casos de negocio | Todos los errores de negocio tienen mensajes específicos |

### Archivos
- `backend/src/main/java/com/cinematchbackend/config/GraphQLExceptionResolver.java`
- `backend/src/main/java/com/cinematchbackend/exception/CodigoSalaInvalidoException.java` (crear/verificar)
- `backend/src/main/java/com/cinematchbackend/exception/NombreRequeridoException.java` (crear)
- `backend/src/main/java/com/cinematchbackend/graphql/AdminResolver.java`
- `backend/src/main/java/com/cinematchbackend/graphql/SalaResolver.java`
- `backend/src/main/java/com/cinematchbackend/graphql/PerfilResolver.java`

---

## R-BE-02: API de métricas con filtros de fecha

### Estado
🟡 **IMPORTANTE** — 2 tests fallan

### Problema
La query `obtenerMetricas` no soporta filtros de fecha o no retorna datos filtrados.

### Tests afectados

| Test | Error |
|------|-------|
| TC-ADMIN-011 | "Filtro activo" no aparece después de aplicar filtro de fechas |
| TC-E2E-004 | Hereda el problema (flujo admin completo) |

### Diagnóstico

El test aplica un filtro de fechas y espera que el frontend muestre "Filtro activo". Para que esto funcione:
1. El frontend debe enviar los parámetros `desde` y `hasta` al backend
2. El backend debe aceptar esos parámetros y retornar datos filtrados
3. El frontend muestra "Filtro activo" si el backend confirma que hay filtro

### Solución

Archivo: `backend/src/main/resources/schema.graphqls`

```graphql
type Query {
    obtenerMetricas(
        desde: String
        hasta: String
    ): MetricasResponse!
}

type MetricasResponse {
    salasActivas: Int!
    salasTotales: Int!
    matchesGenerados: Int!
    promedioCalificacion: Float
    filtroActivo: Boolean!
}
```

Archivo: `backend/src/main/java/com/cinematchbackend/graphql/AdminResolver.java`

```java
@QueryMapping
public MetricasResponse obtenerMetricas(
    @Argument String desde,
    @Argument String hasta,
    @AuthenticationPrincipal Usuario usuario
) {
    validarAdmin(usuario);
    
    LocalDate fechaDesde = desde != null && !desde.isBlank() 
        ? LocalDate.parse(desde) : null;
    LocalDate fechaHasta = hasta != null && !hasta.isBlank() 
        ? LocalDate.parse(hasta) : null;
    
    boolean filtroActivo = fechaDesde != null || fechaHasta != null;
    
    long salasActivas = salaRepository.countByEstadoYFechas("ACTIVA", fechaDesde, fechaHasta);
    long salasTotales = salaRepository.countByFechas(fechaDesde, fechaHasta);
    long matchesGenerados = matchRepository.countByFechas(fechaDesde, fechaHasta);
    Double promedio = calificacionRepository.promedioByFechas(fechaDesde, fechaHasta);
    
    return new MetricasResponse(
        salasActivas, salasTotales, matchesGenerados, promedio, filtroActivo
    );
}
```

Archivos de repositorio:

```java
// SalaRepository.java
@Query("SELECT COUNT(s) FROM Sala s WHERE " +
       "(:estado IS NULL OR s.estado = :estado) AND " +
       "(:desde IS NULL OR s.fechaCreacion >= :desde) AND " +
       "(:hasta IS NULL OR s.fechaCreacion <= :hasta)")
long countByEstadoYFechas(
    @Param("estado") String estado,
    @Param("desde") LocalDate desde,
    @Param("hasta") LocalDate hasta
);

@Query("SELECT COUNT(s) FROM Sala s WHERE " +
       "(:desde IS NULL OR s.fechaCreacion >= :desde) AND " +
       "(:hasta IS NULL OR s.fechaCreacion <= :hasta)")
long countByFechas(
    @Param("desde") LocalDate desde,
    @Param("hasta") LocalDate hasta
);
```

```java
// MatchRepository.java
@Query("SELECT COUNT(m) FROM Match m WHERE " +
       "(:desde IS NULL OR m.fecha >= :desde) AND " +
       "(:hasta IS NULL OR m.fecha <= :hasta)")
long countByFechas(
    @Param("desde") LocalDate desde,
    @Param("hasta") LocalDate hasta
);
```

### Criterios de aceptación

| # | Criterio | Verificación |
|---|----------|-------------|
| 1 | Query acepta parámetros `desde` y `hasta` | Schema GraphQL incluye los argumentos |
| 2 | Sin filtros retorna todos los datos | Datos sin restricción de fecha |
| 3 | Con filtro `desde` solo incluye datos >= fecha | Conteos reflejan el filtro |
| 4 | Con filtro `hasta` solo incluye datos <= fecha | Conteos reflejan el filtro |
| 5 | Response incluye campo `filtroActivo` | `true` cuando hay filtro, `false` cuando no |

### Archivos
- `backend/src/main/resources/schema.graphqls`
- `backend/src/main/java/com/cinematchbackend/graphql/AdminResolver.java`
- `backend/src/main/java/com/cinematchbackend/dto/MetricasResponse.java`
- `backend/src/main/java/com/cinematchbackend/repository/SalaRepository.java`
- `backend/src/main/java/com/cinematchbackend/repository/MatchRepository.java`

---

## R-BE-03: API de retroalimentación con filtros de fecha

### Estado
🟡 **IMPORTANTE** — 1 test falla

### Problema
La query `obtenerRetroalimentacion` no soporta filtros de fecha.

### Test afectado

| Test | Error |
|------|-------|
| TC-ADMIN-018 | "Filtro activo" no aparece después de aplicar filtro |

### Solución

Archivo: `backend/src/main/resources/schema.graphqls`

```graphql
type Query {
    obtenerRetroalimentacion(
        desde: String
        hasta: String
    ): RetroalimentacionResponse!
}

type RetroalimentacionResponse {
    promedio: Float
    distribucion: [DistribucionItem!]!
    filtroActivo: Boolean!
}

type DistribucionItem {
    calificacion: Int!
    cantidad: Int!
}
```

Archivo: `backend/src/main/java/com/cinematchbackend/graphql/AdminResolver.java`

```java
@QueryMapping
public RetroalimentacionResponse obtenerRetroalimentacion(
    @Argument String desde,
    @Argument String hasta,
    @AuthenticationPrincipal Usuario usuario
) {
    validarAdmin(usuario);
    
    LocalDate fechaDesde = desde != null && !desde.isBlank() 
        ? LocalDate.parse(desde) : null;
    LocalDate fechaHasta = hasta != null && !hasta.isBlank() 
        ? LocalDate.parse(hasta) : null;
    
    boolean filtroActivo = fechaDesde != null || fechaHasta != null;
    
    Double promedio = calificacionRepository.promedioByFechas(fechaDesde, fechaHasta);
    List<DistribucionItem> distribucion = 
        calificacionRepository.distribucionByFechas(fechaDesde, fechaHasta);
    
    return new RetroalimentacionResponse(promedio, distribucion, filtroActivo);
}
```

Archivo: `backend/src/main/java/com/cinematchbackend/repository/CalificacionRepository.java`

```java
@Query("SELECT AVG(c.valor) FROM Calificacion c WHERE " +
       "(:desde IS NULL OR c.fecha >= :desde) AND " +
       "(:hasta IS NULL OR c.fecha <= :hasta)")
Double promedioByFechas(
    @Param("desde") LocalDate desde,
    @Param("hasta") LocalDate hasta
);

@Query("SELECT new com.cinematchbackend.dto.DistribucionItem(c.valor, COUNT(c)) " +
       "FROM Calificacion c WHERE " +
       "(:desde IS NULL OR c.fecha >= :desde) AND " +
       "(:hasta IS NULL OR c.fecha <= :hasta) " +
       "GROUP BY c.valor ORDER BY c.valor")
List<DistribucionItem> distribucionByFechas(
    @Param("desde") LocalDate desde,
    @Param("hasta") LocalDate hasta
);
```

### Criterios de aceptación

| # | Criterio | Verificación |
|---|----------|-------------|
| 1 | Query acepta `desde` y `hasta` | Schema incluye argumentos |
| 2 | Sin filtros retorna todas las calificaciones | Promedio y distribución completos |
| 3 | Con filtros retorna datos del rango | Solo calificaciones dentro del rango |
| 4 | Response incluye `filtroActivo` | `true` cuando hay filtro |
| 5 | Distribución tiene items con `calificacion` y `cantidad` | Array con datos correctos |

### Archivos
- `backend/src/main/resources/schema.graphqls`
- `backend/src/main/java/com/cinematchbackend/graphql/AdminResolver.java`
- `backend/src/main/java/com/cinematchbackend/repository/CalificacionRepository.java`
- `backend/src/main/java/com/cinematchbackend/dto/RetroalimentacionResponse.java` (crear/actualizar)
- `backend/src/main/java/com/cinematchbackend/dto/DistribucionItem.java` (crear/verificar)

---

## R-BE-04: Query de perfil debe retornar datos del usuario

### Estado
🟢 **MENOR** — 1 test falla

### Error
```
'' should not be empty.
```

### Test afectado

| Test | Descripción |
|------|-------------|
| TC-E2E-007 | Flujo de perfil completo — al entrar a /perfil el campo nombre está vacío |

### Diagnóstico

El test hace login, navega a /perfil, y verifica que el campo nombre no esté vacío. El problema puede ser:
1. El backend no retorna los datos del perfil en la query
2. La query `miPerfil` o `obtenerPerfil` no existe o retorna null
3. El campo `nombre` no está incluido en la respuesta

### Solución

Verificar que la query de perfil exista y retorne los datos:

Archivo: `backend/src/main/resources/schema.graphqls`

```graphql
type Query {
    miPerfil: Usuario!
}

type Usuario {
    id: ID!
    nombre: String!
    correo: String!
    descripcion: String
}
```

Archivo: `backend/src/main/java/com/cinematchbackend/graphql/PerfilResolver.java`

```java
@QueryMapping
public Usuario miPerfil(@AuthenticationPrincipal Usuario usuario) {
    if (usuario == null) {
        throw new UnauthorizedException("Autenticación requerida");
    }
    // Retornar el usuario completo desde la BD para asegurar datos frescos
    return usuarioRepository.findById(usuario.getId())
        .orElseThrow(() -> new UsuarioNoEncontradoException());
}
```

Verificar que el campo `nombre` no sea null en la base de datos:

```sql
SELECT id, nombre, correo, descripcion FROM usuarios WHERE correo = 'test1@cinematch.com';
-- Asegurar que nombre no sea NULL
```

### Criterios de aceptación

| # | Criterio | Verificación |
|---|----------|-------------|
| 1 | Query `miPerfil` retorna el usuario autenticado | Response incluye datos del usuario |
| 2 | El campo `nombre` no está vacío | `nombre != null && nombre != ""` |
| 3 | El campo `correo` es correcto | Coincide con el usuario logueado |
| 4 | Sin autenticación retorna error | Error UNAUTHORIZED |

### Archivos
- `backend/src/main/java/com/cinematchbackend/graphql/PerfilResolver.java`
- `backend/src/main/resources/schema.graphqls`
- Seed data: verificar que `seed_usuarios_robot.sql` incluye nombre para los usuarios de prueba

---

## Resumen

| # | Requerimiento | Tests | Prioridad |
|---|---------------|-------|-----------|
| R-BE-01 | Mensajes de error específicos | 4 | 🟡 |
| R-BE-02 | Métricas con filtros de fecha | 2 | 🟡 |
| R-BE-03 | Retroalimentación con filtros de fecha | 1 | 🟡 |
| R-BE-04 | Query de perfil retorna datos | 1 | 🟢 |
| | **TOTAL** | **8** | |

### Nota sobre los demás tests

Los **28 tests restantes** de los 36 que fallan son causados por problemas exclusivos del frontend:

| Causa frontend | Tests |
|---------------|-------|
| Selector "Crear sala" no existe | 17 |
| Dashboard admin HTML incompleto | 3 |
| Guards no redirigen | 3 |
| Botones/enlaces de navegación | 4 |
| Filtros no navegan a /swipe | 1 |

Estos no requieren cambios en el backend — solo correcciones en los componentes Angular.

### Orden de implementación

```
1. R-BE-01 (4 tests)  ← mensajes de error en GraphQL
2. R-BE-02 (2 tests)  ← filtros en métricas
3. R-BE-03 (1 test)   ← filtros en retroalimentación
4. R-BE-04 (1 test)   ← query de perfil
```
