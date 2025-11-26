<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx  = request.getContextPath();
  String nick = (String) session.getAttribute("nick");
  String rol  = (String) session.getAttribute("rol"); // "ASISTENTE" | "ORGANIZADOR"
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Crear Edición de Evento — Eventos.uy</title>
  <link rel="stylesheet" href="<%=ctx%>/css/style.css">
  <link rel="stylesheet" href="<%=ctx%>/css/AltaEvento.css">
  <link rel="stylesheet" href="<%=ctx%>/css/layoutMenu.css">
</head>
<body>
<jsp:include page="/WEB-INF/templates/header.jsp" />

<div class="container row" style="margin-top:1rem;">
  <jsp:include page="/WEB-INF/templates/menu.jsp" />

  <!-- Main -->
  <main class="container" style="flex:2; min-width:0;">
    <section class="form-card-altaEvento form-card--wide">
      <h2>Crear Edición de Evento</h2>

      <% if (request.getAttribute("error") != null) { %>
        <p style="color:#c00"><%= request.getAttribute("error") %></p>
      <% } %>

      <% if (request.getAttribute("sinEventos") != null && (Boolean)request.getAttribute("sinEventos")) { %>
        <p style="color:#c00">No hay eventos disponibles. Debe crear un evento antes de poder crear una edición.</p>
      <% } %>

      <form id="form-alta-edicion" method="post" action="<%=ctx%>/edicion/alta" enctype="multipart/form-data">
        <div class="grid-2">
          <div class="form-group-altaEvento">
            <label for="evento">Evento<span class="req">*</span></label>
            <select id="evento" name="evento" required <%= (request.getAttribute("sinEventos") != null && (Boolean)request.getAttribute("sinEventos")) ? "disabled" : "" %>>
              <option value="">Seleccione un evento</option>
              <% 
                java.util.List eventos = (java.util.List) request.getAttribute("listaEventos");
                if (eventos != null) {
                  for (Object obj : eventos) {
                    String nombre = null;
                    try {
                      java.lang.reflect.Method m = obj.getClass().getMethod("getNombre");
                      Object v = m.invoke(obj);
                      nombre = v == null ? "" : v.toString();
                    } catch (Exception e) { nombre = obj.toString(); }
              %>
                <option value="<%=nombre%>"><%=nombre%></option>
              <%   }
                }
              %>
            </select>
          </div>

          <div class="form-group-altaEvento">
            <label for="nombre">Nombre de la edición<span class="req">*</span></label>
            <input type="text" id="nombre" name="nombre" required>
          </div>
        </div>

        <div class="form-group-altaEvento">
          <label for="sigla">Sigla<span class="req">*</span></label>
          <input type="text" id="sigla" name="sigla" required>
        </div>

        <div class="form-group-altaEvento">
          <label for="desc">Descripción<span class="req">*</span></label>
          <textarea id="desc" name="desc" rows="4" required></textarea>
        </div>

        <div class="grid-2">
          <div class="form-group-altaEvento">
            <label for="fechaInicio">Fecha de inicio<span class="req">*</span></label>
            <input type="date" id="fechaInicio" name="fechaInicio" required>
          </div>

          <div class="form-group-altaEvento">
            <label for="fechaFin">Fecha de fin<span class="req">*</span></label>
            <input type="date" id="fechaFin" name="fechaFin" required>
          </div>
        </div>

        <div class="grid-2">
          <div class="form-group-altaEvento">
            <label for="ciudad">Ciudad<span class="req">*</span></label>
            <input type="text" id="ciudad" name="ciudad" required>
          </div>
          <div class="form-group-altaEvento">
            <label for="pais">País<span class="req">*</span></label>
            <input type="text" id="pais" name="pais" required>
          </div>
        </div>

        <div class="form-group-altaEvento">
          <label for="imagen">Imagen (opcional)</label>
          <input type="file" id="imagen" name="imagen" accept="image/*">
          <small class="helper-note">Formatos sugeridos: JPG/PNG. Tamaño máx. 2&nbsp;MB.</small>
        </div>

        <div class="form-group-altaEvento">
          <label for="videoUrl">Video (URL opcional)</label>
          <input type="url" id="videoUrl" name="videoUrl" placeholder="https://..." pattern="https?://.+" title="Ingrese una URL válida (empieza con http:// o https://)">
          <small class="helper-note">Pegá la URL del video (YouTube, Vimeo, etc.). No es obligatoria.</small>
        </div>

        <p class="form-hint-altaEvento">Los campos marcados con <span class="req">*</span> son obligatorios.</p>

        <div class="form-actions-altaEvento">
          <button type="submit" class="btn-guardar-altaEvento" <%= (request.getAttribute("sinEventos") != null && (Boolean)request.getAttribute("sinEventos")) ? "disabled" : "" %>>Guardar</button>
          <button type="submit" class="btn-cancelar-altaEvento btn" name="accion" value="cancelar">Cancelar</button>
        </div>
      </form>

      <script>
        (function () {
          const form = document.getElementById('form-alta-edicion');
          const cancelarBtn = document.querySelector('.btn-cancelar-altaEvento');
          cancelarBtn.addEventListener('click', function(e) {
            Array.from(form.querySelectorAll('[required]')).forEach(function(input) {
              input.removeAttribute('required');
            });
          });

          // verificación básica de tamaño de imagen 
          const inputImg = document.getElementById('imagen');
          const MAX_BYTES = 2 * 1024 * 1024; // 2MB
          inputImg.addEventListener('change', function () {
            const file = this.files && this.files[0];
            if (file && file.size > MAX_BYTES) {
              alert('La imagen supera 2 MB. Elegí un archivo más liviano.');
              this.value = '';
            }
          });

          // validación básica de URL de video (opcional)
          const videoInput = document.getElementById('videoUrl');
          form.addEventListener('submit', function (e) {
            const val = videoInput && videoInput.value && videoInput.value.trim();
            if (val && !/^https?:\/\/.+/i.test(val)) {
              e.preventDefault();
              alert('La URL del video no es válida. Debe comenzar con http:// o https://');
              videoInput.focus();
            }
          });
        })();
      </script>
    </section>
  </main>
</div>

</body>
</html>