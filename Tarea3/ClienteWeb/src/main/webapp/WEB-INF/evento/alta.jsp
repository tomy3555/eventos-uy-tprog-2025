<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="publicadores.*, java.util.*" %>
<%
  String ctx = request.getContextPath();

  // --- Preparar categorías ---
  Set<String> categoriasSet = new HashSet<>();

  @SuppressWarnings("unchecked")
  List<Object> dtCatsRaw = (List<Object>) request.getAttribute("dtCategorias");

  if (dtCatsRaw != null && !dtCatsRaw.isEmpty()) {
    // Caso 1: las categorías vienen del request (desde el publicador)
    for (Object o : dtCatsRaw) {
      try {
        java.lang.reflect.Method m1 = o.getClass().getMethod("getCategorias");
        Object catObj = m1.invoke(o);
        if (catObj != null) {
          java.lang.reflect.Method m2 = catObj.getClass().getMethod("getCategoria");
          Object list = m2.invoke(catObj);
          if (list instanceof Collection<?>) {
            for (Object s : (Collection<?>) list)
              if (s != null && !s.toString().isBlank())
                categoriasSet.add(s.toString());
          }
        }
      } catch (Exception ignore) {}
    }
  }

  if (categoriasSet.isEmpty()) {
	  try {
	    publicadores.PublicadorEventoService svc = new publicadores.PublicadorEventoService();
	    publicadores.PublicadorEvento port = svc.getPublicadorEventoPort();
	    publicadores.DtEventoArray arr = port.listarEventos();
	    if (arr != null && arr.getItem() != null) {
	      for (publicadores.DtEvento ev : arr.getItem()) {
	        if (ev == null || ev.getCategorias() == null) continue;
	        java.util.List<String> catsEv = ev.getCategorias().getCategoria();
	        if (catsEv != null)
	          for (String s : catsEv)
	            if (s != null && !s.isBlank())
	              categoriasSet.add(s);
	      }
	    }
	  } catch (Exception e) {
	    e.printStackTrace();
	  }
	}
  List<String> categorias = new ArrayList<>(categoriasSet);
  categorias.sort(String::compareToIgnoreCase);
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Alta de Evento — Eventos.uy</title>
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
        <h2>Alta de Evento</h2>
        <p>Completá los datos básicos del evento y asociá al menos una categoría.</p>

        <% 
          String duplicado = (String) request.getAttribute("nombreEventoDuplicado");
          boolean hayError = request.getAttribute("error") != null;
        %>
        <div class="alert <%= hayError ? "alert-error" : "hidden" %>" role="alert" aria-live="assertive" style="margin-bottom:1rem;">
          <i class='bx bxs-error-circle'></i>
          <span><%= hayError ? ("Ya existe un evento con el nombre " + (duplicado!=null?duplicado:"")) : "" %></span>
        </div>

        <p id="error-categorias" class="helper-error hidden" aria-live="polite"></p>

        <form id="form-alta-evento" method="post" action="<%=ctx%>/evento/alta" enctype="multipart/form-data" novalidate>

          <div class="form-group-altaTipoRegistro">
            <label>Nombre del evento <span style="color:red">*</span></label>
            <input type="text" name="nombre" required>
            <small class="helper">Debe ser único y descriptivo.</small>
          </div>

          <div class="form-group-altaTipoRegistro">
            <label>Sigla <span style="color:red">*</span></label>
            <input type="text" name="sigla" maxlength="10" required>
            <small class="helper">2–10 caracteres (por ejemplo, abreviatura oficial).</small>
          </div>

          <div class="form-group-altaTipoRegistro">
            <label>Descripción <span style="color:red">*</span></label>
            <textarea name="desc" rows="4" required></textarea>
          </div>

          <div class="form-group-altaTipoRegistro">
            <label>Imagen del evento (opcional)</label>
            <input class=".btn" type="file" name="imagen" accept="image/*">
            <small class="helper-note">Formatos sugeridos: JPG/PNG. Tamaño máx. 2 MB.</small>
          </div>

          <!-- Categorías -->
          <fieldset class="form-group-altaTipoRegistro" id="fs-categorias">
            <legend>Categorías <span style="color:red">*</span></legend>
            <div class="checkbox-grid-ev">
              <% if (!categorias.isEmpty()) {
                   for (String c : categorias) { %>
                <label class="check-item">
                  <input type="checkbox" class="cat" value="<%= c %>">
                  <span><%= c %></span>
                </label>
              <% } } else { %>
                <label><span>(No hay categorías disponibles)</span></label>
              <% } %>
            </div>
            <input type="hidden" id="categorias" name="categorias" value="">
            <small class="helper">Podés elegir más de una categoría.</small>
          </fieldset>

          <p style="font-size:0.9rem;">Los campos marcados con <span style="color:red">*</span> son obligatorios.</p>

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
            const form = document.getElementById('form-alta-evento');
            const cancelarBtn = document.querySelector('.btn-cancelar-altaEvento');
            cancelarBtn.addEventListener('click', () => {
              Array.from(form.querySelectorAll('[required]')).forEach(i => i.removeAttribute('required'));
            });

            form.addEventListener('submit', function (e) {
              if (e.submitter && e.submitter.name === 'accion' && e.submitter.value === 'cancelar') return;
              const checks = Array.from(document.querySelectorAll('.cat'));
              const sel = checks.filter(c => c.checked).map(c => c.value);
              if (sel.length === 0) {
                e.preventDefault();
                document.getElementById('fs-categorias').scrollIntoView({ behavior: 'smooth', block: 'center' });
                const errorCat = document.getElementById('error-categorias');
                errorCat.textContent = 'Debe marcar al menos una categoría.';
                errorCat.classList.remove('hidden');
                return;
              }
              document.getElementById('error-categorias').classList.add('hidden');
              document.getElementById('categorias').value = sel.join(',');
            });

            const inputImg = document.querySelector('input[type="file"][name="imagen"]');
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
          })();
        </script>
      </section>
    </main>
  </div>
</body>
</html>
