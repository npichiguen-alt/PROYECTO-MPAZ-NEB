
# MPAZ NEB

## Descripción del proyecto

MPAZ NEB es una aplicación móvil educativa desarrollada para apoyar la nivelación de estudiantes ficticios de 1.º a 4.º básico en las áreas de Lectura y Matemática.

El proyecto corresponde a la **Evaluación Parcial 5 de la asignatura Desarrollo de Aplicaciones Móviles (DSY1105)**.

La aplicación permite gestionar actividades educativas, realizar asignaciones mediante códigos temporales, responder preguntas, consultar resultados y registrar observaciones pedagógicas.

El sistema incorpora persistencia local mediante Room y comunicación con servicios REST desarrollados en Spring Boot, utilizando PostgreSQL en AWS RDS como base de datos remota.

## Integrantes del equipo

| Integrante | Responsabilidad |
|---|---|
| Matías Pardo | Administración pedagógica, gestión de actividades e infraestructura backend. |
| Noemí Pichiguen | Selección de perfiles, módulo Estudiante, respuestas y resultados. |
| Kathryn Mora | Módulo Asistente de aula, asignaciones, seguimiento, observaciones pedagógicas e integración remota. |

## Tecnologías utilizadas

### Frontend móvil

- Android Studio
- Kotlin
- Jetpack Compose
- Material Design 3
- Arquitectura MVVM
- ViewModel y StateFlow
- Coroutines
- Room Database
- Retrofit
- Gson

### Backend e infraestructura

- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- API REST
- Maven
- PostgreSQL
- AWS EC2
- AWS RDS

### Pruebas y colaboración

- JUnit
- MockK
- Mockito
- Git y GitHub
- Trello

## Arquitectura del sistema

La aplicación utiliza el patrón MVVM para separar las responsabilidades de presentación, lógica y acceso a datos.

```text
Interfaz Jetpack Compose
          |
       ViewModel
          |
       Repository
        /      \
     Room     Retrofit
                 |
             Spring Boot
                 |
             PostgreSQL
              AWS RDS
```

### Capas de Android

- Model: representación de entidades y datos.
- View: interfaces desarrolladas con Jetpack Compose.
- ViewModel: control de estados, validaciones y lógica de interacción.
- Repository: comunicación con fuentes de datos locales y remotas.
- Room: persistencia local.
- Retrofit: consumo de servicios REST.

### Capas de Spring Boot

- Controller: exposición de endpoints HTTP.
- Service: lógica de negocio y validaciones.
- Repository: acceso a PostgreSQL mediante Spring Data JPA.
- Model: entidades persistentes.
- DTO: transferencia y validación de datos.

## Funcionalidades

### Administración pedagógica

- Crear y editar actividades educativas.
- Registrar preguntas, alternativas y respuestas correctas.
- Activar y desactivar actividades.
- Gestionar usuarios ficticios.
- Consultar información pedagógica.

### Asistente de aula

- Consultar actividades locales y remotas.
- Seleccionar estudiantes ficticios.
- Validar formularios de asignación.
- Asignar actividades mediante Spring Boot.
- Recibir códigos temporales generados por el servidor.
- Copiar y compartir códigos mediante funciones nativas de Android.
- Consultar asignaciones remotas.
- Visualizar contadores de asignaciones.
- Consultar resultados de los estudiantes.
- Visualizar aciertos, intentos y rendimiento.
- Registrar y actualizar observaciones pedagógicas.
- Gestionar errores de conexión y mensajes de validación.

### Estudiante

- Ingresar mediante un código temporal.
- Consultar la actividad asignada.
- Responder preguntas de Lectura y Matemática.
- Realizar un máximo de tres intentos por pregunta.
- Recibir retroalimentación visual.
- Registrar respuestas mediante servicios REST.
- Consultar resultados.

## Servicios REST

| Método | Endpoint | Función |
|---|---|---|
| GET | /api/actividades | Consultar actividades |
| POST | /api/actividades | Crear actividad |
| GET | /api/actividades/{id} | Consultar actividad |
| GET | /api/preguntas/actividad/{actividadId} | Consultar preguntas |
| GET | /api/asignaciones | Consultar asignaciones |
| POST | /api/asignaciones | Crear asignación |
| GET | /api/asignaciones/codigo/{codigo} | Buscar asignación por código |
| GET | /api/asignaciones/estudiante/{alias} | Consultar asignaciones por estudiante |
| POST | /api/respuestas | Registrar respuesta |
| GET | /api/respuestas/asignacion/{asignacionId} | Consultar respuestas |
| GET | /api/resultados/asignacion/{asignacionId} | Consultar resultados |
| POST | /api/resultados/asignacion/{asignacionId}/calcular | Calcular resultados |
| PATCH | /api/resultados/{id}/observacion | Actualizar observación pedagógica |

## API externa

El proyecto incorpora una interfaz Retrofit independiente para el consumo de PokeAPI.

Esta integración se diferencia de los servicios REST propios desarrollados en Spring Boot, que gestionan la información pedagógica del sistema.

## Persistencia de datos

El sistema contempla dos mecanismos de almacenamiento:

### Persistencia local

Room Database permite almacenar información dentro del dispositivo Android.

### Persistencia remota

PostgreSQL, alojado en AWS RDS, permite centralizar información mediante los servicios REST de Spring Boot.

La base de datos del proyecto se denomina `mpaz_neb`.

Entre las principales entidades del sistema se encuentran:

- Actividades.
- Preguntas.
- Estudiantes.
- Asignaciones.
- Respuestas.
- Resultados.

Las observaciones pedagógicas se almacenan en el campo `observacion` de los resultados, sin necesidad de crear una tabla independiente.

## Pruebas unitarias

El proyecto incorpora pruebas unitarias para verificar la lógica de los módulos.

### Módulo Asistente

- Validación de asignaciones.
- Prevención de operaciones simultáneas.
- Validación de estudiantes inactivos.
- Validación de actividades no disponibles.
- Validación de campos obligatorios.
- Validación de observaciones vacías.
- Validación del máximo de 250 caracteres.
- Manejo de errores del repositorio.
- Actualización del formulario.
- Consulta de asignaciones y resultados.

### Ejecución de pruebas Android

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

### Ejecución de pruebas Spring Boot

```powershell
.\mvnw.cmd clean test
```

## Generación del APK firmado

Para generar una versión firmada de la aplicación:

1. Abrir el proyecto en Android Studio.
2. Seleccionar Build.
3. Seleccionar Generate Signed App Bundle or APK.
4. Seleccionar APK.
5. Configurar el almacén de claves y el alias.
6. Seleccionar la variante release.
7. Generar el APK firmado.

El archivo generado corresponde a `app-release.apk`.

Las claves privadas y contraseñas de firma no deben almacenarse en GitHub.

## Cómo ejecutar el proyecto

### Aplicación Android

1. Clonar el repositorio Android.
2. Abrir el proyecto en Android Studio.
3. Sincronizar las dependencias de Gradle.
4. Verificar la URL del backend en RetrofitClient.kt.
5. Seleccionar un emulador o dispositivo Android compatible.
6. Ejecutar el módulo app.
7. Seleccionar el perfil correspondiente.

### Backend Spring Boot

1. Clonar el repositorio del backend.
2. Configurar las variables de conexión a PostgreSQL.
3. Descargar las dependencias mediante Maven.
4. Ejecutar el servicio Spring Boot.
5. Verificar la disponibilidad de los endpoints REST.

La conexión remota requiere que el servicio desplegado en AWS y la base de datos estén disponibles.

## Repositorios GitHub

### Aplicación Android

https://github.com/npichiguen-alt/PROYECTO-MPAZ-NEB

### Backend Spring Boot

https://github.com/Matias-Pardo/MPAZ-NEB-API

## Aportes del módulo Asistente

- Desarrollo de las pantallas de Inicio, Catálogo, Estudiantes, Asignar y Seguimiento.
- Implementación de arquitectura MVVM.
- Validación de formularios y observaciones.
- Integración de asignaciones mediante Retrofit.
- Consulta de actividades y resultados remotos.
- Implementación del endpoint PATCH de observaciones pedagógicas.
- Integración con PostgreSQL mediante Spring Boot.
- Desarrollo de pruebas unitarias.
- Generación de APK firmado.

### Commits destacados

- c314252: Integración de asignaciones remotas y seguimiento.
- 3d4ee5d: Integración de actividades, asignaciones, resultados y observaciones AWS.
- b2355be: Implementación de persistencia remota de observaciones pedagógicas en el backend.

## Planificación y colaboración

El equipo utiliza Trello para organizar las tareas, responsables y avances de cada módulo.

Git y GitHub permiten gestionar las versiones del código, registrar los aportes individuales e integrar las funcionalidades desarrolladas por cada integrante.

## Estado de verificación

- Compilación Android exitosa.
- Pruebas unitarias Android superadas.
- APK firmado generado correctamente.
- Backend compilado.
- Once pruebas del backend superadas.
- Una prueba de contexto Spring Boot pendiente por conexión JDBC.
- Código Android y backend publicado en GitHub.
- Despliegue actualizado en AWS y prueba funcional completa pendientes de confirmación.

## Consideraciones

MPAZ NEB es un prototipo académico que utiliza exclusivamente información ficticia.

No se deben utilizar datos personales reales de estudiantes.

Las credenciales de acceso a PostgreSQL, AWS y las claves privadas de firma deben mantenerse fuera del repositorio público.
