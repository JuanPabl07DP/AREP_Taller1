# **MEFLIX: Tu Plataforma de Gestión de Películas** 🎥🍿

¡Bienvenido a MEFLIX! Con este proyecto, puedes listar, añadir, actualizar y eliminar películas utilizando una API REST. 🚀

## ✨ **Características**

- CRUD Completo:
  - 🔍 Listar películas (GET).
  - ➕ Añadir nuevas películas (POST).
  - ✏️ Actualizar información de una película existente (PUT).
  - ❌ Eliminar películas por ID (DELETE).

 - Interfaz de Usuario:
   - Simple y amigable, diseñada para ser intuitiva.
   - Frontend creado en HTML, CSS y JavaScript.

  - Backend:
    - Desarrollado en Java con soporte para HTTP requests.
   
## ⚙️ **Requisitos de Instalación**

Antes de comenzar, asegúrate de tener instalado lo siguiente:

1. 🖥️ Java 17+
2. 🌐 Navegador Web
3. 🛠️ Maven
4. ⚡ Postman o Insomia

## 🛠️ **Instrucciones de Instalación**

1. Clona el repositorio
```
git clone https://github.com/JuanPabl07DP/AREP_Taller1.git
```

2. Compila el Proyecto con Maven
```
mvn clean package
```

3. Ejecuta la clase **SimpleWebService**

4. Abre el navegador e ingresa a http://localhost:8080 y explora la plataforma.

## ▶️ **¿Cómo Ejecutarlo?**

### **Backend**
El servidor inicia automáticamente en el puerto 8080. Aquí están las rutas principales que puedes usar:

- GET /api/movies - Lista todas las películas.
- POST /api/movies - Añade una película.
- PUT /api/movies - Actualiza una película existente.
- DELETE /api/movies - Elimina una película por ID.

### **Frontend**
El frontend se carga automáticamente cuando abres la dirección en el navegador: 
```
http://localhost:8080
```

## 🏗️ **Arquitectura del Prototipo**

### **Componentes Principales**

1. Frontend 🌐
- HTML
- CSS
- JavaScript

2. Backend 💻
- Java: Implementación del servidor y la API REST.
- Librerías:
  - HttpClient: Para manejar las solicitudes HTTP en las pruebas.
  - JSON: Para el manejo de respuestas y solicitudes JSON.

## ✅ **Evaluación (Pruebas Realizadas)**
Se implementaron y ejecutaron pruebas con JUnit para garantizar la funcionalidad del backend. 

Aquí están las pruebas clave:

1. Test de Listado de Películas (GET)
   - Se comprobó que las películas existentes se listan correctamente.
   - Verifica el estado 200 y la respuesta en formato JSON.

2. Test de Adición de Películas (POST)
   - Verifica que una película se añade correctamente.
   - Asegura que la película añadida aparece en el listado.

3. Test de Actualización de Películas (PUT)
   - Cambios en título, director y año se reflejan correctamente.

4. Test de Eliminación de Películas (DELETE)
   - Se asegura de que las películas se eliminen por ID.

5. Pruebas de Errores
   - Manejo adecuado de rutas inexistentes (retorna un error 404).
   - Mensajes claros en caso de errores del cliente.

### Ejecución de las pruebas:
Ejecuta la clase **SimpleWebServerTest**

## 🛠️**Autor**
- **Juan Pablo Daza Pereira** (JuanPabl07DP)
