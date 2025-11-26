# eventos-uy-tprog-2025
Proyecto eventos.uy – Taller de Programación 2025

- **Tarea 1**: lógica de negocio + aplicación de escritorio en Swing.
- **Tarea 2**: sitio web para escritorio (JSP/Servlets) integrado al Servidor Central.
- **Tarea 3**: dispositivo móvil (RWD), Web Services, persistencia con JPA/HSQLDB y empaquetado con Maven.

---

##  Descripción general

La plataforma permite gestionar **usuarios, eventos, ediciones, registros, patrocinios, instituciones y categorías**.

- Usuarios:
  - **Asistentes**: nickname, nombre, apellido, mail, fecha de nacimiento, institución.
  - **Organizadores**: nickname, nombre, mail, descripción, sitio web.
- Eventos:
  - Nombre único, sigla, descripción, fecha de alta, categorías.
- Ediciones:
  - Nombre único, sigla, fechas de inicio/fin/alta, ciudad, país, organizador, tipos de registro.
- Tipos de registro:
  - Nombre, descripción, costo, cupo.
- Patrocinios:
  - Institución, nivel (Platino/Oro/Plata/Bronce), aporte, tipo/cantidad de registros gratuitos, código de patrocinio.
- Registros:
  - Asistente, edición, tipo de registro, fecha, costo (0 si usa patrocinio válido).

Se manejan distintos **actores** según el componente:

- **Administrador** (Estación de Trabajo).
- **Visitante**, **Asistente** y **Organizador** (Sitio Web).
- **Asistente** (Dispositivo Móvil).

---

##  Arquitectura

El sistema se divide en varios componentes:

### 1. Servidor Central

- Contiene la **lógica de negocio** y el modelo de dominio.
- Expone servicios para:
  - Estación de Trabajo (llamadas directas).
  - Servidor Web y Dispositivo Móvil (vía **Web Services JAX-WS RPC**).
- Usa **JPA (EclipseLink)** sobre **HSQLDB** para persistir:
  - Ediciones archivadas.
  - Sus registros y asistentes/organizadores asociados.

### 2. Estación de Trabajo (Swing)

- Aplicación de escritorio para el **Administrador**.
- Permite:
  - Alta / consulta / modificación de usuarios.
  - Gestión de eventos, ediciones, tipos de registro, patrocinios, instituciones.
  - Registro a ediciones.
  - Aceptar / rechazar ediciones ingresadas.
  - Ver estadísticas de eventos más visitados.

### 3. Sitio Web (Cliente Web)

Aplicación web para escritorio (Tomcat) que consume el Servidor Central.

Funcionalidades principales:

- Inicio / cierre de sesión (nickname o mail + contraseña).
- Alta de usuario (con imagen, validación de contraseña y datos).
- Consulta y modificación de perfil (datos, contraseña, imagen).
- Alta y consulta de eventos y ediciones (con imágenes y videos).
- Alta de tipos de registro, instituciones y patrocinios.
- Registro a ediciones (general o con código de patrocinio).
- Consulta de patrocinios y registros.
- Descarga de **certificado de asistencia** en PDF.
- Búsqueda global de eventos y ediciones, con filtros y ordenamiento.
- Métrica de los **5 eventos más visitados**.
- Validación de nickname/mail por **AJAX** al crear usuario.

### 4. Dispositivo Móvil (Cliente Móvil)

Aplicación web separada, diseñada con **Responsive Web Design** (Bootstrap u otro framework).

- Solo Asistentes autenticados.
- Inicio / cierre de sesión.
- Consulta de ediciones (con imagen + video embebido).
- Consulta de registros.
- Registro de asistencia a ediciones.

---

##  Funcionalidades por iteración

### Tarea 1 – Lógica + Swing

- Modelo de dominio con:
  - Usuarios (Asistente/Organizador), Instituciones.
  - Eventos, Ediciones, Tipos de Registro, Patrocinios, Registros.
- Estación de Trabajo en **Java Swing**:
  - Menú principal + internal frames por caso de uso.
  - Formularios con combos, validaciones básicas y diálogos de error.
- Casos de uso implementados (vía GUI + lógica):
  - Alta/consulta/modificación de usuarios.
  - Alta/consulta de eventos.
  - Alta/consulta de ediciones de eventos.
  - Alta/consulta de tipos de registro.
  - Alta/consulta de instituciones.
  - Alta/consulta de patrocinios.
  - Registro a ediciones y consulta de registros.
- Pruebas automáticas con **JUnit** y medición de cobertura con **EclEmma** (≥80% de la capa lógica).

### Tarea 2 – Sitio Web

- Desarrollo de la aplicación web **eventos.uy**:
  - **Parte 1**: Frontend estático (HTML5, CSS, JS).
  - **Parte 2**: Backend dinámico (JSP + Servlets) integrado al Servidor Central.
- Roles en el sitio: Visitante, Asistente, Organizador.
- Casos de uso web:
  - Alta, consulta y modificación de usuario (con imagen).
  - Inicio/cierre de sesión.
  - Alta/consulta de evento y edición (con imagen).
  - Alta/consulta de tipo de registro.
  - Registro a ediciones (incluyendo uso de patrocinios).
  - Alta/consulta de instituciones y patrocinios.
- Validación de formularios en el cliente con **JavaScript**.
- Herramientas de calidad:
  - **JUnit + EclEmma** (≥80% lógica).
  - **PMD y Checkstyle** sin errores en reglas seleccionadas.
- El Servidor Central se empaqueta como **JAR** y se incluye como dependencia del proyecto web.

### Tarea 3 – Web Services, móvil y persistencia

- Exposición de la lógica del Servidor Central como **Web Services JAX-WS (Jakarta XML Web Services)**.
- Configuración de endpoints y URLs mediante archivos `.properties`, sin hardcodear IPs/puertos.
- Cliente Web y Cliente Móvil consumen WS del Servidor Central.
- **Nuevos casos de uso / extensiones**:
  - Alta y consulta de ediciones con video (URL embebida).
  - Seguir / dejar de seguir usuarios.
  - Finalizar evento (no se muestran más en listados ni se crean nuevas ediciones).
  - Archivar ediciones:
    - Se persisten en HSQLDB (vía JPA).
    - La única forma de acceso pasa a ser por la base.
  - Registro de asistencia desde el dispositivo móvil.
  - Descarga de constancia de asistencia en PDF (iText).
  - Búsqueda y ranking de eventos más visitados (vía filtros en el Servidor Web).
  - Barra de búsqueda global siempre visible en el cabezal.
- Empaquetado final con **Maven**:
  - `servidor.jar` – Servidor Central + Estación de Trabajo.
  - `web.war` – aplicación web de escritorio.
  - `movil.war` – aplicación móvil (si se despliega separada).

---

##  Tecnologías

- **Lenguaje**: Java SE 21.
- **Lógica / Back-end**:
  - Java, JPA (EclipseLink), HSQLDB.
  - JAX-WS (Jakarta XML Web Services).
- **Escritorio**: Swing.
- **Web**:
  - HTML5, CSS, JavaScript.
  - JSP, Servlets, Filters.
  - Bootstrap (RWD para dispositivo móvil).
- **Testing y calidad**:
  - JUnit, EclEmma.
  - PMD, Checkstyle.
- **Build & Deploy**:
  - Apache Maven.
  - Tomcat (WARs para web/móvil).
  - Scripts de construcción para generar JAR/WAR.
