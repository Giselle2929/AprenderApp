# AprendeApp 🎓

**AprendeApp** es una plataforma educativa móvil desarrollada en Kotlin siguiendo el patrón de diseño **MVC (Modelo-Vista-Controlador)**. La aplicación permite gestionar recursos académicos de forma eficiente, diferenciando las capacidades del usuario según su rol (Estudiante o Docente).

## 🚀 Características Principales

### 1. Autenticación Segura y Roles
*   **Registro de Usuarios**: Validación estricta de contraseñas (mínimo 12 caracteres, mayúsculas, minúsculas, números y símbolos).
*   **Firebase Auth**: Integración con Firebase para la gestión de sesiones.
*   **Sistema de Roles**:
    *   **Estudiante**: Puede visualizar recursos, buscar, marcar favoritos (persistencia local) y calificar contenido.
    *   **Docente**: Acceso total al CRUD (Crear, Leer, Actualizar y Eliminar) de recursos académicos.

### 2. Gestión de Recursos (CRUD)
*   **Consumo de API**: Integración con MockAPI para el almacenamiento persistente de recursos.
*   **Operaciones**: Implementación de métodos GET, POST, PUT y DELETE mediante Retrofit.
*   **Formularios**: Validación de campos obligatorios y retroalimentación visual mediante Loaders y Toasts.

### 3. Interfaz y Experiencia de Usuario (UI/UX)
*   **Búsqueda Avanzada**: Filtrado en tiempo real por ID, Título o Tipo de recurso.
*   **Ordenamiento**: Opciones para ordenar la lista por título o categoría.
*   **Diseño Moderno**: Uso de Material Design 3, Glide para carga de imágenes y vistas personalizadas.
*   **Feedback**: Alertas de confirmación para eliminación y estados de carga.

### 4. Persistencia Local
*   **Room Database**: Implementación de base de datos local para la persistencia de la lista de "Favoritos" del usuario, permitiendo acceso rápido a sus recursos guardados.

## 🛠️ Stack Tecnológico
*   **Lenguaje**: Kotlin
*   **Arquitectura**: MVC
*   **Red**: Retrofit 2 & GSON
*   **Base de Datos Local**: Room
*   **Autenticación**: Firebase Authentication & Firestore (para perfiles)
*   **Imágenes**: Glide
*   **UI**: Material Components & Android Jetpack

## 📋 Requisitos del Proyecto
Este proyecto cumple con los criterios de evaluación del **Tercer Desafío Práctico - DSM**:
- Registro y Login (10%)
- Validación de Contraseña Segura (10%)
- Sistema de Roles (10%)
- CRUD Completo Docente (25%)
- Búsqueda y Ordenamiento (10%)
- Persistencia de Favoritos y Rating (15%)
- Calidad de UI/UX (10%)
- Puntualidad (10%)

## ✒️ Autores
*   **Gisselle** - *Desarrollo y Diseño*

---
*Este proyecto fue desarrollado con fines educativos para la asignatura de Desarrollo de Software para Dispositivos Móviles.*
