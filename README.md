
# MPAZ NEB

## Descripción del proyecto

**MPAZ NEB** es una aplicación móvil educativa desarrollada para apoyar la nivelación de estudiantes ficticios de 1.º a 4.º básico, en las áreas de Lectura y Matemática.

El proyecto corresponde a la **Evaluación Parcial 5 de la asignatura Desarrollo de Aplicaciones Móviles (DSY1105)**.

La aplicación permite gestionar actividades educativas, registrar estudiantes, realizar asignaciones mediante códigos temporales, responder preguntas, consultar resultados y registrar observaciones pedagógicas.

El sistema utiliza Kotlin y Jetpack Compose en Android, arquitectura MVVM, persistencia local con Room, servicios REST desarrollados en Spring Boot, PostgreSQL alojado en AWS RDS y consumo de la API externa PokéAPI.

## Integrantes del equipo

| Integrante | Responsabilidad |
|---|---|
| Matías Pardo | Administración pedagógica, actividades, preguntas, estudiantes e infraestructura backend. |
| Noemí Pichiguen | Selección de perfiles, módulo Estudiante, respuestas, resultados e integración PokéAPI. |
| Kathryn Mora | Módulo Asistente, asignaciones, seguimiento, observaciones, integración remota, corrección del estado de asignaciones y documentación técnica. |

## Tecnologías utilizadas

### Aplicación Android

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
- AWS EC2.
- AWS RDS.
- Systemd para la ejecución automática del backend.

### Pruebas y colaboración

- JUnit.
- MockK.
- Mockito.
- Gradle.
- JaCoCo.
- Git y GitHub.
- Trello.

## Arquitectura del sistema

La aplicación utiliza la arquitectura **MVVM (Model-View-ViewModel)** para separar las interfaces, la lógica de presentación y el acceso a los datos.

```text
Interfaz Android - Jetpack Compose
               |
            ViewModel
               |
            Repository
           /    |     \
        Room  Retrofit  PokéAPI
                  |
              Spring Boot
                  |
              PostgreSQL
                AWS RDS
```

### Capas de Android

- **Model:** representa las entidades y los datos de la aplicación.
- **View:** contiene las pantallas desarrolladas con Jetpack Compose.
- **ViewModel:** administra los estados y la lógica de interacción.
- **Repository:** permite consultar y modificar información.
- **Room:** proporciona persistencia local.
- **Retrofit:** permite consumir servicios REST.

### Capas del backend

- **Controller:** expone los endpoints HTTP.
- **Service:** implementa la lógica de negocio.
- **Repository:** accede a PostgreSQL mediante Spring Data JPA.
- **Model:** contiene las entidades persistentes.
- **DTO:** facilita la transferencia y validación de información.

## Funcionalidades del sistema

### Perfil Administrador

- Crear, editar y eliminar actividades.
- Registrar preguntas y alternativas.
- Activar y desactivar actividades.
- Registrar estudiantes ficticios.
- Consultar estudiantes almacenados en PostgreSQL.
- Consultar los resultados de las actividades.
- Validar los campos obligatorios.
- Proteger la eliminación de actividades activas.

### Perfil Asistente

- Consultar el catálogo de actividades.
- Filtrar actividades por módulo, curso y dificultad.
- Consultar estudiantes activos desde AWS.
- Validar formularios de asignación.
- Asignar actividades mediante servicios REST.
- Generar, copiar y compartir códigos temporales.
- Consultar asignaciones y sus estados.
- Visualizar contadores de asignaciones.
- Consultar aciertos, intentos y rendimiento.
- Registrar y actualizar observaciones pedagógicas.
- Manejar estados de carga y errores de conexión.

### Perfil Estudiante

- Ingresar mediante un código temporal.
- Consultar las actividades asignadas.
- Responder preguntas de Lectura y Matemática.
- Realizar un máximo de tres intentos por pregunta.
- Recibir retroalimentación visual mediante Pokémon.
- Registrar respuestas en PostgreSQL.
- Consultar resultados y rendimiento.

## API REST

La aplicación Android consume los servicios desarrollados en Spring Boot mediante Retrofit.

| Método | Endpoint | Función |
|---|---|---|
| GET | `/api/actividades` | Consultar actividades |
| POST | `/api/actividades` | Crear una actividad |
| GET | `/api/actividades/{id}` | Consultar una actividad |
| GET | `/api/preguntas/actividad/{actividadId}` | Consultar preguntas |
| GET | `/api/estudiantes/activos` | Consultar estudiantes activos |
| POST | `/api/estudiantes` | Registrar un estudiante |
| GET | `/api/asignaciones` | Consultar asignaciones |
| POST | `/api/asignaciones` | Crear una asignación |
| GET | `/api/asignaciones/codigo/{codigo}` | Buscar asignación por código |
| GET | `/api/asignaciones/estudiante/{alias}` | Consultar asignaciones por estudiante |
| POST | `/api/respuestas` | Registrar una respuesta |
| GET | `/api/respuestas/asignacion/{asignacionId}` | Consultar respuestas |
| GET | `/api/resultados/asignacion/{asignacionId}` | Consultar resultados |
| POST | `/api/resultados/asignacion/{asignacionId}/calcular` | Calcular resultados |
| PATCH | `/api/resultados/{id}/observacion` | Actualizar una observación |

El backend también implementa operaciones de modificación, eliminación y activación de actividades, además de la gestión de preguntas.

## API externa: PokéAPI

MPAZ NEB utiliza **PokéAPI** como servicio externo para mostrar Pokémon durante la retroalimentación educativa.

URL base:

```text
https://pokeapi.co/api/v2/
```

La integración se realiza mediante Retrofit, utilizando una interfaz independiente de la API REST propia del proyecto.

Se incorporaron recursos gráficos locales de respaldo para:

- Pikachu.
- Bulbasaur.
- Eevee.

Estos recursos permiten mantener la retroalimentación visual cuando las imágenes remotas no se pueden cargar correctamente.

## Persistencia de datos

### Persistencia local

La aplicación utiliza Room Database para almacenar información local en Android.

### Persistencia remota

La información compartida se almacena en PostgreSQL, alojado en AWS RDS, mediante los servicios REST de Spring Boot.

Nombre de la base de datos:

```text
mpaz_neb
```

Principales entidades:

- Actividades.
- Preguntas.
- Estudiantes.
- Asignaciones.
- Respuestas.
- Resultados.

Las observaciones pedagógicas se almacenan en el campo `observacion` de los resultados.

El backend actualiza una asignación al estado **Completada** cuando todas las preguntas han sido respondidas correctamente o se han agotado los tres intentos permitidos por pregunta.

## Despliegue en AWS

El backend Spring Boot está desplegado en una instancia **AWS EC2** conectada a PostgreSQL en **AWS RDS**.

### Dirección pública verificada

Dirección comprobada el 22 de noviembre de 2026, hora de Chile:

```text
http://3.85.113.237:8080/
```

Endpoint utilizado para comprobar la conexión:

```text
http://3.85.113.237:8080/api/estudiantes/activos
```

Se obtuvo una respuesta HTTP 200 con estudiantes activos registrados en PostgreSQL.

### Configuración en Android

La dirección del backend se encuentra en:

```text
app/src/main/java/com/example/mpazneb/data/remote/RetrofitClient.kt
```

Configuración utilizada para generar el APK final:

```kotlin
private const val BASE_URL = "http://3.85.113.237:8080/"
```

### Servicio Spring Boot

El backend está configurado como servicio de Ubuntu:

```text
mpaz-neb-api.service
```

Comando para comprobar su estado:

```bash
sudo systemctl status mpaz-neb-api.service --no-pager
```

El servicio quedó habilitado para iniciarse automáticamente con la instancia EC2.

### Consideraciones sobre AWS

La instancia de AWS Academy utiliza una dirección IPv4 pública dinámica.

Si la instancia se detiene y vuelve a iniciarse, la IP puede cambiar. En ese caso, será necesario actualizar `RetrofitClient.kt` y generar nuevamente el APK.

El grupo de seguridad de EC2 debe permitir el puerto TCP 8080 desde las direcciones IP autorizadas para utilizar la aplicación.

Si se utiliza una red diferente, como el Wi-Fi del instituto, puede ser necesario autorizar su IP pública.

## Pruebas unitarias Android

Se ejecutaron las siguientes pruebas unitarias:

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
| **Total** | **64** |

### Resultados

- Pruebas unitarias aprobadas: 64.
- Fallos: 0.
- Pruebas omitidas: 0.
- Compilación correcta.
- Resultado: `BUILD SUCCESSFUL`.

### Ejecutar pruebas

Desde PowerShell, en la carpeta del proyecto:

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
```

También se verificó la compilación de la versión release con:

```powershell
.\gradlew.bat :app:assembleRelease :app:testDebugUnitTest
```

Resultado final registrado:

```text
BUILD SUCCESSFUL in 57s
75 actionable tasks: 24 executed, 51 up-to-date
```

### Informe de pruebas

```text
app/build/reports/tests/testDebugUnitTest/index.html
```

## Cobertura de código con JaCoCo

Se generó un informe de cobertura de las pruebas unitarias Android.

| Indicador | Cobertura |
|---|---:|
| Instrucciones | 5 % |
| Ramas | 6 % |
| Líneas | 6,7 % |

Cobertura de instrucciones en paquetes seleccionados:

| Paquete | Cobertura |
|---|---:|
| repository.pokemon | 90 % |
| model.asistente | 75 % |
| viewmodel | 54 % |
| repository.estudiante | 52 % |
| model | 50 % |
| data.remote | 33 % |

Para generar el informe:

```powershell
.\gradlew.bat :app:createDebugUnitTestCoverageReport
```

Ubicación del informe:

```text
app/build/reports/coverage/test/debug/index.html
```

**Nota:** el porcentaje de pruebas aprobadas no equivale al porcentaje de cobertura. La cobertura global incluye clases y pantallas que no están completamente cubiertas por pruebas unitarias.

## Pruebas unitarias del backend

Se ejecutaron pruebas unitarias de los servicios de:

- Actividades.
- Preguntas.
- Estudiantes.
- Asignaciones.
- Respuestas.
- Resultados.

Resultado verificado en AWS:

- Pruebas ejecutadas: 70.
- Fallos: 0.
- Errores: 0.
- Compilación Maven exitosa.

### Ejecutar pruebas en Windows

```powershell
.\mvnw.cmd "-Dtest=*ServiceTest" clean test
```

### Ejecutar pruebas en Ubuntu

```bash
bash mvnw "-Dtest=*ServiceTest" clean package
```

Los informes se generan en:

```text
target/surefire-reports/
```

La prueba de contexto completo de Spring Boot requiere una conexión PostgreSQL correctamente configurada. En el entorno local se detectó un error JDBC relacionado con esta conexión, por lo que dicha prueba no está incluida entre las 70 pruebas unitarias aprobadas.

### Resumen general

| Componente | Pruebas unitarias aprobadas |
|---|---:|
| Android | 64 |
| Spring Boot | 70 |
| **Total** | **134** |

## Verificación funcional del sistema

Se comprobó el flujo completo de los tres perfiles:

1. Creación de una actividad educativa.
2. Registro de preguntas.
3. Activación de actividades.
4. Registro y consulta de estudiantes.
5. Asignación de actividades desde el perfil Asistente.
6. Generación de códigos temporales.
7. Ingreso del Estudiante mediante código.
8. Registro de respuestas.
9. Cálculo y consulta de resultados.
10. Registro de observaciones pedagógicas.
11. Actualización del estado de asignaciones a Completada.
12. Visualización de Pokémon como retroalimentación educativa.

También se comprobó el funcionamiento de Spring Boot después de reiniciar la instancia EC2.

## APK firmado de entrega

Se generó un APK firmado de la variante **release**, utilizando el almacén de claves existente del proyecto.

Archivo generado:

```text
app-release.apk
```

La generación se completó correctamente mediante Android Studio.

### Verificaciones realizadas

- Compilación release exitosa.
- Generación del APK firmado exitosa.
- Instalación correcta en el emulador Pixel 6.
- Apertura de la aplicación instalada.
- Conexión con el backend desplegado en AWS EC2.
- Consulta correcta de estudiantes desde PostgreSQL RDS.

La prueba final mostró en la aplicación los estudiantes:

- Estudiante Demo EPS.
- Estudiante Prueba 2.

La API respondió correctamente mediante HTTP 200 desde el ordenador utilizado para la verificación.

### Instalación del APK

1. Descargar o recibir el archivo `app-release.apk`.
2. Transferirlo al dispositivo Android o al emulador.
3. Autorizar la instalación desde la aplicación utilizada para abrir el archivo, si Android lo solicita.
4. Instalar la aplicación.
5. Abrir MPAZ NEB desde el icono instalado.
6. Comprobar que existe conectividad con AWS.
7. Consultar estudiantes y actividades.

### Conflictos de firma

Si Android muestra:

```text
INSTALL_FAILED_UPDATE_INCOMPATIBLE
```

significa que existe una versión instalada con el mismo identificador de paquete, pero firmada con una clave diferente.

Para solucionarlo, se debe desinstalar la versión anterior antes de instalar el APK firmado.

**Advertencia:** desinstalar una aplicación elimina sus datos locales. No elimina los registros guardados en PostgreSQL RDS.

El botón Run de Android Studio suele instalar una versión debug, que puede tener una firma diferente de la versión release.

Por tanto, para comprobar el APK firmado es recomendable abrirlo desde su icono en el dispositivo, sin reinstalarlo mediante Run.

### Seguridad de la firma

El archivo `.jks`, las contraseñas y las claves privadas no deben publicarse en GitHub ni compartirse junto con el APK.

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
- Restricciones en operaciones sobre actividades.
- Recursos gráficos de respaldo para Pokémon.

MPAZ NEB es un **prototipo académico**.

El acceso por perfiles y códigos temporales no constituye un sistema completo de autenticación para producción.

La comunicación HTTP utilizada en el entorno de evaluación debería reemplazarse por HTTPS en un despliegue productivo.

## Cómo ejecutar el proyecto

### Aplicación Android

1. Clonar el repositorio Android.
2. Abrir el proyecto en Android Studio.
3. Sincronizar Gradle.
4. Comprobar la URL en `RetrofitClient.kt`.
5. Verificar que AWS se encuentra disponible.
6. Iniciar un emulador o conectar un dispositivo Android.
7. Ejecutar el módulo `app` para desarrollo.
8. Seleccionar el perfil correspondiente.

### Backend Spring Boot

1. Clonar el repositorio del backend.
2. Configurar Java 21.
3. Configurar la conexión a PostgreSQL.
4. Descargar las dependencias Maven.
5. Compilar el proyecto.
6. Ejecutar Spring Boot.
7. Comprobar los endpoints REST.

## Repositorios y planificación

### Aplicación Android

https://github.com/npichiguen-alt/PROYECTO-MPAZ-NEB

### Backend Spring Boot

https://github.com/Matias-Pardo/MPAZ-NEB-API

### Tablero Trello

https://trello.com/b/EGRBtnop/

## Commits destacados

| Commit | Aporte |
|---|---|
| `940d5aa` | Gestión remota de actividades y preguntas |
| `3d4ee5d` | Integración remota del Asistente |
| `b2355be` | Persistencia de observaciones pedagógicas |
| `5ed6089` | Integración de estudiantes remotos |
| `85ef79e` | Corrección del estado de asignaciones en backend y pruebas |
| `fa6cc15` | Mejoras de PokéAPI y respaldos locales |
| `ec040ad` | Imágenes y pruebas de Pokémon |
| `10cfe78` | Actualización de la IP pública AWS en Retrofit |

## Estado final del proyecto

- [x] Código Android publicado en GitHub.
- [x] Backend Spring Boot publicado en GitHub.
- [x] Backend desplegado en AWS EC2.
- [x] PostgreSQL RDS conectado correctamente.
- [x] Integración con PokéAPI.
- [x] 64 pruebas unitarias Android aprobadas.
- [x] 70 pruebas unitarias backend aprobadas.
- [x] Informe de cobertura JaCoCo generado.
- [x] Flujo integrado de los tres perfiles verificado.
- [x] Corrección del estado de asignaciones.
- [x] Visualización de Pokémon comprobada.
- [x] APK firmado release generado.
- [x] APK firmado instalado en Pixel 6.
- [x] Conexión del APK con AWS verificada.
- [x] Consulta de estudiantes de PostgreSQL desde Android comprobada.

## Consideraciones finales

MPAZ NEB utiliza información ficticia con fines educativos.

La disponibilidad de las funciones remotas depende de que AWS EC2, Spring Boot y PostgreSQL RDS estén operativos y de que la red utilizada tenga acceso autorizado al puerto correspondiente.

No se deben publicar credenciales de AWS, PostgreSQL ni claves privadas de firma.
