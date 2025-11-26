<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx  = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Crear Institución — Eventos.uy</title>
  <link rel="stylesheet" href="<%=ctx%>/css/style.css">
  <link rel="stylesheet" href="<%=ctx%>/css/AltaEvento.css">
  <link rel="stylesheet" href="<%=ctx%>/css/layoutMenu.css">
  <link rel="stylesheet" href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css">
</head>
<body>

  <jsp:include page="/WEB-INF/templates/header.jsp" />

  <div class="container row" style="margin-top:1rem;">
    <jsp:include page="/WEB-INF/templates/menu.jsp" />

    <main class="container">
      <section class="form-card-altaEvento form-card--wide">
        <header class="form-header">
          <h1 class="form-title">Crear Institución</h1>
          <p class="form-subtitle">Completá los datos de la institución. El nombre debe ser único.</p>
        </header>

        <%-- Mensajes --%>
        <%
          String duplicado = (String) request.getAttribute("nombreInstitucionDuplicado");
          String error = (String) request.getAttribute("error");
          boolean hayError = error != null;
        %>
        <div class="alert <%= hayError ? "alert-error" : "hidden" %>" role="alert" aria-live="assertive">
          <i class='bx bxs-error-circle'></i>
          <span>
            <%= "duplicado".equals(error) ? ("Ya existe una institución con el nombre " + (duplicado!=null?duplicado:"")) : (hayError ? error : "") %>
          </span>
        </div>

        <form id="form-alta-institucion" method="post" action="<%=ctx%>/institucion/alta" enctype="multipart/form-data" novalidate>
          <div class="grid-2">
            <div class="form-group-altaEvento">
              <label for="nombre">Nombre de la institución <span class="req">*</span></label>
              <input type="text" id="nombre" name="nombre" required>
              <small class="helper">Debe ser único y descriptivo.</small>
            </div>

            <div class="form-group-altaEvento">
              <label for="web">Sitio web <span class="req">*</span></label>
              <input type="url" id="web" name="web" required placeholder="https://ejemplo.com">
              <small class="helper">Debe ser una URL válida.</small>
            </div>
          </div>

          <div class="form-group-altaEvento">
            <label for="desc">Descripción <span class="req">*</span></label>
            <textarea id="desc" name="desc" rows="4" required></textarea>
          </div>

          <div class="form-group-altaEvento">
            <label for="imagen">Logo de la institución (opcional)</label>
            <input type="file" id="imagen" name="imagen" accept="image/*">
            <small class="helper-note">Formatos sugeridos: JPG/PNG. Tamaño máx. 2&nbsp;MB.</small>
          </div>

          <p class="form-hint-altaEvento">Los campos marcados con <span class="req">*</span> son obligatorios.</p>

          <div class="form-actions-altaEvento actions">
            <button type="submit" class="btn-guardar-altaEvento">
              <i class='bx bx-save'></i> Guardar
            </button>
            <button type="submit" class="btn btn-cancelar-altaEvento" name="accion" value="cancelar">
              <i class='bx bx-x-circle'></i> Cancelar
            </button>
          </div>
        </form>
        <script>
          (function () {
            const form = document.getElementById('form-alta-institucion');
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
          })();
        </script>
      </section>
    </main>
  </div>

</body>
</html>