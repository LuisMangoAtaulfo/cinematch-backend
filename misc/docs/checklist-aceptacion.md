# Checklist de Pruebas de Aceptación

Este documento describe los escenarios de aceptación que deben validarse manualmente por los usuarios finales antes del despliegue a producción.

---

## 1. Dashboard de Métricas de Usuario

### 1.1 Acceso al Dashboard
- [ ] El usuario puede acceder a "Mi Dashboard" desde el menú principal
- [ ] Al hacer clic, se redirige a `/dashboard/actividad`
- [ ] La página carga sin errores y muestra los KPIs del usuario

### 1.2 Visualización de Actividad
- [ ] Se muestra el total de salas creadas/unidas por el usuario
- [ ] Se muestra el número de salas activas actuales
- [ ] Se muestra el total de matches generados
- [ ] Se muestra la calificación promedio del usuario
- [ ] Se muestra el total de evaluaciones realizadas
- [ ] Los datos corresponden únicamente al usuario autenticado

### 1.3 Tendencias Históricas
- [ ] El usuario puede acceder a `/dashboard/tendencias`
- [ ] Se muestra el historial diario de actividad
- [ ] Cada día muestra: fecha, total de salas, matches, salas activas y promedio
- [ ] Si no hay datos, se muestra un mensaje apropiado
- [ ] El usuario puede volver a la vista de actividad actual

---

## 2. Métricas de Administrador

### 2.1 Acceso Restringido
- [ ] Solo usuarios con rol `ADMINISTRADOR` pueden acceder a `/admin/metricas`
- [ ] Usuarios normales reciben error de acceso denegado
- [ ] El login de admin funciona correctamente

### 2.2 Visualización Global
- [ ] Se muestra el total de salas de la plataforma
- [ ] Se muestra el número de salas activas
- [ ] Se muestra el total de matches generados en la plataforma
- [ ] Se muestra el promedio de calificación global
- [ ] Los datos son consistentes con la actividad real

### 2.3 Historial Global
- [ ] Se muestra el historial diario de toda la plataforma
- [ ] Los datos se agrupan correctamente por fecha
- [ ] El filtro por rango de fechas funciona correctamente

---

## 3. Funcionalidad de Salas y Matches

### 3.1 Creación de Sala
- [ ] Un usuario puede crear una sala y obtener un código único
- [ ] El código se genera correctamente y es único
- [ ] La sala se guarda en la base de datos con estado `ESPERANDO`

### 3.2 Unión a Sala
- [ ] Un usuario puede unirse a una sala con un código válido
- [ ] Si el código no existe, se muestra error apropiado
- [ ] Si la sala ya tiene 2 usuarios, se muestra error
- [ ] Al unirse, el estado de la sala cambia a `ACTIVA`

### 3.3 Generación de Match
- [ ] Cuando ambos usuarios evalúan positivamente el mismo contenido, se genera un match
- [ ] El match se guarda en la base de datos
- [ ] Se publica un evento de match (para notificaciones en tiempo real)
- [ ] Los usuarios pueden ver sus matches en la lista

---

## 4. Calificaciones y Evaluaciones

### 4.1 Calificación de Sala
- [ ] Al finalizar una sala, los usuarios pueden calificarla (1-5 estrellas)
- [ ] La calificación se guarda correctamente
- [ ] El promedio de calificación se actualiza en las métricas
- [ ] Un usuario no puede calificar la misma sala dos veces

### 4.2 Evaluación de Contenido
- [ ] Los usuarios pueden evaluar contenido (like/dislike) durante la sala
- [ ] Las evaluaciones se guardan correctamente
- [ ] Si ambos usuarios evalúan positivamente, se genera un match
- [ ] Las evaluaciones son visibles en el historial del usuario

---

## 5. Chat y Mensajería

### 5.1 Chat en Tiempo Real
- [ ] Los usuarios pueden enviar mensajes en la sala activa
- [ ] Los mensajes se reciben en tiempo real vía WebSocket
- [ ] Los mensajes se guardan en la base de datos
- [ ] Al recargar la página, se mantienen los mensajes anteriores

### 5.2 Desconexión
- [ ] Si un usuario se desconecta, el otro recibe notificación
- [ ] La sincronización se pausa hasta que se reconecte
- [ ] Los mensajes no se pierden durante la desconexión

---

## 6. Seguridad y Autorización

### 6.1 Autenticación
- [ ] El login devuelve un token JWT válido
- [ ] El registro crea un usuario nuevo correctamente
- [ ] Las contraseñas se encriptan correctamente (BCrypt)
- [ ] Los tokens se invalidan al hacer logout

### 6.2 Autorización
- [ ] Los endpoints de usuario requieren autenticación
- [ ] Los endpoints de admin requieren rol `ADMINISTRADOR`
- [ ] GraphQL está accesible para todos (autorización por resolver)
- [ ] Los tokens expiran correctamente

---

## 7. Rendimiento y Escalabilidad

### 7.1 Tiempos de Respuesta
- [ ] Las queries de métricas responden en < 2 segundos
- [ ] El historial de métricas responde en < 3 segundos
- [ ] Las operaciones de sala responden en < 1 segundo

### 7.2 Concurrencia
- [ ] Múltiples usuarios pueden crear salas simultáneamente
- [ ] Múltiples usuarios pueden unirse a salas simultáneamente
- [ ] No hay condiciones de carrera en la generación de matches

---

## 8. Casos de Error y Recuperación

### 8.1 Errores de Base de Datos
- [ ] Si la BD no está disponible, se muestra mensaje de error
- [ ] Las transacciones se revierten correctamente en caso de error
- [ ] No hay datos inconsistentes después de un error

### 8.2 Errores de Validación
- [ ] Si se envían datos inválidos, se retorna error 400
- [ ] Los mensajes de error son descriptivos
- [ ] No se guardan datos inválidos en la BD

---

## Criterios de Aceptación Generales

- [ ] Todas las funcionalidades principales operan correctamente
- [ ] No hay errores críticos en la consola del navegador
- [ ] No hay errores críticos en los logs del servidor
- [ ] La aplicación es usable y responsiva
- [ ] Los datos persisten correctamente entre sesiones
- [ ] La aplicación funciona en los navegadores principales (Chrome, Firefox, Safari, Edge)

---

**Responsable de validación:** Equipo de QA / Usuarios finales  
**Fecha de validación:** _Pendiente_  
**Estado:** _Pendiente de ejecución_
