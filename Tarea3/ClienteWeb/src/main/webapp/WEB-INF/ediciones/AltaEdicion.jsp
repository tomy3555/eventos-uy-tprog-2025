<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx  = request.getContextPath();
  String nick = (String) session.getAttribute("nick");
  String rol  = (String) session.getAttribute("rol");
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Alta de Edición de Evento — Eventos.uy</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <link rel="stylesheet" href="<%=ctx%>/css/style.css">
  <link rel="stylesheet" href="<%=ctx%>/css/layoutMenu.css">
  <link rel="stylesheet" href="<%=ctx%>/css/altaTipoRegistro.css">
  <link rel="stylesheet" href="<%=ctx%>/css/custom.css">
  <link rel="stylesheet" href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css">
</head>
<body>

  <jsp:include page="/WEB-INF/templates/header.jsp" />

  <div class="container row" style="margin-top:1rem;">
    <jsp:include page="/WEB-INF/templates/menu.jsp" />

    <main class="container" style="flex:2; min-width:0;">
      <section class="card event-card">
        <h2>Alta de Edición de Evento</h2>

        <% if (request.getAttribute("error") != null) { %>
          <p style="color:#c00"><%= request.getAttribute("error") %></p>
        <% } %>

        <% if (request.getAttribute("sinEventos") != null && (Boolean)request.getAttribute("sinEventos")) { %>
          <p style="color:#c00">No hay eventos disponibles. Debe crear un evento antes de poder crear una edición.</p>
        <% } %>

        <form id="form-alta-edicion" method="post" action="<%=ctx%>/edicion/alta" enctype="multipart/form-data">
          
          <div class="form-group-altaTipoRegistro">
            <label for="evento">Evento <span style="color:red">*</span></label>
            <select id="evento" name="evento" required 
                    <%= (request.getAttribute("sinEventos") != null && (Boolean)request.getAttribute("sinEventos")) ? "disabled" : "" %>>
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
              <%   } } %>
            </select>
          </div>

          <div class="form-group-altaTipoRegistro">
            <label>Nombre de la edición <span style="color:red">*</span></label>
            <input type="text" name="nombre" required>
          </div>

          <div class="form-group-altaTipoRegistro">
            <label>Sigla <span style="color:red">*</span></label>
            <input type="text" name="sigla" required>
          </div>

          <div class="form-group-altaTipoRegistro">
            <label>Descripción <span style="color:red">*</span></label>
            <textarea name="desc" rows="4" required></textarea>
          </div>

          <div class="form-group-altaTipoRegistro">
            <label>Fecha de inicio <span style="color:red">*</span></label>
            <input type="date" name="fechaInicio" required>
          </div>

          <div class="form-group-altaTipoRegistro">
            <label>Fecha de fin <span style="color:red">*</span></label>
            <input type="date" name="fechaFin" required>
          </div>

          <div class="form-group-altaTipoRegistro">
            <label>Ciudad <span style="color:red">*</span></label>
            <input type="text" name="ciudad" required>
          </div>

          <div class="form-group-altaTipoRegistro">
            <label>País <span style="color:red">*</span></label>
            <input type="text" name="pais" required>
          </div>

          <div class="form-group-altaTipoRegistro">
            <label>Imagen (opcional)</label>
            <input type="file" name="imagen" accept="image/*">
            <small class="helper-note">Formatos sugeridos: JPG/PNG. Tamaño máx. 2 MB.</small>
          </div>

          <div class="form-group-altaTipoRegistro">
            <label>Video (URL opcional)</label>
            <input type="url" name="videoUrl" placeholder="https://..." pattern="https?://.+" 
                   title="Ingrese una URL válida (empieza con http:// o https://)">
            <small class="helper-note">Pegá la URL del video (YouTube, Vimeo, etc.).</small>
          </div>

          <p style="font-size:0.9rem; margin-top:.5rem;">Los campos marcados con <span style="color:red">*</span> son obligatorios.</p>

          <div class="form-actions-altaEvento">
            <button type="submit" class="btn-guardar-altaEvento" 
                    <%= (request.getAttribute("sinEventos") != null && (Boolean)request.getAttribute("sinEventos")) ? "disabled" : "" %>>
              <i class='bx bx-save'></i> Guardar
            </button>
            <button type="button" class="btn-cancelar-altaEvento" onclick="window.location='<%=ctx%>/inicio'">
              <i class='bx bx-x-circle'></i> Cancelar
            </button>
          </div>
        </form>

        <script>
          (function () {
            const form = document.getElementById('form-alta-edicion');
            const inputImg = form.querySelector('input[type="file"]');
            const MAX_BYTES = 2 * 1024 * 1024;
            if (inputImg) {
              inputImg.addEventListener('change', function () {
                const file = this.files && this.files[0];
                if (file && file.size > MAX_BYTES) {
                  alert('La imagen supera 2 MB. Elegí un archivo más liviano.');
                  this.value = '';
                }
              });
            }

            const videoInput = form.querySelector('input[name="videoUrl"]');
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