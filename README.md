# MPAZ NEB

## Descripción del proyecto

MPAZ NEB es una aplicación móvil educativa desarrollada para apoyar la nivelación de estudiantes ficticios de 1.º a 4.º básico en las áreas de Lectura y Matemática.

El proyecto corresponde a la **Evaluación Parcial 5 de la asignatura Desarrollo de Aplicaciones Móviles (DSY1105)**.

La aplicación permite gestionar actividades educativas, registrar estudiantes, realizar asignaciones mediante códigos temporales, responder preguntas, consultar resultados y registrar observaciones pedagógicas.

El sistema incorpora persistencia local mediante Room, servicios REST desarrollados en Spring Boot, PostgreSQL en AWS RDS y consumo de la API externa PokéAPI.

## Integrantes del equipo

| Integrante | Responsabilidad |
|---|---|
| Matías Pardo | Administración pedagógica, actividades, preguntas, estudiantes e infraestructura backend. |
| Noemí Pichiguen | Selección de perfiles, módulo Estudiante, respuestas, resultados e integración PokéAPI. |
| Kathryn Mora | Módulo Asistente, asignaciones, seguimiento, observaciones, integración remota, corrección del estado de asignaciones y documentación técnica. |

## Tecnologías utilizadas

### Aplicación móvil

- Android Studio.
- Kotlin.
- Jetpack Compose.
- Material Design 3.
- Arquitectura MVVM.
- ViewModel.
- Coroutines.
- Room Database.
- Retrofit y Gson.

### Backend e infraestructura

- Java 21.
- Spring Boot.
- Spring Data JPA e Hibernate.
- Jakarta Validation.
- API REST.
- Maven.
- PostgreSQL.
- AWS EC2 y AWS RDS.
- Systemd para ejecución persistente del backend.

### Pruebas y colaboración

- JUnit.
- MockK.
- Mockito.
- Gradle.
- JaCoCo.
- Git y GitHub.
- Trello.

## Arquitectura del sistema

La aplicación utiliza MVVM para separar la interfaz, la lógica de negocio y el acceso a datos.

```text
Interfaz Jetpack Compose
          |
       ViewModel
          |
       Repository
       /    |    \
    Room Retrofit PokéAPI
            |
        Spring Boot
            |
        PostgreSQL
          AWS RDS
```

### Capas Android

- Model: representación de entidades y datos.
- View: interfaces desarrolladas con Jetpack Compose.
- ViewModel: estados, validaciones y lógica de interacción.
- Repository: acceso a fuentes de datos locales y remotas.
- Room: persistencia local.
- Retrofit: consumo de servicios REST.

### Capas Spring Boot

- Controller: exposición de endpoints HTTP.
- Service: lógica de negocio y validaciones.
- Repository: acceso a PostgreSQL mediante Spring Data JPA.
- Model: entidades persistentes.
- DTO: transferencia y validación de datos.

## Funcionalidades

### Administración pedagógica

- Crear, editar y eliminar actividades.
- Registrar y gestionar preguntas y alternativas.
- Activar y desactivar actividades.
- Registrar estudiantes ficticios.
- Consultar actividades y estudiantes desde AWS.
- Consultar resultados remotos.
- Validar los campos obligatorios.
- Proteger la eliminación de actividades activas.

### Asistente de aula

- Consultar el catálogo de actividades.
- Filtrar por módulo, curso y dificultad.
- Consultar estudiantes activos registrados en PostgreSQL.
- Validar formularios de asignación.
- Asignar actividades mediante servicios REST.
- Generar, copiar y compartir códigos temporales.
- Consultar asignaciones y sus estados.
- Visualizar contadores de asignaciones.
- Consultar aciertos, intentos y rendimiento.
- Registrar y actualizar observaciones pedagógicas.
- Gestionar estados de carga y errores de conexión.

### Estudiante

- Ingresar mediante un código temporal.
- Consultar actividades y preguntas asignadas.
- Responder ejercicios de Lectura y Matemática.
- Realizar un máximo de tres intentos por pregunta.
- Recibir retroalimentación visual mediante Pokémon.
- Registrar respuestas en PostgreSQL.
- Consultar resultados y rendimiento.

## Servicios REST

Principales endpoints utilizados por la aplicación:

| Método | Endpoint | Función |
|---|---|---|
| GET | /api/actividades | Consultar actividades |
| POST | /api/actividades | Crear actividad |
| GET | /api/actividades/{id} | Consultar actividad |
| GET | /api/preguntas/actividad/{actividadId} | Consultar preguntas |
| GET | /api/estudiantes/activos | Consultar estudiantes activos |
| POST | /api/estudiantes | Registrar estudiante |
| GET | /api/asignaciones | Consultar asignaciones |
| POST | /api/asignaciones | Crear asignación |
| GET | /api/asignaciones/codigo/{codigo} | Buscar asignación por código |
| GET | /api/asignaciones/estudiante/{alias} | Consultar asignaciones por estudiante |
| POST | /api/respuestas | Registrar respuesta |
| GET | /api/respuestas/asignacion/{asignacionId} | Consultar respuestas |
| GET | /api/resultados/asignacion/{asignacionId} | Consultar resultados |
| POST | /api/resultados/asignacion/{asignacionId}/calcular | Calcular resultados |
| PATCH | /api/resultados/{id}/observacion | Actualizar observación pedagógica |

El backend también implementa operaciones de edición, eliminación y activación de actividades, además de operaciones de gestión de preguntas.

## API externa: PokéAPI

La aplicación consume PokéAPI mediante una interfaz Retrofit independiente de los servicios REST propios.

URL base:

https://pokeapi.co/api/v2/

La integración permite mostrar Pokémon como recurso de retroalimentación durante las actividades educativas.

Se implementaron mejoras de carga y recursos gráficos locales de respaldo para Pikachu, Bulbasaur y Eevee, con el objetivo de mantener la retroalimentación visual cuando falla la carga de imágenes remotas.

## Persistencia de datos

### Persistencia local

Room Database permite almacenar información dentro del dispositivo Android.

### Persistencia remota

PostgreSQL, alojado en AWS RDS, centraliza la información mediante los servicios REST de Spring Boot.

La base de datos se denomina `mpaz_neb`.

Principales entidades:

- Actividades.
- Preguntas.
- Estudiantes.
- Asignaciones.
- Respuestas.
- Resultados.

Las observaciones pedagógicas se almacenan en el campo `observacion` de los resultados.

El backend actualiza una asignación al estado Completada cuando todas las preguntas han sido respondidas correctamente o se han agotado los tres intentos permitidos por pregunta.

## Despliegue en AWS

El backend Spring Boot está desplegado en una instancia EC2 conectada a PostgreSQL en AWS RDS.

URL pública verificada el 22 de noviembre de 2026:

```text
http://98.82.178.102:8080/
```

La dirección se configura en:

```text
app/src/main/java/com/example/mpazneb/data/remote/RetrofitClient.kt
```

La instancia utiliza una IP pública dinámica. Si se detiene y vuelve a iniciar, es necesario comprobar la nueva dirección y actualizar la configuración Android cuando corresponda.

El backend se ejecuta mediante el servicio `mpaz-neb-api.service`, configurado para iniciarse automáticamente al arrancar la instancia.

## Pruebas unitarias Android

Resultados registrados el 22 de noviembre de 2026:

| Clase de prueba | Pruebas aprobadas |
|---|---:|
| AsistenteViewModelTest | 15 |
| ContenidoEstudianteRepositoryTest | 5 |
| EstudianteRemotoRepositoryTest | 5 |
| EstudianteViewModelTest | 10 |
| PokemonRepositoryTest | 3 |
| PokemonViewModelTest | 10 |
| RespuestaRepositoryTest | 3 |
| ValidadorAsignacionTest | 13 |
| Total | 64 |

Resultados:

- Pruebas ejecutadas: 64.
- Fallos: 0.
- Pruebas omitidas: 0.
- Porcentaje de éxito: 100%.
- Duración de las pruebas: 14,921 segundos.
- Compilación y ejecución de pruebas: BUILD SUCCESSFUL.

### Ejecutar las pruebas Android

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
```

Informe generado:

```text
app/build/reports/tests/testDebugUnitTest/index.html
```

### Cobertura de código JaCoCo

| Indicador | Cobertura |
|---|---:|
| Instrucciones | 5% |
| Ramas | 6% |
| Líneas | 6,7% |

Cobertura de instrucciones por paquetes seleccionados:

| Paquete | Cobertura |
|---|---:|
| repository.pokemon | 90% |
| model.asistente | 75% |
| viewmodel | 54% |
| repository.estudiante | 52% |
| model | 50% |
| data.remote | 33% |

Generar informe:

```powershell
.\gradlew.bat :app:createDebugUnitTestCoverageReport
```

Ubicación:

```text
app/build/reports/coverage/test/debug/index.html
```

El porcentaje de éxito de las pruebas no equivale al porcentaje de cobertura. La cobertura global incluye pantallas Compose, componentes de persistencia y otras clases que no están completamente cubiertas por las pruebas unitarias actuales.

## Pruebas unitarias Spring Boot

Se ejecutaron las pruebas unitarias de los servicios de actividades, preguntas, estudiantes, asignaciones, respuestas y resultados.

Resultado verificado en AWS EC2:

- Pruebas ejecutadas: 70.
- Fallos: 0.
- Errores: 0.
- Compilación Maven exitosa.

### Ejecución en Windows

```powershell
.\mvnw.cmd "-Dtest=*ServiceTest" clean test
```

### Ejecución en Ubuntu AWS

```bash
bash mvnw "-Dtest=*ServiceTest" clean package
```

Los informes Maven se generan en:

```text
target/surefire-reports/
```

La prueba de contexto completo de Spring Boot requiere una conexión PostgreSQL correctamente configurada. Esta prueba presentó un error JDBC en el entorno local y no está incluida entre las 70 pruebas unitarias aprobadas.

### Resumen de pruebas

| Componente | Pruebas unitarias aprobadas |
|---|---:|
| Android | 64 |
| Spring Boot | 70 |
| Total | 134 |

## Verificación funcional

Se verificó el flujo integrado de los tres perfiles:

1. Creación de una actividad educativa.
2. Registro de preguntas y activación.
3. Registro y consulta de estudiantes remotos.
4. Asignación de una actividad desde Asistente.
5. Generación de código temporal.
6. Ingreso del Estudiante mediante el código.
7. Registro de respuestas y cálculo del rendimiento.
8. Consulta de resultados en Administración y Asistente.
9. Registro de observaciones pedagógicas.
10. Actualización del estado de asignación a Completada.
11. Visualización de imagen Pokémon y retroalimentación ante una respuesta incorrecta.

También se comprobó la recuperación del servicio Spring Boot tras detener y volver a iniciar EC2.

## Seguridad y estabilidad

El proyecto incorpora:

- Validación de campos obligatorios.
- Validación de cursos, módulos y dificultades.
- Validación de estudiantes activos.
- Control de intentos por pregunta.
- Prevención de operaciones simultáneas.
- Manejo de errores de conexión.
- Estados de carga y opciones de reintento.
- Validación de observaciones pedagógicas.
- Restricciones de integridad en las operaciones de actividades.
- Recursos gráficos de respaldo para Pokémon.

El proyecto es un prototipo académico. El acceso por perfiles y códigos temporales no constituye un sistema completo de autenticación para producción.

La comunicación HTTP utilizada en el entorno académico debe reemplazarse por HTTPS en un entorno productivo.

## Generación del APK firmado

1. Abrir el proyecto en Android Studio.
2. Seleccionar Build.
3. Seleccionar Generate Signed App Bundle or APK.
4. Seleccionar APK.
5. Utilizar el almacén de claves existente.
6. Seleccionar el alias correspondiente.
7. Seleccionar la variante release.
8. Generar el APK firmado.
9. Instalarlo en un dispositivo o emulador compatible.
10. Verificar las funcionalidades principales.

El archivo generado corresponde a `app-release.apk`.

Las contraseñas y claves privadas de firma no deben publicarse en GitHub.

## Cómo ejecutar el proyecto

### Aplicación Android

1. Clonar el repositorio Android.
2. Abrirlo en Android Studio.
3. Sincronizar Gradle.
4. Comprobar la URL de RetrofitClient.kt.
5. Verificar que AWS esté disponible.
6. Seleccionar un emulador o dispositivo Android.
7. Ejecutar el módulo app.
8. Seleccionar el perfil correspondiente.

### Backend Spring Boot

1. Clonar el repositorio del backend.
2. Configurar Java 21.
3. Configurar las variables de entorno para PostgreSQL.
4. Descargar las dependencias Maven.
5. Ejecutar Spring Boot.
6. Comprobar los endpoints REST.

## Repositorios y planificación

Android:

https://github.com/npichiguen-alt/PROYECTO-MPAZ-NEB

Backend:

https://github.com/Matias-Pardo/MPAZ-NEB-API

Tablero de planificación:

https://trello.com/b/EGRBtnop/

## Commits destacados

| Commit | Aporte |
|---|---|
| 940d5aa | Gestión remota de actividades y preguntas |
| 3d4ee5d | Integración remota del Asistente |
| b2355be | Persistencia de observaciones pedagógicas |
| 5ed6089 | Integración de estudiantes remotos |
| 85ef79e | Corrección del estado de asignaciones y pruebas unitarias |
| fa6cc15 | Mejoras PokéAPI y respaldos locales |
| ec040ad | Actualización de imágenes y pruebas Pokémon |

## Estado de verificación

- Código Android y backend publicado en GitHub.
- Backend desplegado en AWS.
- Conexión PostgreSQL operativa.
- 64 pruebas unitarias Android aprobadas.
- 70 pruebas unitarias backend aprobadas.
- Informe de cobertura Android generado.
- Flujo integral de los tres perfiles verificado.
- Corrección de estado de asignaciones verificada.
- Imagen Pokémon verificada en emulador.
- Regeneración y verificación del APK firmado final pendiente.

## Consideraciones

MPAZ NEB utiliza exclusivamente información ficticia con fines educativos.

No se deben utilizar datos personales reales de estudiantes ni publicar credenciales de PostgreSQL, AWS o claves privadas de firma.
