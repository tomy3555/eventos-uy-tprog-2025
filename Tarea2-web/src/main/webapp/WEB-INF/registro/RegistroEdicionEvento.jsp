<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*,logica.datatypes.*" %>
<%
  String ctx = request.getContextPath();
  List<DTEvento> eventos = (List<DTEvento>) request.getAttribute("eventos");
  Map<String, List<DTEdicion>> mapa = (Map<String, List<DTEdicion>>) request.getAttribute("edicionesPorEvento");
  DTEdicion edSel = (DTEdicion) request.getAttribute("edicionSeleccionada");
  List<DTTipoRegistro> tipos = (List<DTTipoRegistro>) request.getAttribute("tiposRegistro");
  Map<String, Integer> cuposDisponibles = (Map<String, Integer>) request.getAttribute("cuposDisponibles");

  String eventoSel = request.getParameter("evento");
  String edicionSel = request.getParameter("edicion");
  Boolean yaRegistrado = (Boolean) request.getAttribute("yaRegistrado");
  if (yaRegistrado == null) yaRegistrado = false;

  boolean cerrada = false;
  if (edSel != null && edSel.getFechaFin() != null) {
    cerrada = java.time.LocalDate.now().isAfter(edSel.getFechaFin());
  }
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"/>
  <title>Registrarse a una Edición de Evento</title>
  <link rel="stylesheet" href="<%=ctx%>/css/style.css"/>
  <link rel="stylesheet" href="<%=ctx%>/css/RegistroEdicionEvento.css"/>
</head>
<body>

<jsp:include page="/WEB-INF/templates/header.jsp" />

<div class="container row" style="margin-top:1rem; display:flex; align-items:flex-start;">
  <jsp:include page="/WEB-INF/templates/menu.jsp" />

  <main class="container" style="flex:2; min-width:0;">

    <% if (request.getAttribute("error") != null) { %>
      <p style="color:#c00; font-weight:600"><%= request.getAttribute("error") %></p>
    <% } %>

    <section class="form-card-registroEdicionEvento">
      <h2>Registrarse a una Edición de Evento</h2>

      <form id="formSeleccion" action="<%=ctx%>/registro/inscripcion" method="get">
        <div class="form-group-registroEdicionEvento">
          <label for="selectEvento">Evento *</label>
          <select id="selectEvento" name="evento" required>
            <option value="">-- Seleccione un evento --</option>
            <% if (eventos != null) {
                 for (DTEvento ev : eventos) {
                   String sel = ev.getNombre().equals(eventoSel) ? "selected" : "";
            %>
              <option value="<%=ev.getNombre()%>" <%=sel%>><%=ev.getNombre()%></option>
            <% } } %>
          </select>
        </div>

        <div class="form-group-registroEdicionEvento">
          <label for="selectEdicion">Edición *</label>
          <select id="selectEdicion" name="edicion" required <%= (eventoSel==null||eventoSel.isBlank())?"disabled":"" %>>
            <option value="">-- Seleccione primero un evento --</option>
          </select>
        </div>
      </form>
    </section>

    <% if (edSel != null) { %>
      <section class="event-card" style="margin-top:1rem">
        <h3>Datos de la edición</h3>
        <ul>
          <li><strong>Evento:</strong> <%= eventoSel %></li>
          <li><strong>Edición:</strong> <%= edSel.getNombre() %></li>
          <li><strong>Sigla:</strong> <%= edSel.getSigla() %></li>
          <li><strong>Fecha inicio:</strong> <%= edSel.getFechaInicio() %></li>
          <li><strong>Fecha fin:</strong> <%= edSel.getFechaFin() %></li>
          <li><strong>Ciudad:</strong> <%= edSel.getCiudad() %></li>
          <li><strong>País:</strong> <%= edSel.getPais() %></li>
        </ul>

        <% if (cerrada) { %>
          <div style="color:#c00; font-weight:600; margin:1rem 0;">
            Esta edición finalizó el <%= edSel.getFechaFin() %>. No admite nuevas inscripciones.
          </div>
        <% } %>

        <% if (yaRegistrado) { %>
          <div style="color:#c00; font-weight:600; margin:1rem 0;">
            Ya estás registrado a esta edición. No puedes inscribirte nuevamente.
          </div>
        <% } %>

        <h3>Tipos de registro</h3>
        <% if (tipos == null || tipos.isEmpty()) { %>
          <p>No hay tipos de registro disponibles para esta edición.</p>
        <% } else if (!yaRegistrado && !cerrada) { %>
          <form action="<%=ctx%>/registro/inscripcion" method="post" id="formInscripcion">
            <input type="hidden" name="evento" value="<%= eventoSel %>"/>
            <input type="hidden" name="edicion" value="<%= edSel.getNombre() %>"/>
            <% for (DTTipoRegistro tr : tipos) { %>
              <label style="display:block; margin:.25rem 0;">
                <input type="radio" name="tipo" value="<%= tr.getNombre() %>" required/>
                <strong><%= tr.getNombre() %></strong> — <em><%= tr.getDescripcion() %></em> — $<%= tr.getCosto() %>
                <span style="color:#007700; font-weight:600;">
                  Cupos disponibles: <%= (cuposDisponibles != null && cuposDisponibles.get(tr.getNombre()) != null) ? cuposDisponibles.get(tr.getNombre()) : 0 %>
                </span>
              </label>
            <% } %>
            <div class="form-group-registroEdicionEvento" style="margin-top:1rem">
              <label for="codigoPatrocinio">Código de patrocinio (opcional)</label>
              <input id="codigoPatrocinio" name="codigoPatrocinio" placeholder="Ej: CORREANTEL"/>
              <small>Si es válido, el costo será $0.</small>
            </div>
            <button type="submit" class="btn-guardar-registroEdicionEvento" style="margin-top:1rem;">Confirmar inscripción</button>
          </form>
        <% } %>
      </section>
    <% } %>
  </main>
</div>

<script>
  const data = {
    <% if (mapa != null && !mapa.isEmpty()) {
         int i = 0;
         for (Map.Entry<String, List<DTEdicion>> entry : mapa.entrySet()) {
           if (i++ > 0) out.print(",");
           String ev = entry.getKey().replace("\"","\\\"");
           out.print("\""+ev+"\":[");
           List<DTEdicion> eds = entry.getValue();
           for (int j=0; j<eds.size(); j++) {
             DTEdicion ed = eds.get(j);
             out.print("{\"nombre\":\""+ed.getNombre().replace("\"","\\\"")+"\",\"sigla\":\""+ed.getSigla().replace("\"","\\\"")+"\"}");
             if (j < eds.size()-1) out.print(",");
           }
           out.print("]");
         }
       } %>
  };

  const selEvento = document.getElementById("selectEvento");
  const selEdicion = document.getElementById("selectEdicion");
  const formSel = document.getElementById("formSeleccion");

  function renderEdiciones(nombreEvento, preselect) {
    selEdicion.innerHTML = "";
    const eds = data[nombreEvento] || [];
    if (!eds.length) {
      selEdicion.innerHTML = "<option value=''>-- No hay ediciones aceptadas --</option>";
      selEdicion.disabled = true;
      return;
    }
    selEdicion.disabled = false;
    selEdicion.insertAdjacentHTML("beforeend","<option value=''>-- Seleccione una edición --</option>");
    eds.forEach(ed => {
      const opt = document.createElement("option");
      opt.value = ed.nombre;
      opt.textContent = ed.nombre;
      if (preselect && preselect === ed.nombre) opt.selected = true;
      selEdicion.appendChild(opt);
    });
  }

  selEvento.addEventListener("change", () => renderEdiciones(selEvento.value, null));
  selEdicion.addEventListener("change", () => {
    if (selEvento.value && selEdicion.value) formSel.submit();
  });

  (function init(){
    const ev = "<%= eventoSel == null ? "" : eventoSel.replace("\"","\\\"") %>";
    const ed = "<%= edicionSel == null ? "" : edicionSel.replace("\"","\\\"") %>";
    if (ev) renderEdiciones(ev, ed);
  })();
</script>

</body>
</html>
