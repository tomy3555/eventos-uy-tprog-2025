<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx  = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Crear Evento — Eventos.uy</title>
  <link rel="stylesheet" href="<%=ctx%>/css/style.css">
  <link rel="stylesheet" href="<%=ctx%>/css/AltaEvento.css">
  <link rel="stylesheet" href="<%=ctx%>/css/layoutMenu.css">
  <link rel="stylesheet" href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css">
</head>
<body>

  <jsp:include page="/WEB-INF/templates/header.jsp" />

  <div class="container row" style="margin-top:1rem;">
    <jsp:include page="/WEB-INF/templates/menu.jsp" />

    <!-- Main -->
    <main class="container">
      <section class="form-card-altaEvento form-card--wide">
        <header class="form-header">
          <h1 class="form-title">Crear Evento</h1>
          <p class="form-subtitle">Completá los datos básicos del evento y asociá al menos una categoría.</p>
        </header>

        <%-- Mensajes --%>
        <%
          String duplicado = (String) request.getAttribute("nombreEventoDuplicado");
          boolean hayError = request.getAttribute("error") != null;
        %>
        <div class="alert <%= hayError ? "alert-error" : "hidden" %>" role="alert" aria-live="assertive">
          <i class='bx bxs-error-circle'></i>
          <span><%= hayError ? ("Ya existe un evento con el nombre " + (duplicado!=null?duplicado:"")) : "" %></span>
        </div>
        <p id="error-categorias" class="helper-error hidden" aria-live="polite"></p>

        <!-- para permitir subir archivo -->
        <form id="form-alta-evento" method="post" action="<%=ctx%>/evento/alta" enctype="multipart/form-data" novalidate>
          <div class="grid-2">
            <div class="form-group-altaEvento">
              <label for="nombre">Nombre del evento <span class="req">*</span></label>
              <input type="text" id="nombre" name="nombre">
              <small class="helper">Debe ser único y descriptivo.</small>
            </div>

            <div class="form-group-altaEvento">
              <label for="sigla">Sigla <span class="req">*</span></label>
              <input type="text" id="sigla" name="sigla">
              <small class="helper">2–10 caracteres (por ejemplo, abreviatura oficial).</small>
            </div>
          </div>

          <div class="form-group-altaEvento">
            <label for="desc">Descripción <span class="req">*</span></label>
            <textarea id="desc" name="desc" rows="4"></textarea>
          </div>

          <!-- Imagen -->
          <div class="form-group-altaEvento">
            <label for="imagen">Imagen del evento (opcional)</label>
            <input type="file" id="imagen" name="imagen" accept="image/*">
            <small class="helper-note">Formatos sugeridos: JPG/PNG. Tamaño máx. 2&nbsp;MB.</small>
          </div>

          <fieldset class="form-group-altaEvento" id="fs-categorias">
            <legend>Categorías <span class="req">*</span></legend>
            <div class="checkbox-grid-ev">
              <% 
                java.util.List<logica.datatypes.DTCategorias> dtCats = (java.util.List<logica.datatypes.DTCategorias>) request.getAttribute("dtCategorias");
                if (dtCats != null && !dtCats.isEmpty()) {
                  for (logica.datatypes.DTCategorias dtCat : dtCats) {
                    for (String c : dtCat.getCategorias()) {
              %>
                        <label><span><%= c %></span><input type="checkbox" class="cat" value="<%= c %>"></label>
              <%      }
                  }
                } else {
              %>
                        <label><span>(Sin categorías)</span></label>
              <%    }
              %>
            </div>
            <input type="hidden" id="categorias" name="categorias" value="">
            <small class="helper">Podés elegir más de una.</small>
          </fieldset>

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
            const form = document.getElementById('form-alta-evento');
            const cancelarBtn = document.querySelector('.btn-cancelar-altaEvento');
            cancelarBtn.addEventListener('click', function(e) {
              Array.from(form.querySelectorAll('[required]')).forEach(function(input) {
                input.removeAttribute('required');
              });
            });

            form.addEventListener('submit', function (e) {
              // detectar si el submit fue por Cancelar
              if (e.submitter && e.submitter.name === 'accion' && e.submitter.value === 'cancelar') {
                
                return;
              }
              const checks = Array.from(document.querySelectorAll('.cat'));
              const sel = checks.filter(c => c.checked).map(c => c.value);
              if (sel.length === 0) {
                e.preventDefault();
                document.getElementById('fs-categorias').scrollIntoView({behavior:'smooth', block:'center'});
                const errorCat = document.getElementById('error-categorias');
                errorCat.textContent = 'Debe marcar al menos una categoría.';
                errorCat.classList.remove('hidden');
                return;
              }
              document.getElementById('error-categorias').classList.add('hidden');
              document.getElementById('categorias').value = sel.join(',');
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