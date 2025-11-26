<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import="java.util.*, java.net.URLEncoder,
            publicadores.PublicadorUsuarioService,
            publicadores.DtDatosUsuario, publicadores.DtDatosUsuarioArray,
            publicadores.DtEdicion, publicadores.DtRegistro" %>
<%@ page import="publicadores.DTArchEdicion" %>

<%
  String ctx = request.getContextPath();
  String nickSesion = (String) session.getAttribute("nick");

  Collection<DtDatosUsuario> usuarios = null;
  DtDatosUsuario usuario = null;
  Map<String, String> edicionToEvento = (Map<String, String>) request.getAttribute("edicionToEvento");
  String error = (String) request.getAttribute("error");

  Map<String,String> fotos = (Map<String,String>) request.getAttribute("fotos");
  String usrImagenUrl = (String) request.getAttribute("usrImagenUrl");

  Object attrUsuarios = request.getAttribute("usuarios");
  Object attrUsuario = request.getAttribute("usuario");
  if (attrUsuario instanceof DtDatosUsuario) {
    usuario = (DtDatosUsuario) attrUsuario;
  }

  try {
    if (attrUsuarios instanceof Collection) {
      usuarios = (Collection<DtDatosUsuario>) attrUsuarios;
    } else {
      PublicadorUsuarioService svc = new PublicadorUsuarioService();
      publicadores.PublicadorUsuario port = svc.getPublicadorUsuarioPort();
      DtDatosUsuarioArray arr = port.obtenerUsuariosDT();
      if (arr != null && arr.getItem() != null) {
        usuarios = new ArrayList<>();
        usuarios.addAll(arr.getItem());
      }
    }

    if (usuario == null) {
      String nickParam = request.getParameter("nick");
      if (nickParam != null && !nickParam.isBlank()) {
        PublicadorUsuarioService svc2 = new PublicadorUsuarioService();
        publicadores.PublicadorUsuario port2 = svc2.getPublicadorUsuarioPort();
        try { usuario = port2.obtenerDatosUsuario(nickParam); } catch (Exception ignore) {}
      }
    }
  } catch (Exception ex) {
    ex.printStackTrace();
    if (usuarios == null) usuarios = Collections.emptyList();
  }

  Boolean esPerfilOrganizadorAttr = (Boolean) request.getAttribute("esPerfilOrganizador");
  boolean esPerfilOrganizador = (esPerfilOrganizadorAttr != null)
      ? esPerfilOrganizadorAttr.booleanValue()
      : (usuario != null && (usuario.getDesc() != null || usuario.getLink() != null));

  Boolean esSuPropioPerfilAttr = (Boolean) request.getAttribute("esSuPropioPerfil");
  boolean esSuPropioPerfil = (esSuPropioPerfilAttr != null) ? esSuPropioPerfilAttr.booleanValue()
                        : (usuario != null && nickSesion != null && nickSesion.equals(usuario.getNickname()));
  Boolean yaLoSigoAttr = (Boolean) request.getAttribute("yaLoSigo");
  boolean yaLoSigo = (yaLoSigoAttr != null) ? yaLoSigoAttr.booleanValue() : false;

  /* === ARCHIVADAS: lista que deja el servlet (puede venir null) === */
  @SuppressWarnings("unchecked")
  List<DTArchEdicion> archOrgList = (List<DTArchEdicion>) request.getAttribute("archivadasOrganizadasList");
  if (archOrgList == null) archOrgList = java.util.Collections.emptyList();
  boolean hayArchivadasOrg = !archOrgList.isEmpty();

  /* Alerts (éxito/advertencias/errores) */
  @SuppressWarnings("unchecked")
  List<String> AOK   = (List<String>) request.getAttribute("alerts_ok");
  @SuppressWarnings("unchecked")
  List<String> AWARN = (List<String>) request.getAttribute("alerts_warn");
  @SuppressWarnings("unchecked")
  List<String> AERR  = (List<String>) request.getAttribute("alerts_err");
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
    .btn-link.user-nick{display:inline-flex;align-items:center;gap:.4em;background:none;border:none;color:#0d6efd;font-weight:600;font-size:.95rem;text-decoration:none;padding:.2em .4em;border-radius:6px;transition:color .2s, background .2s}
    .btn-link.user-nick i{font-size:1.1em;color:#0d6efd}
    .btn-link.user-nick:hover{background:rgba(13,110,253,.1);color:#0b5ed7;cursor:pointer;text-decoration:none}
    .usuario-card .avatar{width:72px;height:72px;border-radius:50%;object-fit:cover;background:#f3f4f6;display:block;margin-bottom:.25rem}
    .perfil-header{display:flex;align-items:center;gap:1rem;margin-bottom:1rem}
    .perfil-header .avatar{width:96px;height:96px;border-radius:50%;object-fit:cover;background:#f3f4f6}
    .no-avatar{width:72px;height:72px;border-radius:50%;display:flex;align-items:center;justify-content:center;background:#f3f4f6;color:#6b7280;font-size:.72rem;text-align:center;padding:6px;box-sizing:border-box;margin-bottom:.25rem}
    .perfil-header .no-avatar{width:96px;height:96px;font-size:.82rem;padding:8px;margin:0}
    .follow-bar{display:flex;align-items:center;gap:.75rem;margin:.5rem 0}
    .btn-link{background:none;border:none;color:#007bff;text-decoration:underline;cursor:pointer;padding:0}
    .hint{font-size:.85rem;color:#6b7280}
    .tag-arch { font-weight:600; opacity:.85; }
    .archivada { opacity:.92; }

    /* Collapsible */
    .collapse-wrap { border:1px solid #e5e7eb; border-radius:12px; background:#fff; }
    .collapse-head { display:flex; align-items:center; justify-content:space-between; width:100%; padding:.8rem 1rem; background:#fafafa; border-radius:12px; border:0; cursor:pointer; font-weight:600; }
    .collapse-body { overflow:hidden; max-height:0; transition:max-height .28s ease; }
    .collapse-body-inner { padding:1rem; }
    .collapse-wrap.open .collapse-body { max-height:2000px; } /* lo suficiente para el form */
    .alerts-stack .alert{padding:.6rem .8rem; border-radius:8px}
    .alert--ok{background:#e9f7ef; border:1px solid #ccebd9}
    .alert--warn{background:#fff8e6; border:1px solid #ffe6ad}
    .alert--err{background:#fdecea; border:1px solid #f5c2c0}
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
      <section class="card event-card" style="padding: 1.5rem;">
      <% if (error != null) { %>
        <div class="alerts-stack" style="margin-bottom:1rem">
          <div class="alert alert--err"><strong>✕</strong> <%= error %></div>
        </div>
      <% } %>

      <% if (usuario == null) { %>
        <!-- ===========================
             LISTADO DE USUARIOS
        ============================ -->
        <h1>Usuarios registrados</h1>
        <div class="usuarios-grid">
          <% Map<String, String> usuariosImagenUrlMap = (Map<String, String>) request.getAttribute("usuariosImagenUrlMap"); %>
          <% if (usuarios == null || usuarios.isEmpty()) { %>
            <p>No hay usuarios registrados.</p>
          <% } else {
         	  for (DtDatosUsuario u : usuarios) {
        		    String fotoUrl = (usuariosImagenUrlMap != null) ? usuariosImagenUrlMap.get(u.getNickname()) : (ctx + "/img/user-default.jpg");
        %>
        <div class="card usuario-card">
          <img class="avatar"
               src="<%= fotoUrl %>"
               alt="Avatar de <%= u.getNickname() %>"
               onerror="this.onerror=null;this.src='<%= ctx %>/img/user-default.jpg';">

              <h3>
                <form action="<%=ctx%>/usuario/ConsultaUsuario" method="get" style="display:inline;">
                  <input type="hidden" name="nick" value="<%=u.getNickname()%>" />
                  <button type="submit" class="btn-link user-nick">
                    <i class='bx bxs-user-circle'></i>
                    <span><strong>Ver Perfil</strong></span>
                  </button>
                </form>
              </h3>

              <p><strong>Nickname:</strong> <%=u.getNickname()%></p>

              <%
                @SuppressWarnings("unchecked")
                Map<String,Boolean> yaLoSigoMap = (Map<String,Boolean>) request.getAttribute("yaLoSigoMap");
                boolean yaSigueUser = false;
                if (yaLoSigoMap != null && yaLoSigoMap.get(u.getNickname()) != null) {
                  yaSigueUser = yaLoSigoMap.get(u.getNickname()).booleanValue();
                }
              %>
              <% if (nickSesion != null && !nickSesion.equals(u.getNickname())) { %>
                <form class="follow-form" data-nick="<%= u.getNickname() %>"
                      action="<%= ctx %>/<%= (!yaSigueUser ? "usuario/seguir" : "usuario/dejarSeguir") %>"
                      method="post"
                      style="display:inline;margin-top:.5rem;">
                  <input type="hidden" name="a" value="<%= u.getNickname() %>">
                  <input type="hidden" name="listar" value="1">
                  <button type="submit" class="btn"><%= (!yaSigueUser ? "Seguir" : "Dejar de seguir") %></button>
                </form>
              <% } %>
            </div>
          <% } } %>
        </div>

      <% } else { %>
        <!-- ===========================
     PERFIL INDIVIDUAL
============================ -->
<h1>Perfil de <%= usuario.getNickname() %></h1>

<div class="perfil-header">
  <%
    String imgName   = (usuario != null) ? usuario.getImagen() : null;
    // Usar la URL dinámica generada por el servlet
    String avatarSrc = usrImagenUrl;
    // Campos opcionales por rol (con tolerancia a stubs distintos)
    String descripcion = null, link = null, apellido = null, institucion = null;
    String fechaNac = null;
    try { descripcion = usuario.getDesc(); } catch (Throwable ignore) {}
    try { link        = usuario.getLink(); } catch (Throwable ignore) {}
    try { apellido    = usuario.getApellido(); } catch (Throwable ignore) {}
    try { fechaNac    = usuario.getFechaNac(); } catch (Throwable ignore) {}
    try { institucion = usuario.getNombreInstitucion(); } catch (Throwable ignore) {}
    if (institucion == null || institucion.isBlank()) {
      try { institucion = usuario.getNombreInstitucion(); } catch (Throwable ignore) {}
    }
  %>

  <img class="avatar"
       src="<%= avatarSrc %>"
       alt="Avatar de <%= usuario.getNickname() %>"
       style="width:96px;height:96px;border-radius:50%;object-fit:cover"
       onerror="this.onerror=null;this.src='<%= ctx %>/img/user-default.jpg';">

  <div id="datosUsuario">
    <div class="follow-bar" style="margin:.5rem 0;">
      <% if (!esSuPropioPerfil && nickSesion != null) { %>
        <form class="follow-form"
              data-nick="<%= usuario.getNickname() %>"
              action="<%= ctx %>/<%= (!yaLoSigo ? "usuario/seguir" : "usuario/dejarSeguir") %>"
              method="post"
              style="display:inline;">
          <input type="hidden" name="a" value="<%= usuario.getNickname() %>">
          <button type="submit" class="btn"><%= (!yaLoSigo ? "Seguir" : "Siguiendo") %></button>
        </form>
      <% } %>
    </div>

    <!-- Datos básicos -->
    <p><strong>Nickname:</strong> <span><%= usuario.getNickname() %></span></p>
    <p><strong>Email:</strong>    <span><%= usuario.getEmail() %></span></p>
    <% if (usuario.getNombre() != null && !usuario.getNombre().isBlank()) { %>
      <p><strong>Nombre:</strong>   <span><%= usuario.getNombre() %></span></p>
    <% } %>

    <!-- Datos específicos de ASISTENTE (si existen en el DTO) -->
    <% if (apellido != null && !apellido.isBlank()) { %>
      <p><strong>Apellido:</strong> <span><%= apellido %></span></p>
    <% } %>
    <% if (fechaNac != null) { %>
      <p><strong>Fecha de nacimiento:</strong> <span><%= fechaNac %></span></p>
    <% } %>
    <% if (institucion != null && !institucion.isBlank()) { %>
      <p><strong>Institución:</strong> <span><%= institucion %></span></p>
    <% } %>

    <!-- Datos específicos de ORGANIZADOR (si existen en el DTO) -->
    <% if (descripcion != null && !descripcion.isBlank()) { %>
      <p><strong>Descripción:</strong> <span><%= descripcion %></span></p>
    <% } %>
    <% if (link != null && !link.isBlank()) { %>
      <p><strong>Sitio web:</strong>
        <a href="<%= link %>" target="_blank" rel="noopener"><%= link %></a>
      </p>
    <% } %>
  </div>
</div>

        <!-- ====== ALERTS (si vinieron del servlet) ====== -->
        <div class="alerts-stack" style="display:grid; gap:.5rem; margin: .5rem 0 1rem;">
          <% if (AOK != null) { for (String m : AOK) { %>
            <div class="alert alert--ok"><strong>✓</strong> <%= m %></div>
          <% } } %>
          <% if (AWARN != null) { for (String m : AWARN) { %>
            <div class="alert alert--warn"><strong>!</strong> <%= m %></div>
          <% } } %>
          <% if (AERR != null) { for (String m : AERR) { %>
            <div class="alert alert--err"><strong>✕</strong> <%= m %></div>
          <% } } %>
        </div>

        <!-- ====== BOTÓN QUE DESPLIEGA EL FORM ====== -->
        <% if (esSuPropioPerfil) { %>
        <div class="collapse-wrap" id="editCollapse">
          <button class="collapse-head" type="button" id="toggleEdit">
            <span>Modificar datos</span>
            <i class='bx bx-chevron-down' id="chev"></i>
          </button>
          <div class="collapse-body">
            <div class="collapse-body-inner">

              <!-- ====== FORMULARIO (igual al que veníamos usando) ====== -->
              <form id="formEditarUsuario"
                    action="<%= ctx %>/usuario/modificar"
                    method="post"
                    enctype="multipart/form-data"
                    class="form form--perfil">

                <!-- Identidad (no editables) -->
                <div class="grid-2">
                  <label>
                    <span>Nickname</span>
                    <input type="text" value="<%= usuario.getNickname() %>" disabled>
                  </label>
                  <label>
                    <span>Email</span>
                    <input type="email" value="<%= usuario.getEmail() %>" disabled>
                  </label>
                </div>

                <!-- Nombre -->
                <label>
                  <span>Nombre</span>
                  <input type="text" name="nombre"
                         value="<%= (request.getAttribute("form_nombre")!=null ? (String)request.getAttribute("form_nombre")
                                         : (usuario.getNombre()!=null ? usuario.getNombre() : "")) %>"
                         required>
                </label>

                <%-- Perfil público (si aplica) --%>
                <%
                  boolean muestraDesc = (usuario.getDesc() != null) || (request.getAttribute("form_descripcion")!=null);
                  boolean muestraLink = (usuario.getLink() != null) || (request.getAttribute("form_link")!=null);
                  if (muestraDesc || muestraLink) {
                %>
                  <div class="grid-2">
                    <% if (muestraDesc) { %>
                    <label>
                      <span>Descripción</span>
                      <textarea name="descripcion" rows="3"><%= (
                          request.getAttribute("form_descripcion")!=null ? (String)request.getAttribute("form_descripcion")
                          : (usuario.getDesc()!=null ? usuario.getDesc() : "")
                      ) %></textarea>
                    </label>
                    <% } %>

                    <% if (muestraLink) { %>
                    <label>
                      <span>Sitio web</span>
                      <input type="url" name="link"
                             value="<%= (
                                  request.getAttribute("form_link")!=null ? (String)request.getAttribute("form_link")
                                  : (usuario.getLink()!=null ? usuario.getLink() : "")
                             ) %>">
                    </label>
                    <% } %>
                  </div>
                <% } %>

                <%-- Datos personales (si aplica) --%>
                <%
                  boolean muestraApellido = (usuario.getApellido() != null) || (request.getAttribute("form_apellido")!=null);
                  boolean muestraFecha    = (usuario.getFechaNac() != null) || (request.getAttribute("form_fechaNac")!=null);
                  boolean muestraInst     = false;
                  try { muestraInst = (usuario.getNombreInstitucion()!=null) || (request.getAttribute("form_institucion")!=null); }
                  catch (Throwable ignore) {}
                  if (muestraApellido || muestraFecha || muestraInst) {
                %>
                  <div class="grid-3">
                    <% if (muestraApellido) { %>
                    <label>
                      <span>Apellido</span>
                      <input type="text" name="apellido"
                             value="<%= (
                                request.getAttribute("form_apellido")!=null ? (String)request.getAttribute("form_apellido")
                                : (usuario.getApellido()!=null ? usuario.getApellido() : "")
                             ) %>">
                    </label>
                    <% } %>

                    <% if (muestraFecha) { %>
                    <label>
                      <span>Fecha de nacimiento</span>
                      <input type="date" name="fechaNac"
                             value="<%= (
                               request.getAttribute("form_fechaNac")!=null ? (String)request.getAttribute("form_fechaNac")
                               : (usuario.getFechaNac()!=null ? usuario.getFechaNac().toString() : "")
                             ) %>">
                    </label>
                    <% } %>

                    <% if (muestraInst) { %>
                    <label>
                      <span>Institución</span>
                      <input type="text" name="institucion"
                             value="<%= (
                               request.getAttribute("form_institucion")!=null ? (String)request.getAttribute("form_institucion")
                               : (usuario.getNombreInstitucion()!=null ? usuario.getNombreInstitucion() : "")
                             ) %>">
                    </label>
                    <% } %>
                  </div>
                <% } %>

                <!-- Cambio de contraseña (opcional) -->
                <fieldset class="field-group">
                  <legend>Cambiar contraseña (opcional)</legend>
                  <div class="grid-2">
                    <label>
                      <span>Nueva contraseña</span>
                      <input type="password" name="password" autocomplete="new-password" minlength="6">
                    </label>
                    <label>
                      <span>Repetir nueva contraseña</span>
                      <input type="password" name="password2" autocomplete="new-password" minlength="6">
                    </label>
                  </div>
                  <p class="hint">Si completás ambos campos y coinciden, se actualizará tu contraseña.</p>
                </fieldset>

                <!-- Imagen -->
                <div class="grid-2" style="align-items:center">
                  <div>
                    <span>Imagen actual</span><br>
                    <img src="<%= usrImagenUrl %>"
                         alt="Avatar de <%= usuario.getNickname() %>"
                         style="width:96px;height:96px;border-radius:50%;object-fit:cover"
                         onerror="this.onerror=null;this.src='<%= ctx %>/img/user-default.jpg';">

                  </div>
                  <label>
                    <span>Subir nueva imagen</span>
                    <input type="file" name="imagen" accept="image/*">
                  </label>
                </div>

                <!-- Acciones -->
                <div class="actions" style="margin-top:1rem; display:flex; gap:.5rem;">
                  <button type="submit" class="btn">Guardar cambios</button>
                  <a class="btn btn--ghost" href="<%= ctx %>/usuario/ConsultaUsuario?nick=<%= java.net.URLEncoder.encode(usuario.getNickname(), java.nio.charset.StandardCharsets.UTF_8) %>">Cancelar</a>
                </div>
              </form>
            </div>
          </div>
        </div>
        <% } %>

        <!-- ====== RED SOCIAL ====== -->
<%
  List<String> seguidores = (List<String>) request.getAttribute("segidores"); // (typo original)
  seguidores = (List<String>) request.getAttribute("seguidores");
  List<String> seguidos = (List<String>) request.getAttribute("seguidos");
%>
        <div style="margin-top: 1rem; border-top: 1px solid #ddd; padding-top: .75rem;">
          <h2>Red social</h2>
          <div style="display:flex;gap:3rem;flex-wrap:wrap;">
            <div>
              <strong>Seguidores:</strong><br>
              <% if (seguidores == null || seguidores.isEmpty()) { %>
                <span>—</span>
              <% } else { %>
                <ul style="margin:.25rem 0;padding-left:1rem;">
                  <% for (String s : seguidores) { %>
                    <li>
                      <form action="<%=ctx%>/usuario/ConsultaUsuario" method="get" style="display:inline;">
                        <input type="hidden" name="nick" value="<%= s %>">
                        <button type="submit" class="btn-link user-nick">
                          <i class='bx bxs-user'></i> <%= s %>
                        </button>
                      </form>
                    </li>
                  <% } %>
                </ul>
              <% } %>
            </div>

            <div>
              <strong>Seguidos:</strong><br>
              <% if (seguidos == null || seguidos.isEmpty()) { %>
                <span>—</span>
              <% } else { %>
                <ul style="margin:.25rem 0;padding-left:1rem;">
                  <% for (String s : seguidos) { %>
                    <li>
                      <form action="<%=ctx%>/usuario/ConsultaUsuario" method="get" style="display:inline;">
                        <input type="hidden" name="nick" value="<%= s %>">
                        <button type="submit" class="btn-link user-nick">
                          <i class='bx bxs-user'></i> <%= s %>
                        </button>
                      </form>
                    </li>
                  <% } %>
                </ul>
              <% } %>
            </div>
          </div>
        </div>

        <!-- ====== EDICIONES (vivas y archivadas) ====== -->
<%
  boolean tieneEdiciones = usuario.getEdiciones() != null && !usuario.getEdiciones().getEdicion().isEmpty();
  boolean tieneRegistros = usuario.getRegistros() != null && !usuario.getRegistros().getRegistro().isEmpty();
%>

<% if (tieneEdiciones || hayArchivadasOrg) { %>
  <h2>Ediciones de eventos</h2>
  <ul class="lista-ediciones">
  <%
    @SuppressWarnings("unchecked")
    Set<String> archivablesSet =
      (Set<String>) request.getAttribute("archivablesSet");
    if (archivablesSet == null) archivablesSet = java.util.Collections.emptySet();

    String ownerNick = (usuario != null ? usuario.getNickname() : null);
  %>
<% if (tieneEdiciones) {
     for (DtEdicion e : usuario.getEdiciones().getEdicion()) {
       String estado = (e.getEstado() != null) ? e.getEstado().toString() : "Sin estado";
       boolean mostrar = "Aceptada".equalsIgnoreCase(estado) ||
                         (esSuPropioPerfil && ("Ingresada".equalsIgnoreCase(estado) || "Rechazada".equalsIgnoreCase(estado)));
       if (mostrar) {

         
         String eventoNombre = "";
         if (edicionToEvento != null && edicionToEvento.get(e.getNombre()) != null) {
             eventoNombre = edicionToEvento.get(e.getNombre());
         } else if (e.getEvento() != null && e.getEvento().getNombre() != null) {
             eventoNombre = e.getEvento().getNombre();
         } else {
             eventoNombre = "(Evento desconocido)"; 
         }

         String edicionNombre = e.getNombre();
         String claveCompuesta = (eventoNombre == null ? "" : eventoNombre) + "::" + edicionNombre;
         boolean esArchivable = archivablesSet.contains(edicionNombre) || archivablesSet.contains(claveCompuesta);
         String edKey = (eventoNombre == null ? "" : eventoNombre) + "::" + e.getNombre();
%>

  <li style="display:flex; align-items:center; gap:.5rem; flex-wrap: wrap;">
    <form action="<%= ctx %>/edicion/ConsultaEdicion" method="get" style="display:inline;">
      <input type="hidden" name="evento" value="<%= eventoNombre %>" />
      <input type="hidden" name="edicion" value="<%= e.getNombre() %>" />
      <a href="#" onclick="this.closest('form').submit(); return false;"
         style="color:inherit; text-decoration:none; font-weight:600;">
        <%= e.getNombre() %>
      </a>
    </form>

    <span>(<%= e.getFechaInicio() %> - <%= e.getFechaFin() %>)</span>
    <em class="estado"> — <%= estado %></em>

    <% if (esSuPropioPerfil && archivablesSet.contains(edKey)) { %>
      <form action="<%= ctx %>/usuario/archivarEdicion" method="post" style="display:inline; margin-left:.5rem;">
        <input type="hidden" name="edicion" value="<%= edKey %>"/>
        <input type="hidden" name="owner"   value="<%= ownerNick %>"/>
        <button type="submit" class="btn btn--small"
                onclick="return confirm('¿Archivar la edición «<%= e.getNombre() %>»? Esta acción la saca de los listados.');">
          Archivar
        </button>
      </form>
    <% } %>
  </li>

<%
       } // if mostrar
     } // for
   } // if tieneEdiciones
%>

  <%-- === ARCHIVADAS (como ORGANIZADOR) --%>
  <% if (esPerfilOrganizador && hayArchivadasOrg) {
       for (DTArchEdicion a : archOrgList) { %>
    <li class="archivada" style="display:flex; align-items:center; gap:.5rem; flex-wrap: wrap;">
      <span style="font-weight:600;"><%= a.getSiglaEdicion() %></span>
      <span>(<%= a.getFechaInicio() %> - <%= a.getFechaFin() %>)</span>
      <em class="estado"> — Aceptada</em>
      <span class="tag-arch"> — (ARCHIVADA)</span>
    </li>
  <% } } %>
  </ul>
<% } %>

<% if (esSuPropioPerfil && tieneRegistros) { %>
  <h2>Eventos registrados</h2>
  <ul class="lista-eventos">
    <% for (DtRegistro r : usuario.getRegistros().getRegistro()) {
         String eventoNombre = (edicionToEvento != null) ? edicionToEvento.get(r.getEdicion()) : "";
    %>
      <li>
        <form action="<%= ctx %>/edicion/ConsultaEdicion" method="get" style="display:inline;">
          <input type="hidden" name="evento" value="<%= eventoNombre %>" />
          <input type="hidden" name="edicion" value="<%= r.getEdicion() %>" />
          <a href="#" onclick="this.closest('form').submit(); return false;"
             style="color:inherit; text-decoration:none; font-weight:600;">
             <%= r.getEdicion() %>
          </a>
        </form>
        <span>| <strong>Tipo:</strong> <%= r.getTipoRegistro() %></span>
        <span>| <strong>Fecha registro:</strong> <%= r.getFechaRegistro() %></span>
        <span>| <strong>Costo:</strong> $<%= r.getCosto() %></span>
        <a class="btn" href="<%= ctx %>/registro/ConsultaRegistroEdicion?idRegistro=<%= URLEncoder.encode(r.getIdentificador(), "UTF-8") %>">
          Ver detalles
        </a>
      </li>
    <% } %>
  </ul>
<% } %>

      <% } %>
      </section>
    </main>
  </div>

  <script>
    // Toggle "Modificar datos"
    (function(){
      var wrap = document.getElementById('editCollapse');
      var btn  = document.getElementById('toggleEdit');
      var chev = document.getElementById('chev');
      if (wrap && btn) {
        btn.addEventListener('click', function(){
          wrap.classList.toggle('open');
          if (chev) chev.classList.toggle('bx-rotate-180');
        });
      }
    })();

    // Follow AJAX — igual que tenías
    (function(){
      function isFollowAction(action){ return action && action.endsWith('/seguir'); }
      document.querySelectorAll('form.follow-form').forEach(form => {
        form.addEventListener('submit', async function(evt){
          evt.preventDefault();
          const btn = form.querySelector('button[type="submit"]');
          if (!btn) return;
          btn.disabled = true;
          const formData = new FormData(form);
          try {
            const resp = await fetch(form.action, {
              method: 'POST',
              headers: { 'X-Requested-With': 'XMLHttpRequest' },
              body: formData,
              credentials: 'same-origin'
            });
            if (!resp.ok) throw new Error('HTTP ' + resp.status);
            const text = (await resp.text()).trim();
            const currentlyFollow = isFollowAction(form.action);
            if (text === 'OK') {
              if (currentlyFollow) {
                btn.textContent = 'Dejar de seguir';
                form.action = form.action.replace('/seguir', '/dejarSeguir');
              } else {
                btn.textContent = 'Seguir';
                form.action = form.action.replace('/dejarSeguir', '/seguir');
              }
            } else {
              // mismo fallback
              if (currentlyFollow) {
                btn.textContent = 'Dejar de seguir';
                form.action = form.action.replace('/seguir', '/dejarSeguir');
              } else {
                btn.textContent = 'Seguir';
                form.action = form.action.replace('/dejarSeguir', '/seguir');
              }
            }
          } catch (err) {
            console.error('Follow error', err);
            alert('Error procesando la acción. Intente de nuevo.');
          } finally {
            btn.disabled = false;
          }
        });
      });

      var ctx = "<%=ctx%>";
      window.addEventListener('pageshow', function(evt){
        try {
          var forms = Array.from(document.querySelectorAll('form.follow-form'));
          if (!forms || forms.length === 0) return;
          var nicks = forms.map(f => f.dataset.nick).filter(Boolean);
          if (nicks.length === 0) return;
          var url = ctx + '/usuario/ConsultaUsuario?checkSeguidos=1&nicks=' + encodeURIComponent(nicks.join(',')) + '&_=' + Date.now();
          fetch(url, { credentials: 'same-origin', cache: 'no-store' })
            .then(function(r){ if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
            .then(function(obj){
              forms.forEach(function(form){
                var nick = form.dataset.nick;
                if (!nick) return;
                var btn = form.querySelector('button[type="submit"]');
                var sigue = !!obj[nick];
                if (btn) {
                  if (sigue) {
                    btn.textContent = 'Dejar de seguir';
                    form.action = form.action.replace('/seguir','/dejarSeguir');
                    btn.classList.add('following'); btn.classList.remove('not-following');
                  } else {
                    btn.textContent = 'Seguir';
                    form.action = form.action.replace('/dejarSeguir','/seguir');
                    btn.classList.add('not-following'); btn.classList.remove('following');
                  }
                }
              });
            })
            .catch(function(err){ console.debug('Revalidate follow state failed:', err); });
        } catch (e) { console.debug('pageshow handler error', e); }
      });
    })();
  </script>
</body>
</html>