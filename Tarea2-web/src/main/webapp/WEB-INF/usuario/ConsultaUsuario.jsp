<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*, java.net.URLEncoder, logica.datatypes.*" %>

<%
  String ctx = request.getContextPath();
  String nickSesion = (String) session.getAttribute("nick");

  Collection<DTDatosUsuario> usuarios = (Collection<DTDatosUsuario>) request.getAttribute("usuarios");
  DTDatosUsuario usuario = (DTDatosUsuario) request.getAttribute("usuario");
  Map<String, String> edicionToEvento = (Map<String, String>) request.getAttribute("edicionToEvento");
  String error = (String) request.getAttribute("error");

  Map<String,String> fotos = (Map<String,String>) request.getAttribute("fotos");
  String usrImagenUrl = (String) request.getAttribute("usrImagenUrl");

  // Rol real del perfil consultado (lo setea el servlet)
  Boolean esPerfilOrganizadorAttr = (Boolean) request.getAttribute("esPerfilOrganizador");
  boolean esPerfilOrganizador = (esPerfilOrganizadorAttr != null)
      ? esPerfilOrganizadorAttr.booleanValue()
      : (usuario != null && (usuario.getDesc() != null || usuario.getLink() != null));

  // follow flags
  Boolean esSuPropioPerfilAttr = (Boolean) request.getAttribute("esSuPropioPerfil");
  boolean esSuPropioPerfil = (esSuPropioPerfilAttr != null) ? esSuPropioPerfilAttr.booleanValue()
                        : (usuario != null && nickSesion != null && nickSesion.equals(usuario.getNickname()));
  Boolean yaLoSigoAttr = (Boolean) request.getAttribute("yaLoSigo");
  boolean yaLoSigo = (yaLoSigoAttr != null) ? yaLoSigoAttr.booleanValue() : false;
%>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Consulta de Usuarios — Eventos.uy</title>
  <link rel="stylesheet" href="<%=ctx%>/css/style.css">
  <link rel="stylesheet" href="<%=ctx%>/css/ConsultaUsuario.css">
  <link rel="stylesheet" href="<%=ctx%>/css/layoutMenu.css">
  <link rel="stylesheet" href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css">
  <style>
    .usuario-card .avatar { width:72px; height:72px; border-radius:50%; object-fit:cover; background:#f3f4f6; display:block; margin-bottom:.25rem; }
    .perfil-header { display:flex; align-items:center; gap:1rem; margin-bottom:1rem; }
    .perfil-header .avatar { width:96px; height:96px; border-radius:50%; object-fit:cover; background:#f3f4f6; }
    .no-avatar {
      width:72px; height:72px; border-radius:50%;
      display:flex; align-items:center; justify-content:center;
      background:#f3f4f6; color:#6b7280; font-size:.72rem; text-align:center;
      padding:6px; box-sizing:border-box; margin-bottom:.25rem;
    }
    .perfil-header .no-avatar { width:96px; height:96px; font-size:.82rem; padding:8px; margin:0; }
    .follow-bar { display:flex; align-items:center; gap:.75rem; margin:.5rem 0; }
    .btn-link { background:none; border:none; color:#007bff; text-decoration:underline; cursor:pointer; padding:0; }
    .hint { font-size:.85rem; color:#6b7280; }
  </style>
</head>

<body>
  <!-- Header -->
  <jsp:include page="/WEB-INF/templates/header.jsp" />

  <div class="container row layout-inicio">
    <!-- Sidebar -->
    <jsp:include page="/WEB-INF/templates/menu.jsp" />

    <!-- Main -->
    <main class="main-inicio">
      <% if (error != null) { %>
        <p class="error"><%=error%></p>
      <% } %>

      <% if (usuario == null) { %>
        <!-- ===========================
             LISTADO DE USUARIOS
        ============================ -->
        <h1>Usuarios registrados</h1>
        <div class="usuarios-grid">
          <% if (usuarios == null || usuarios.isEmpty()) { %>
            <p>No hay usuarios registrados.</p>
          <% } else {
               for (DTDatosUsuario u : usuarios) {
                 String fotoUrl = (fotos == null) ? null : fotos.get(u.getNickname());
          %>
            <div class="card usuario-card">
              <% if (fotoUrl != null && !fotoUrl.isBlank()) { %>
                <img class="avatar" src="<%= fotoUrl %>" alt="Avatar de <%= u.getNickname() %>">
              <% } else { %>
                <div class="no-avatar" aria-label="Sin imagen">sin imagen</div>
              <% } %>

              <h3>
                <form action="<%=ctx%>/usuario/ConsultaUsuario" method="get" style="display:inline;">
                  <input type="hidden" name="nick" value="<%=u.getNickname()%>" />
                  <button type="submit" class="btn-link">
                    <i class='bx bxs-id-card' style="font-size:1.2em;margin-right:0.3em;"></i>
                    <span><%=u.getNickname()%></span>
                  </button>
                </form>
              </h3>
              <p><strong>Nickname:</strong> <%=u.getNickname()%></p>
            </div>
          <% } } %>
        </div>

      <% } else { %>
        <!-- ===========================
             PERFIL INDIVIDUAL
        ============================ -->
        <h1>Perfil de <%= usuario.getNickname() %></h1>

<div class="perfil-header">
  <% if (usrImagenUrl != null && !usrImagenUrl.isBlank()) { %>
    <img class="avatar" src="<%= usrImagenUrl %>" alt="Avatar de <%= usuario.getNickname() %>">
  <% } else { %>
    <div class="no-avatar" aria-label="Sin imagen">sin imagen</div>
  <% } %>

  <div id="datosUsuario">
    <p><strong>Nickname:</strong> <span><%= usuario.getNickname() %></span></p>
    <p><strong>Email:</strong> <span><%= usuario.getEmail()%></span></p>

    <!-- === BARRA SEGUIR / DEJAR SEGUIR (FORM SEPARADO, NO ANIDADO) === -->
    <div class="follow-bar" style="margin:.5rem 0;">
      <% if (!esSuPropioPerfil && nickSesion != null) { %>
        <form action="<%= ctx %>/<%= (!yaLoSigo ? "usuario/seguir" : "usuario/dejarSeguir") %>" method="post" style="display:inline;">
          <input type="hidden" name="a" value="<%= usuario.getNickname() %>">
          <button type="submit" class="btn"><%= (!yaLoSigo ? "Seguir" : "Dejar de seguir") %></button>
        </form>
      <% } %>
    </div>

    <!-- === FORM DE EDICIÓN (SEPARADO) === -->
    <form id="formEditarUsuario" action="<%= ctx %>/usuario/modificar" method="post" enctype="multipart/form-data">
      <input type="hidden" name="nick" value="<%= usuario.getNickname() %>">

              <!-- Campo de imagen (en edición) -->
              <div class="edit-mode" style="display:none; margin:.25rem 0;">
                <label for="imagen"><strong>Foto:</strong></label>
                <input type="file" id="imagen" name="imagen" accept="image/*">
                <div class="hint">Formatos: JPG/PNG/WebP. Tamaño máx: 5 MB.</div>
              </div>

              <% if (!esPerfilOrganizador) { %>
                <!-- 🔹 CAMPOS DE ASISTENTE -->
                <p><strong>Nombre:</strong>
                  <span class="view-mode"><%= usuario.getNombre() %></span>
                  <input class="edit-mode" type="text" name="nombre" value="<%= usuario.getNombre() %>" style="display:none;">
                </p>

                <p><strong>Apellido:</strong>
                  <span class="view-mode"><%= usuario.getApellido() %></span>
                  <input class="edit-mode" type="text" name="apellido" value="<%= usuario.getApellido() %>" style="display:none;">
                </p>

                <p><strong>Fecha de nacimiento:</strong>
                  <span class="view-mode"><%= usuario.getFechaNac() != null ? usuario.getFechaNac() : "—" %></span>
                  <input class="edit-mode" type="date" name="fechaNac" 
                         value="<%= usuario.getFechaNac() != null ? usuario.getFechaNac() : "" %>" 
                         style="display:none;">
                </p>

                <p><strong>Institución:</strong>
                  <span class="view-mode">
                    <% String instName = (usuario.getInstitucion() != null ? usuario.getInstitucion() : null);
                       Map<String,String> instFotos = (Map<String,String>) request.getAttribute("instFotos");
                       String instImg = (instFotos != null && instName != null) ? instFotos.get(instName) : null;
                    %>
                    <% if (instName == null) { %>
                      — 
                    <% } else { %>
                      <span style="display:inline-flex;align-items:center;gap:0.4em;">
                        <% if (instImg != null) { %>
                          <img src="<%= instImg %>" alt="Logo <%= instName %>" style="width:24px;height:24px;object-fit:cover;border-radius:4px;vertical-align:middle;" />
                        <% } %>
                        <span><%= instName %></span>
                      </span>
                    <% } %>
                  </span>
                   <select class="edit-mode" name="institucion" style="display:none;">
                     <option value="">— Seleccione —</option>
                     <%
                       Collection<String> instituciones = (Collection<String>) request.getAttribute("instituciones");
                       if (instituciones != null) {
                         for (String inst : instituciones) {
                           String selected = (usuario.getInstitucion() != null && usuario.getInstitucion().equals(inst)) ? "selected" : "";
                     %>
                         <option value="<%= inst %>" <%= selected %>><%= inst %></option>
                     <% } } %>
                   </select>
                 </p>

              <% } else { %>
                <!-- 🔹 CAMPOS DE ORGANIZADOR -->
                <p><strong>Descripción:</strong>
                  <span class="view-mode"><%= usuario.getDesc() != null ? usuario.getDesc() : "—" %></span>
                  <textarea class="edit-mode" name="descripcion" rows="3" style="display:none;"><%= usuario.getDesc() %></textarea>
                </p>

                <p><strong>Link:</strong>
                  <span class="view-mode"><%= usuario.getLink() != null ? usuario.getLink() : "—" %></span>
                  <input class="edit-mode" type="text" name="link" value="<%= usuario.getLink() != null ? usuario.getLink() : "" %>" style="display:none;">
                </p>
              <% } %>

              <% boolean esSuPropioPerfil2 = esSuPropioPerfil; %>
              <% if (esSuPropioPerfil2) { %>
                <p><strong>Contraseña:</strong>
                  <span class="view-mode">••••••••</span>
                  <input class="edit-mode" type="password" name="password" placeholder="Nueva contraseña" style="display:none;">
                </p>

                <div id="accionesPerfil" style="margin-top:1rem;">
                  <button type="button" id="btnEditar" class="btn">Modificar datos</button>
                  <button type="submit" id="btnGuardar" class="btn" style="display:none;">Guardar</button>
                  <button type="button" id="btnCancelar" class="btn" style="display:none;">Cancelar</button>
                </div>
              <% } %>
            </form>
          </div>
        </div>

        <% boolean tieneEdiciones = usuario.getEdiciones() != null && !usuario.getEdiciones().isEmpty();
           boolean tieneRegistros = usuario.getRegistros() != null && !usuario.getRegistros().isEmpty();
        %>

        <% if (tieneEdiciones) { %>
          <h2>Ediciones de eventos</h2>
          <ul class="lista-ediciones">
            <% for (DTEdicion e : usuario.getEdiciones()) {
                 String estado = (e.getEstado() != null) ? e.getEstado().toString() : "Sin estado";
                 boolean mostrar = "Aceptada".equalsIgnoreCase(estado) ||
                                   (esSuPropioPerfil && ("Ingresada".equalsIgnoreCase(estado) || "Rechazada".equalsIgnoreCase(estado)));
                 if (mostrar) {
                   String eventoNombre = (edicionToEvento != null) ? edicionToEvento.get(e.getNombre()) : "";
            %>
              <li>
                <form action="<%= ctx %>/edicion/ConsultaEdicion" method="get" style="display:inline;">
                  <input type="hidden" name="evento" value="<%= eventoNombre %>" />
                  <input type="hidden" name="edicion" value="<%= e.getNombre() %>" />
                  <button type="submit" class="btn-link">
                    <strong><%= e.getNombre() %></strong>
                  </button>
                </form>
                <span>(<%= e.getFechaInicio() %> - <%= e.getFechaFin() %>)</span>
                <em class="estado"> — <%= estado %></em>
              </li>
            <% } } %>
          </ul>
        <% } %>

        <% if (esSuPropioPerfil && tieneRegistros) { %>
          <h2>Eventos registrados</h2>
          <ul class="lista-eventos">
            <% for (DTRegistro r : usuario.getRegistros()) {
                 String eventoNombre = (edicionToEvento != null) ? edicionToEvento.get(r.getEdicion()) : "";
            %>
              <li>
                <form action="<%= ctx %>/edicion/ConsultaEdicion" method="get" style="display:inline;">
                  <input type="hidden" name="evento" value="<%= eventoNombre %>" />
                  <input type="hidden" name="edicion" value="<%= r.getEdicion() %>" />
                  <button type="submit" class="btn-link">
                    <strong><%= r.getEdicion() %></strong>
                  </button>
                </form>
                <span>| <strong>Tipo:</strong> <%= r.getTipoRegistro() %></span>
                <span>| <strong>Fecha registro:</strong> <%= r.getFechaRegistro() %></span>
                <span>| <strong>Costo:</strong> $<%= r.getCosto() %></span>
                <a class="btn" href="<%= ctx %>/registro/ConsultaRegistroEdicion?idRegistro=<%= URLEncoder.encode(r.getId(), "UTF-8") %>">Ver detalles</a>
              </li>
            <% } %>
          </ul>
        <% } %>
      <% } %>
    </main>
  </div>

  <script>
    const btnEditar = document.getElementById("btnEditar");
    const btnGuardar = document.getElementById("btnGuardar");
    const btnCancelar = document.getElementById("btnCancelar");

    if (btnEditar) {
      btnEditar.addEventListener("click", () => {
        document.querySelectorAll(".view-mode").forEach(e => e.style.display = "none");
        document.querySelectorAll(".edit-mode").forEach(e => e.style.display = "inline");
        btnEditar.style.display = "none";
        btnGuardar.style.display = "inline";
        btnCancelar.style.display = "inline";
      });

      btnCancelar.addEventListener("click", () => {
        document.querySelectorAll(".view-mode").forEach(e => e.style.display = "inline");
        document.querySelectorAll(".edit-mode").forEach(e => e.style.display = "none");
        btnEditar.style.display = "inline";
        btnGuardar.style.display = "none";
        btnCancelar.style.display = "none";
        const fi = document.getElementById("imagen");
        if (fi) fi.value = "";
      });
    }
  </script>
</body>
</html>
