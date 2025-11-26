<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx  = request.getContextPath();
  String duplicado = (String) request.getAttribute("nombreInstitucionDuplicado");
  String error = (String) request.getAttribute("error");
  boolean hayError = error != null;
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Alta de Institución — Eventos.uy</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <!-- CSS base -->
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
        <h2>Alta de Institución</h2>
        <p style="margin-bottom:1rem;">Completá los datos de la institución. El nombre debe ser único.</p>

        <%-- Mensajes de error --%>
        <div class="alert <%= hayError ? "alert-error" : "hidden" %>" role="alert" aria-live="assertive" style="margin-bottom:1rem;">
          <i class='bx bxs-error-circle'></i>
          <span>
            <%= "duplicado".equals(error)
                ? ("Ya existe una institución con el nombre " + (duplicado != null ? duplicado : ""))
                : (hayError ? error : "") %>
          </span>
        </div>

        <!-- Formulario principal -->
        <form id="form-alta-institucion" method="post" action="<%=ctx%>/institucion/alta" enctype="multipart/form-data" novalidate>

          <div class="form-group-altaTipoRegistro">
            <label>Nombre de la institución <span style="color:red">*</span></label>
            <input type="text" name="nombre" required>
            <small class="helper">Debe ser único y descriptivo.</small>
          </div>

          <div class="form-group-altaTipoRegistro">
            <label>Sitio web <span style="color:red">*</span></label>
            <input type="url" name="web" required placeholder="https://ejemplo.com">
            <small class="helper">Debe ser una URL válida.</small>
          </div>

          <div class="form-group-altaTipoRegistro">
            <label>Descripción <span style="color:red">*</span></label>
            <textarea name="desc" rows="4" required></textarea>
          </div>

          <div class="form-group-altaTipoRegistro">
            <label>Logo de la institución (opcional)</label>
            <input type="file" name="imagen" accept="image/*">
            <small class="helper-note">Formatos sugeridos: JPG/PNG. Tamaño máx. 2 MB.</small>
          </div>

          <p style="font-size:0.9rem; margin-top:.5rem;">Los campos marcados con <span style="color:red">*</span> son obligatorios.</p>

          <div class="form-actions-altaEvento">
            <button type="submit" class="btn-guardar-altaEvento">
              <i class='bx bx-save'></i> Guardar
            </button>
            <button type="submit" class="btn-cancelar-altaEvento" name="accion" value="cancelar">
              <i class='bx bx-x-circle'></i> Cancelar
            </button>
          </div>
        </form>

        <script>
          (function () {
            const form = document.getElementById('form-alta-institucion');
            const cancelarBtn = document.querySelector('.btn-cancelar-altaEvento');
            if (cancelarBtn) {
              cancelarBtn.addEventListener('click', function() {
                Array.from(form.querySelectorAll('[required]')).forEach(function(input) {
                  input.removeAttribute('required');
                });
              });
            }

            // Verificación básica de tamaño de imagen
            const inputImg = form.querySelector('input[type="file"]');
            const MAX_BYTES = 2 * 1024 * 1024; // 2MB
            if (inputImg) {
              inputImg.addEventListener('change', function () {
                const file = this.files && this.files[0];
                if (file && file.size > MAX_BYTES) {
                  alert('La imagen supera 2 MB. Elegí un archivo más liviano.');
                  this.value = '';
                }
              });
            }
          })();
        </script>
      </section>
    </main>
  </div>

</body>
</html>
