# AprendeApp 🎓

**AprendeApp** es una plataforma educativa móvil desarrollada en Kotlin siguiendo el patrón de diseño **MVC (Modelo-Vista-Controlador)**. La aplicación permite gestionar recursos académicos de forma eficiente, diferenciando las capacidades del usuario según su rol (Estudiante o Docente).

---

### 📥 Descarga la Aplicación
Puedes descargar la última versión ejecutable (APK) directamente desde aquí:

[![Descargar APK](https://img.shields.io/badge/Descargar-APK-brightgreen?style=for-the-badge&logo=android)](https://github.com/Giselle2929/AprenderApp/raw/main/app/build/outputs/apk/debug/app-debug.apk)

---

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

## 📋 video https://drive.google.com/file/d/1RSa8ciBVbYAq8tY4Fi2pFu5_zdCcqc6M/view?usp=drivesdk

---
*Este proyecto fue desarrollado con fines educativos para la asignatura de Desarrollo de Software para Dispositivos Móviles.*
