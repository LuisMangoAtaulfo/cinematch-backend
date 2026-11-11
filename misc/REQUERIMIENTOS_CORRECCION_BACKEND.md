# Requerimientos de Corrección — BACKEND
## Basado en fallos de ejecución del 10 de noviembre de 2026

**Estado actual:** 103 tests pasan, 40 fallan  
**Objetivo:** Corregir el backend para soportar los tests fallidos  
**Tests a recuperar:** 12 tests (30% de los 40 fallidos)

**Nota:** La mayoría de los fallos (28 de 40) son problemas del frontend. Este documento cubre los problemas del backend que contribuyen a los fallos.

---

## Índice

1. [Mensajes de error GraphQL](#r-be-01-corregir-mensajes-de-error-graphql)
2. [Validación de autenticación en backend](#r-be-02-validación-de-autenticación)
3. [Validación de roles de administrador](#r-be-03-validación-de-roles-admin)
4. [API de métricas con filtros](#r-be-04-api-de-métricas-con-filtros)
5. [API de plataformas con toggle](#r-be-05-api-de-plataformas-con-toggle)
6. [API de retroalimentación con filtros](#r-be-06-api-de-retroalimentación-con-filtros)

---

## R-BE-01: Corregir Mensajes de Error GraphQL

### Estado
🟡 **IMPORTANTE** — 8 tests fallan (comparte causa con frontend)

### Problema
El backend GraphQL no retorna mensajes de error específicos, o retorna mensajes genéricos que el frontend no puede mostrar correctamente.

### Tests Afectados

| Test ID | Descripción | Mensaje Esperado | Problema Backend |
|---------|-------------|------------------|------------------|
| TC-AUTH-006 | Login con campos vacíos | "Completa todos los campos" | Backend no valida o retorna error genérico |
| TC-AUTH-007 | Login con correo vacío | "Completa todos los campos" | Backend no valida o retorna error genérico |
| TC-AUTH-008 | Login con contraseña vacía | "Completa todos los campos" | Backend no valida o retorna error genérico |
| TC-AUTH-015 | Registro con campos vacíos | "Completa todos los campos" | Backend no valida o retorna error genérico |
| TC-ADMIN-002 | Login admin credenciales incorrectas | "Credenciales incorrectas" | Backend retorna "INTERNAL_ERROR" |
| TC-ADMIN-004 | Login admin campos vacíos | "Completa todos los campos" | Backend no valida o retorna error genérico |
| TC-SALA-007 | Unirse con código inválido | "Código de sala inválido" | Backend retorna error genérico |
| TC-PERF-004 | Perfil nombre vacío | "El nombre es requerido" | Backend no valida |

### Requerimiento

**1.1: Crear exception resolver para GraphQL**

Archivo: `backend/src/main/java/com/cinematchbackend/config/GraphQLExceptionResolver.java`

```java
package com.cinematchbackend.config;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

@Component
public class GraphQLExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof CamposVaciosException) {
            return GraphqlErrorBuilder.newError(env)
                .message("Completa todos los campos")
                .errorType(ErrorType.BAD_REQUEST)
                .build();
        }
        
        if (ex instanceof BadCredentialsException) {
            return GraphqlErrorBuilder.newError(env)
                .message("Credenciales incorrectas")
                .errorType(ErrorType.BAD_REQUEST)
                .build();
        }
        
        if (ex instanceof CodigoSalaInvalidoException) {
            return GraphqlErrorBuilder.newError(env)
                .message("Código de sala inválido")
                .errorType(ErrorType.BAD_REQUEST)
                .build();
        }
        
        if (ex instanceof NombreRequeridoException) {
            return GraphqlErrorBuilder.newError(env)
                .message("El nombre es requerido")
                .errorType(ErrorType.BAD_REQUEST)
                .build();
        }
        
        if (ex instanceof UsuarioNoEncontradoException) {
            return GraphqlErrorBuilder.newError(env)
                .message("Usuario no encontrado")
                .errorType(ErrorType.BAD_REQUEST)
                .build();
        }
        
        if (ex instanceof EmailAlreadyExistsException) {
            return GraphqlErrorBuilder.newError(env)
                .message("El correo ya está en uso")
                .errorType(ErrorType.BAD_REQUEST)
                .build();
        }
        
        // Error genérico
        return GraphqlErrorBuilder.newError(env)
            .message("Error: " + ex.getMessage())
            .errorType(ErrorType.INTERNAL_ERROR)
            .build();
    }
}
```

**1.2: Crear excepciones personalizadas**

Archivos en `backend/src/main/java/com/cinematchbackend/exception/`:

```java
// CamposVaciosException.java
public class CamposVaciosException extends RuntimeException {
    public CamposVaciosException() {
        super("Completa todos los campos");
    }
}

// CodigoSalaInvalidoException.java
public class CodigoSalaInvalidoException extends RuntimeException {
    public CodigoSalaInvalidoException() {
        super("Código de sala inválido");
    }
}

// NombreRequeridoException.java
public class NombreRequeridoException extends RuntimeException {
    public NombreRequeridoException() {
        super("El nombre es requerido");
    }
}
```

**1.3: Validar en los resolvers GraphQL**

Archivo: `backend/src/main/java/com/cinematchbackend/graphql/AuthResolver.java`

```java
@MutationMapping
public LoginResponse login(
    @Argument String correo,
    @Argument String password
) {
    // Validar campos vacíos
    if (correo == null || correo.isBlank() || password == null || password.isBlank()) {
        throw new CamposVaciosException();
    }
    
    // Intentar autenticar
    try {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(correo, password)
        );
        // ... generar token
    } catch (BadCredentialsException e) {
        throw new BadCredentialsException("Credenciales incorrectas");
    }
}
```

Archivo: `backend/src/main/java/com/cinematchbackend/graphql/SalaResolver.java`

```java
@MutationMapping
public Sala unirseASala(
    @Argument String codigo,
    @AuthenticationPrincipal Usuario usuario
) {
    // Validar código
    if (codigo == null || codigo.isBlank()) {
        throw new CamposVaciosException();
    }
    
    // Buscar sala
    Sala sala = salaRepository.findByCodigo(codigo)
        .orElseThrow(() -> new CodigoSalaInvalidoException());
    
    // ... lógica para unirse
}
```

Archivo: `backend/src/main/java/com/cinematchbackend/graphql/PerfilResolver.java`

```java
@MutationMapping
public Usuario actualizarPerfil(
    @Argument String nombre,
    @Argument String descripcion,
    @AuthenticationPrincipal Usuario usuario
) {
    // Validar nombre
    if (nombre == null || nombre.isBlank()) {
        throw new NombreRequeridoException();
    }
    
    usuario.setNombre(nombre);
    usuario.setDescripcion(descripcion);
    return usuarioRepository.save(usuario);
}
```

### Criterios de Aceptación

| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Login con campos vacíos retorna "Completa todos los campos" | GraphQL response contiene `errors[0].message = "Completa todos los campos"` |
| 2 | Login con credenciales incorrectas retorna "Credenciales incorrectas" | GraphQL response contiene mensaje exacto |
| 3 | Unirse con código inválido retorna "Código de sala inválido" | GraphQL response contiene mensaje exacto |
| 4 | Actualizar perfil con nombre vacío retorna "El nombre es requerido" | GraphQL response contiene mensaje exacto |
| 5 | Los errores no son "INTERNAL_ERROR" genéricos | Todos los errores tienen mensajes específicos |

### Archivos a Modificar

- `backend/src/main/java/com/cinematchbackend/config/GraphQLExceptionResolver.java` (crear)
- `backend/src/main/java/com/cinematchbackend/exception/CamposVaciosException.java` (crear)
- `backend/src/main/java/com/cinematchbackend/exception/CodigoSalaInvalidoException.java` (crear)
- `backend/src/main/java/com/cinematchbackend/exception/NombreRequeridoException.java` (crear)
- `backend/src/main/java/com/cinematchbackend/graphql/AuthResolver.java` (agregar validaciones)
- `backend/src/main/java/com/cinematchbackend/graphql/SalaResolver.java` (agregar validaciones)
- `backend/src/main/java/com/cinematchbackend/graphql/PerfilResolver.java` (agregar validaciones)
- `backend/src/main/java/com/cinematchbackend/graphql/AdminResolver.java` (agregar validaciones)

---

## R-BE-02: Validación de Autenticación

### Estado
🟡 **IMPORTANTE** — 3 tests fallan (complementa R-FE-03)

### Problema
El backend no valida tokens JWT en las operaciones GraphQL protegidas, permitiendo acceso no autorizado.

### Tests Afectados

| Test ID | Descripción | Problema Backend |
|---------|-------------|------------------|
| TC-SALA-005 | Sala requiere autenticación | Backend permite crear sala sin token |
| TC-SALA-010 | Unirse requiere autenticación | Backend permite unirse sin token |
| TC-ADMIN-019 | Rutas admin requieren autenticación | Backend permite acceso sin token |

### Requerimiento

**2.1: Configurar validación JWT en GraphQL**

Archivo: `backend/src/main/java/com/cinematchbackend/config/GraphQLSecurityConfig.java`

```java
package com.cinematchbackend.config;

import com.cinematchbackend.service.JwtService;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;

@Configuration
public class GraphQLSecurityConfig {
    
    public static void validateAuthentication(DataFetchingEnvironment env) {
        Object principal = env.getGraphQlContext().get("principal");
        if (principal == null) {
            throw new UnauthorizedException("Autenticación requerida");
        }
    }
    
    public static void validateAdmin(DataFetchingEnvironment env) {
        validateAuthentication(env);
        Object principal = env.getGraphQlContext().get("principal");
        if (!(principal instanceof Usuario usuario) || 
            !"ADMINISTRADOR".equals(usuario.getRol())) {
            throw new ForbiddenException("Se requiere rol de administrador");
        }
    }
}
```

**2.2: Aplicar validación en resolvers**

Archivo: `backend/src/main/java/com/cinematchbackend/graphql/SalaResolver.java`

```java
@MutationMapping
public Sala crearSala(@AuthenticationPrincipal Usuario usuario) {
    // Validar autenticación
    if (usuario == null) {
        throw new UnauthorizedException("Autenticación requerida");
    }
    
    // ... crear sala
}

@MutationMapping
public Sala unirseASala(
    @Argument String codigo,
    @AuthenticationPrincipal Usuario usuario
) {
    // Validar autenticación
    if (usuario == null) {
        throw new UnauthorizedException("Autenticación requerida");
    }
    
    // ... unirse a sala
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
    // Validar autenticación y rol
    if (usuario == null) {
        throw new UnauthorizedException("Autenticación requerida");
    }
    if (!"ADMINISTRADOR".equals(usuario.getRol())) {
        throw new ForbiddenException("Se requiere rol de administrador");
    }
    
    // ... obtener métricas
}
```

**2.3: Crear excepciones de seguridad**

Archivos en `backend/src/main/java/com/cinematchbackend/exception/`:

```java
// UnauthorizedException.java
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}

// ForbiddenException.java
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
```

**2.4: Actualizar GraphQLExceptionResolver**

Agregar al resolver existente:

```java
if (ex instanceof UnauthorizedException) {
    return GraphqlErrorBuilder.newError(env)
        .message("Autenticación requerida")
        .errorType(ErrorType.UNAUTHORIZED) // HTTP 401
        .build();
}

if (ex instanceof ForbiddenException) {
    return GraphqlErrorBuilder.newError(env)
        .message("No tienes permisos para realizar esta acción")
        .errorType(ErrorType.FORBIDDEN) // HTTP 403
        .build();
}
```

### Criterios de Aceptación

| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Crear sala sin token retorna error UNAUTHORIZED | GraphQL response tiene `errorType = UNAUTHORIZED` |
| 2 | Unirse a sala sin token retorna error UNAUTHORIZED | GraphQL response tiene `errorType = UNAUTHORIZED` |
| 3 | Acceder a métricas sin token retorna error UNAUTHORIZED | GraphQL response tiene `errorType = UNAUTHORIZED` |
| 4 | Acceder a métricas con token de usuario normal retorna FORBIDDEN | GraphQL response tiene `errorType = FORBIDDEN` |
| 5 | Operaciones con token válido funcionan correctamente | No hay errores de autenticación |

### Archivos a Modificar

- `backend/src/main/java/com/cinematchbackend/config/GraphQLSecurityConfig.java` (crear)
- `backend/src/main/java/com/cinematchbackend/exception/UnauthorizedException.java` (crear)
- `backend/src/main/java/com/cinematchbackend/exception/ForbiddenException.java` (crear)
- `backend/src/main/java/com/cinematchbackend/graphql/SalaResolver.java` (agregar validaciones)
- `backend/src/main/java/com/cinematchbackend/graphql/AdminResolver.java` (agregar validaciones)
- `backend/src/main/java/com/cinematchbackend/config/GraphQLExceptionResolver.java` (agregar casos)

---

## R-BE-03: Validación de Roles de Administrador

### Estado
🟡 **IMPORTANTE** — Complementa R-BE-02

### Problema
El backend no valida que solo usuarios con rol ADMINISTRADOR puedan acceder a las operaciones de administración.

### Tests Afectados

| Test ID | Descripción | Problema Backend |
|---------|-------------|------------------|
| TC-ADMIN-019 | Rutas admin requieren autenticación | Backend no valida rol |
| TC-ADMIN-020 | Rutas admin requieren rol administrador | Backend no valida rol |

### Requerimiento

Ya cubierto parcialmente en R-BE-02. Asegurar que TODAS las operaciones de admin validen el rol:

```java
@QueryMapping
public DashboardResponse obtenerDashboard(@AuthenticationPrincipal Usuario usuario) {
    validarAdmin(usuario);
    // ... lógica
}

@QueryMapping
public MetricasResponse obtenerMetricas(
    @Argument String desde,
    @Argument String hasta,
    @AuthenticationPrincipal Usuario usuario
) {
    validarAdmin(usuario);
    // ... lógica
}

@MutationMapping
public Plataforma cambiarEstadoPlataforma(
    @Argument Long id,
    @Argument Boolean activa,
    @AuthenticationPrincipal Usuario usuario
) {
    validarAdmin(usuario);
    // ... lógica
}

@QueryMapping
public RetroalimentacionResponse obtenerRetroalimentacion(
    @Argument String desde,
    @Argument String hasta,
    @AuthenticationPrincipal Usuario usuario
) {
    validarAdmin(usuario);
    // ... lógica
}

private void validarAdmin(Usuario usuario) {
    if (usuario == null) {
        throw new UnauthorizedException("Autenticación requerida");
    }
    if (!"ADMINISTRADOR".equals(usuario.getRol())) {
        throw new ForbiddenException("Se requiere rol de administrador");
    }
}
```

### Criterios de Aceptación

| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Usuario normal no puede acceder a dashboard admin | Retorna error FORBIDDEN |
| 2 | Usuario normal no puede acceder a métricas admin | Retorna error FORBIDDEN |
| 3 | Usuario normal no puede cambiar estado de plataforma | Retorna error FORBIDDEN |
| 4 | Usuario normal no puede acceder a retroalimentación | Retorna error FORBIDDEN |
| 5 | Administrador puede acceder a todas las operaciones admin | Funciona correctamente |

### Archivos a Modificar

- `backend/src/main/java/com/cinematchbackend/graphql/AdminResolver.java` (agregar validaciones en todos los métodos)

---

## R-BE-04: API de Métricas con Filtros

### Estado
🟡 **IMPORTANTE** — 2 tests fallan por este problema

### Problema
El backend no soporta filtros de fecha en las métricas, o no retorna los datos correctamente.

### Tests Afectados

| Test ID | Descripción | Problema Backend |
|---------|-------------|------------------|
| TC-ADMIN-011 | Filtrar métricas por fecha | Backend no soporta filtros |
| TC-ADMIN-012 | Limpiar filtro de fechas | Backend no soporta filtros |

### Requerimiento

Archivo: `backend/src/main/java/com/cinematchbackend/graphql/AdminResolver.java`

```java
@QueryMapping
public MetricasResponse obtenerMetricas(
    @Argument String desde,
    @Argument String hasta,
    @AuthenticationPrincipal Usuario usuario
) {
    validarAdmin(usuario);
    
    LocalDate fechaDesde = desde != null ? LocalDate.parse(desde) : null;
    LocalDate fechaHasta = hasta != null ? LocalDate.parse(hasta) : null;
    
    // Consultar métricas con filtros
    long salasActivas = salaRepository.countByEstadoAndFechaBetween(
        "ACTIVA", fechaDesde, fechaHasta
    );
    long salasTotales = salaRepository.countByFechaBetween(fechaDesde, fechaHasta);
    long matchesGenerados = matchRepository.countByFechaBetween(fechaDesde, fechaHasta);
    double promedioCalificacion = calificacionRepository.promedioByFechaBetween(
        fechaDesde, fechaHasta
    );
    
    return new MetricasResponse(
        salasActivas,
        salasTotales,
        matchesGenerados,
        promedioCalificacion
    );
}
```

Archivo: `backend/src/main/java/com/cinematchbackend/repository/SalaRepository.java`

```java
@Query("SELECT COUNT(s) FROM Sala s WHERE s.estado = :estado " +
       "AND (:desde IS NULL OR s.fechaCreacion >= :desde) " +
       "AND (:hasta IS NULL OR s.fechaCreacion <= :hasta)")
long countByEstadoAndFechaBetween(
    @Param("estado") String estado,
    @Param("desde") LocalDate desde,
    @Param("hasta") LocalDate hasta
);

@Query("SELECT COUNT(s) FROM Sala s " +
       "WHERE (:desde IS NULL OR s.fechaCreacion >= :desde) " +
       "AND (:hasta IS NULL OR s.fechaCreacion <= :hasta)")
long countByFechaBetween(
    @Param("desde") LocalDate desde,
    @Param("hasta") LocalDate hasta
);
```

### Criterios de Aceptación

| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Métricas sin filtros retorna todos los datos | Query sin parámetros retorna datos completos |
| 2 | Métricas con filtro `desde` filtra correctamente | Solo incluye datos >= fecha desde |
| 3 | Métricas con filtro `hasta` filtra correctamente | Solo incluye datos <= fecha hasta |
| 4 | Métricas con ambos filtros funciona | Solo incluye datos en el rango |
| 5 | Los valores retornados son correctos | Los conteos y promedios son precisos |

### Archivos a Modificar

- `backend/src/main/java/com/cinematchbackend/graphql/AdminResolver.java` (agregar filtros)
- `backend/src/main/java/com/cinematchbackend/repository/SalaRepository.java` (agregar métodos)
- `backend/src/main/java/com/cinematchbackend/repository/MatchRepository.java` (agregar métodos)
- `backend/src/main/java/com/cinematchbackend/repository/CalificacionRepository.java` (agregar métodos)

---

## R-BE-05: API de Plataformas con Toggle

### Estado
🟡 **IMPORTANTE** — 1 test falla por este problema

### Problema
El backend no permite cambiar el estado (activa/inactiva) de una plataforma.

### Tests Afectados

| Test ID | Descripción | Problema Backend |
|---------|-------------|------------------|
| TC-ADMIN-014 | Deshabilitar plataforma | Mutation no existe o no funciona |

### Requerimiento

Archivo: `backend/src/main/java/com/cinematchbackend/graphql/AdminResolver.java`

```java
@MutationMapping
public Plataforma cambiarEstadoPlataforma(
    @Argument Long id,
    @Argument Boolean activa,
    @AuthenticationPrincipal Usuario usuario
) {
    validarAdmin(usuario);
    
    Plataforma plataforma = plataformaRepository.findById(id)
        .orElseThrow(() -> new PlataformaNoEncontradaException());
    
    plataforma.setActiva(activa);
    return plataformaRepository.save(plataforma);
}
```

Archivo: `backend/src/main/java/com/cinematchbackend/model/Plataforma.java`

```java
@Entity
public class Plataforma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    
    private Boolean activa = true;
    
    // getters y setters
}
```

Archivo: `backend/src/main/resources/schema.graphqls`

```graphql
type Mutation {
    cambiarEstadoPlataforma(id: ID!, activa: Boolean!): Plataforma
}
```

### Criterios de Aceptación

| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Mutation `cambiarEstadoPlataforma` existe | Schema GraphQL incluye la mutation |
| 2 | Cambiar estado de plataforma funciona | La plataforma cambia de `activa = true` a `activa = false` |
| 3 | Solo administradores pueden cambiar estado | Usuario normal recibe error FORBIDDEN |
| 4 | La plataforma se persiste correctamente | Después de cambiar, la BD refleja el cambio |
| 5 | Se puede volver a activar una plataforma | Cambiar de `false` a `true` funciona |

### Archivos a Modificar

- `backend/src/main/java/com/cinematchbackend/graphql/AdminResolver.java` (agregar mutation)
- `backend/src/main/java/com/cinematchbackend/repository/PlataformaRepository.java` (verificar)
- `backend/src/main/resources/schema.graphqls` (agregar mutation si no existe)

---

## R-BE-06: API de Retroalimentación con Filtros

### Estado
🟡 **IMPORTANTE** — 1 test falla por este problema

### Problema
El backend no soporta filtros de fecha en la retroalimentación, o no retorna el promedio correctamente.

### Tests Afectados

| Test ID | Descripción | Problema Backend |
|---------|-------------|------------------|
| TC-ADMIN-018 | Filtrar retroalimentación por fecha | Backend no soporta filtros |

### Requerimiento

Archivo: `backend/src/main/java/com/cinematchbackend/graphql/AdminResolver.java`

```java
@QueryMapping
public RetroalimentacionResponse obtenerRetroalimentacion(
    @Argument String desde,
    @Argument String hasta,
    @AuthenticationPrincipal Usuario usuario
) {
    validarAdmin(usuario);
    
    LocalDate fechaDesde = desde != null ? LocalDate.parse(desde) : null;
    LocalDate fechaHasta = hasta != null ? LocalDate.parse(hasta) : null;
    
    // Calcular promedio
    Double promedio = calificacionRepository.promedioByFechaBetween(
        fechaDesde, fechaHasta
    );
    
    // Calcular distribución (conteo por calificación 1-10)
    List<DistribucionItem> distribucion = calificacionRepository.distribucionByFechaBetween(
        fechaDesde, fechaHasta
    );
    
    return new RetroalimentacionResponse(promedio, distribucion);
}
```

Archivo: `backend/src/main/java/com/cinematchbackend/repository/CalificacionRepository.java`

```java
@Query("SELECT AVG(c.valor) FROM Calificacion c " +
       "WHERE (:desde IS NULL OR c.fecha >= :desde) " +
       "AND (:hasta IS NULL OR c.fecha <= :hasta)")
Double promedioByFechaBetween(
    @Param("desde") LocalDate desde,
    @Param("hasta") LocalDate hasta
);

@Query("SELECT new com.cinematchbackend.dto.DistribucionItem(c.valor, COUNT(c)) " +
       "FROM Calificacion c " +
       "WHERE (:desde IS NULL OR c.fecha >= :desde) " +
       "AND (:hasta IS NULL OR c.fecha <= :hasta) " +
       "GROUP BY c.valor " +
       "ORDER BY c.valor")
List<DistribucionItem> distribucionByFechaBetween(
    @Param("desde") LocalDate desde,
    @Param("hasta") LocalDate hasta
);
```

Archivo: `backend/src/main/java/com/cinematchbackend/dto/DistribucionItem.java`

```java
public class DistribucionItem {
    private Integer calificacion;
    private Long cantidad;
    
    public DistribucionItem(Integer calificacion, Long cantidad) {
        this.calificacion = calificacion;
        this.cantidad = cantidad;
    }
    
    // getters y setters
}
```

Archivo: `backend/src/main/java/com/cinematchbackend/dto/RetroalimentacionResponse.java`

```java
public class RetroalimentacionResponse {
    private Double promedio;
    private List<DistribucionItem> distribucion;
    
    public RetroalimentacionResponse(Double promedio, List<DistribucionItem> distribucion) {
        this.promedio = promedio;
        this.distribucion = distribucion;
    }
    
    // getters y setters
}
```

Archivo: `backend/src/main/resources/schema.graphqls`

```graphql
type Query {
    obtenerRetroalimentacion(desde: String, hasta: String): RetroalimentacionResponse
}

type RetroalimentacionResponse {
    promedio: Float
    distribucion: [DistribucionItem!]!
}

type DistribucionItem {
    calificacion: Int!
    cantidad: Int!
}
```

### Criterios de Aceptación

| # | Criterio | Medida de Éxito |
|---|----------|-----------------|
| 1 | Query `obtenerRetroalimentacion` existe | Schema GraphQL incluye la query |
| 2 | Retorna promedio de calificaciones | El valor es correcto |
| 3 | Retorna distribución por calificación (1-10) | Array con 10 elementos (o menos si no hay todas) |
| 4 | Filtro `desde` funciona correctamente | Solo incluye calificaciones >= fecha |
| 5 | Filtro `hasta` funciona correctamente | Solo incluye calificaciones <= fecha |
| 6 | Sin filtros retorna todas las calificaciones | Funciona correctamente |

### Archivos a Modificar

- `backend/src/main/java/com/cinematchbackend/graphql/AdminResolver.java` (agregar query)
- `backend/src/main/java/com/cinematchbackend/repository/CalificacionRepository.java` (agregar métodos)
- `backend/src/main/java/com/cinematchbackend/dto/DistribucionItem.java` (crear)
- `backend/src/main/java/com/cinematchbackend/dto/RetroalimentacionResponse.java` (crear)
- `backend/src/main/resources/schema.graphqls` (agregar tipos si no existen)

---

## Resumen de Impacto

| Requerimiento | Tests Recuperados | Prioridad |
|---------------|-------------------|-----------|
| R-BE-01: Mensajes de error GraphQL | 8 | 🟡 IMPORTANTE |
| R-BE-02: Validación de autenticación | 3 | 🟡 IMPORTANTE |
| R-BE-03: Validación de roles admin | (incluido en R-BE-02) | 🟡 IMPORTANTE |
| R-BE-04: API de métricas con filtros | 2 | 🟡 IMPORTANTE |
| R-BE-05: API de plataformas con toggle | 1 | 🟡 IMPORTANTE |
| R-BE-06: API de retroalimentación con filtros | 1 | 🟡 IMPORTANTE |
| **TOTAL** | **~12 tests** | |

**Nota:** Muchos tests fallan por problemas del frontend (28 de 40). Los requerimientos de backend complementan las correcciones del frontend.

---

## Orden de Implementación Sugerido

1. **R-BE-01** (8 tests) — Mensajes de error, mayor impacto
2. **R-BE-02 + R-BE-03** (3 tests) — Seguridad y validación de roles
3. **R-BE-04** (2 tests) — Métricas con filtros
4. **R-BE-05** (1 test) — Toggle de plataformas
5. **R-BE-06** (1 test) — Retroalimentación con filtros

---

## Verificación Final

Después de implementar todas las correcciones del backend:

```bash
# Verificar que los mensajes de error son correctos
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"mutation { login(correo: \"\", password: \"\") { token } }"}'

# Esperado:
# {"errors":[{"message":"Completa todos los campos","extensions":{"classification":"Bad Request"}}]}

# Verificar que la autenticación funciona
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"mutation { crearSala { codigo } }"}'

# Esperado (sin token):
# {"errors":[{"message":"Autenticación requerida","extensions":{"classification":"Unauthorized"}}]}

# Ejecutar todos los tests
cd documentacion/robot
./run_tests.sh

# Resultado esperado:
# 143 tests, 143 passed, 0 failed
```

---

## Dependencias entre Frontend y Backend

| Problema | Frontend | Backend | Tests |
|----------|----------|---------|-------|
| Mensajes de error | Mostrar mensajes correctos | Retornar mensajes correctos | 8 |
| Guards de autenticación | Redirigir a /login | Validar tokens | 3 |
| Dashboard admin | Mostrar elementos | Retornar datos | 5 |
| Filtros de fecha | Inputs y UI | Soportar filtros | 2 |
| Toggle plataformas | Checkbox y UI | Mutation | 1 |
| Retroalimentación | Mostrar promedio | Query con filtros | 1 |

**Conclusión:** Se necesitan correcciones en AMBOS (frontend y backend) para recuperar todos los tests.
