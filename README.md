# MPAZ NEB

## Descripción del proyecto

MPAZ NEB es una aplicación móvil educativa desarrollada para apoyar la nivelación de estudiantes ficticios de 1.º a 4.º básico en las áreas de Lectura y Matemática.

El proyecto corresponde a la **Evaluación Parcial 2 de la asignatura Desarrollo de Aplicaciones Móviles (DSY1105)**.

La aplicación permite gestionar actividades educativas, asignarlas mediante códigos temporales, responder preguntas y consultar resultados y observaciones pedagógicas.

## Integrantes del equipo

- **Matías Pardo:** Administración pedagógica, gestión de actividades y persistencia de datos con Room.
- **Noemí Pichiguen:** Selección de perfiles y módulo Estudiante.
- **Kathryn Mora:** Módulo Asistente de aula, asignaciones y seguimiento.

## Tecnologías utilizadas

- Android Studio
- Kotlin
- Jetpack Compose
- Material Design 3
- Arquitectura MVVM
- ViewModel y StateFlow
- Room Database
- Git y GitHub
- Trello

## Funcionalidades implementadas

### Administración pedagógica

- Crear y editar actividades de Lectura y Matemática.
- Registrar preguntas, alternativas y respuestas correctas.
- Activar y desactivar actividades.
- Gestionar usuarios ficticios.
- Consultar resultados generales y observaciones.

### Asistente de aula

- Consultar actividades disponibles.
- Seleccionar estudiantes ficticios.
- Asignar actividades educativas.
- Generar códigos temporales de acceso.
- Consultar seguimiento y resultados.
- Registrar observaciones pedagógicas.
- Copiar y compartir códigos usando funciones nativas de Android.

### Estudiante

- Ingresar mediante un código temporal.
- Consultar la actividad asignada.
- Responder preguntas de Lectura y Matemática.
- Realizar un máximo de tres intentos por pregunta.
- Recibir retroalimentación visual.
- Consultar resultados.

## Arquitectura y almacenamiento

La aplicación utiliza una organización basada en MVVM:

- **Model:** Representa las entidades y los datos.
- **View:** Pantallas creadas con Jetpack Compose.
- **ViewModel:** Gestiona los estados y la lógica de interacción.
- **Repository:** Conecta la lógica de la aplicación con las fuentes de datos.
- **Room:** Permite almacenar localmente actividades, preguntas, asignaciones, respuestas y resultados.

## Navegación y diseño

La aplicación incluye tres perfiles: Administración pedagógica, Asistente de aula y Estudiante.

La interfaz utiliza componentes de Material 3, navegación funcional entre vistas, íconos, mensajes de validación y animaciones de retroalimentación.

## Recursos nativos

Se incorporaron funciones nativas de Android para copiar códigos al portapapeles y compartir información ficticia mediante el menú del sistema.

## Cómo ejecutar el proyecto

1. Clonar o descargar el repositorio de GitHub.
2. Abrir el proyecto en Android Studio.
3. Sincronizar las dependencias de Gradle.
4. Seleccionar un emulador Android compatible.
5. Ejecutar el módulo `app`.
6. Seleccionar un perfil y probar sus funcionalidades.

## Pruebas realizadas

Se verificó en el emulador:

- Validación de formularios.
- Creación y consulta de actividades.
- Asignación mediante códigos temporales.
- Resolución de preguntas y control de intentos.
- Persistencia de resultados después de cerrar y abrir la aplicación.
- Navegación entre los tres perfiles.
- Animaciones de retroalimentación.
- Funciones nativas de copiar y compartir.

## Planificación y colaboración

El equipo utiliza GitHub para el control de versiones y Trello para organizar las tareas, responsables y avances del proyecto.

## Consideraciones

Este proyecto corresponde a un prototipo académico y utiliza exclusivamente datos ficticios. No se almacenan datos personales reales de estudiantes.