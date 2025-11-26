<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%! 
  // 🔹 Función auxiliar para obtener valores guardados en el request
  String val(jakarta.servlet.http.HttpServletRequest r, String name) {
    Object v = r.getAttribute(name);
    return v == null ? "" : v.toString();
  }
%>
<%
  String ctx   = request.getContextPath();
  String error = (String) request.getAttribute("error");
  boolean esPost = "POST".equalsIgnoreCase(request.getMethod());
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Crear cuenta — Eventos.uy</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="<%=ctx%>/css/style.css">
  <link rel="stylesheet" href="<%=ctx%>/css/altaUsuario.css">
  <link rel="stylesheet" href="<%=ctx%>/css/layoutMenu.css">
  <link rel="stylesheet" href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css">
</head>
<body>

<jsp:include page="/WEB-INF/templates/header.jsp" />
<div class="container row" style="display:flex; align-items:flex-start;">
  <jsp:include page="/WEB-INF/templates/menu.jsp" />
  
  <main class="form-wrapper-alta-usuario" style="flex:2; min-width:0;">
    <h1 class="titulo-alta-usuario">Crear cuenta</h1>

    <% if (esPost && error != null && !error.isEmpty()) { %>
      <div class="alerta-error" style="color: red; font-weight: bold; margin-bottom: 1rem;">
        <%= error %>
      </div>
    <% } %>

    
    <form id="altaForm" class="form-alta-usuario" method="post" 
          action="<%=ctx%>/usuario/AltaUsuario" 
          enctype="multipart/form-data" 
          novalidate>

      <!-- Tipo de cuenta -->
      <fieldset class="fieldset-rol">
        <legend>Tipo de cuenta</legend>
        <%
          String rolSel = val(request, "rol");
          if (rolSel.isEmpty()) rolSel = "asistente"; 
        %>
        <label class="rol-opcion">
          <input type="radio" name="rol" value="asistente" 
            <%= "asistente".equalsIgnoreCase(rolSel) ? "checked" : "" %>> Asistente
        </label>
        <label class="rol-opcion">
          <input type="radio" name="rol" value="organizador" 
            <%= "organizador".equalsIgnoreCase(rolSel) ? "checked" : "" %>> Organizador
        </label>
      </fieldset>

      <!-- Datos comunes -->
      <div class="fila-doble">
        <div class="input-group">
          <label for="nick">Nick*</label>
          <input id="nick" name="nick" value="<%= val(request, "nick") %>" required placeholder="Tu alias">
          <small id="nick-msg" class="hint"></small>
        </div>
        <div class="input-group">
          <label for="email">Correo*</label>
          <input id="email" name="email" type="email" value="<%= val(request, "email") %>" required placeholder="tucorreo@ejemplo.com">
          <small id="email-msg" class="hint"></small>
        </div>
      </div>

      <div class="fila-doble">
        <div class="input-group">
          <label for="pass">Contraseña*</label>
          <input id="pass" name="pass" type="password" required minlength="4" value="<%= val(request, "pass") %>">
        </div>
        <div class="input-group">
          <label for="pass2">Repetir contraseña*</label>
          <input id="pass2" name="pass2" type="password" required minlength="4" value="<%= val(request, "pass2") %>">
        </div>
      </div>

      <!-- Imagen -->
      <div class="input-group">
        <label for="imagen">Foto de perfil (opcional)</label>
        <input id="imagen" name="imagen" type="file" accept="image/*">
        <img id="preview" style="max-width: 150px; margin-top: 10px; display:none; border-radius: 6px; border: 1px solid var(--line);">
      </div>

      <!-- Asistente -->
      <section id="grupoAsistente" data-role="asistente">
        <h3>Datos de asistente</h3>
        <div class="fila-doble">
          <div class="input-group">
            <label for="nombreA">Nombre*</label>
            <input id="nombreA" name="nombreA" value="<%= val(request, "nombreA") %>">
          </div>
          <div class="input-group">
            <label for="apellidoA">Apellido*</label>
            <input id="apellidoA" name="apellidoA" value="<%= val(request, "apellidoA") %>">
          </div>
        </div>
        <div class="fila-doble">
          <div class="input-group">
            <label for="nacA">Fecha de nacimiento*</label>
            <input id="nacA" name="nacA" type="date" value="<%= val(request, "nacA") %>">
          </div>
          <div class="input-group">
            <label for="instIdA">Institución (opcional)</label>
            <select id="instIdA" name="instIdA">
              <option value="">-- Seleccione una institución --</option>
              <% 
                Collection<String> instituciones = (Collection<String>) request.getAttribute("instituciones");
                String instSel = val(request, "instIdA");
                if (instituciones != null) {
                  for (String inst : instituciones) { 
              %>
                <option value="<%= inst %>" <%= inst.equals(instSel) ? "selected" : "" %>><%= inst %></option>
              <% 
                  }
                } 
              %>
            </select>
          </div>
        </div>
      </section>

      <!-- Organizador -->
      <section id="grupoOrganizador" data-role="organizador" style="display:none;">
        <h3>Datos de organizador</h3>
        <div class="fila-doble">
          <div class="input-group">
            <label for="nombreO">Nombre de la organización*</label>
            <input id="nombreO" name="nombreO" value="<%= val(request, "nombreO") %>">
          </div>
          <div class="input-group">
            <label for="webO">Sitio web (opcional)</label>
            <input id="webO" name="webO" type="url" value="<%= val(request, "link") %>">
          </div>
        </div>
        <div class="input-group">
          <label for="descO">Descripción*</label>
          <textarea id="descO" name="descO" rows="3"><%= val(request, "descripcion") %></textarea>
        </div>
      </section>

     
      <div class="acciones-form">
        <button type="submit" class="btn">Crear cuenta</button>
        <button type="submit" class="btn ghost" name="accion" value="cancelar">Cancelar</button>
      </div>
    </form>
  </main>
</div>

<script>
(function(){
  const $ = s => document.querySelector(s);
  const form = $('#altaForm');
  if (!form) return;

  const grupoA = $('#grupoAsistente');
  const grupoO = $('#grupoOrganizador');
  const rolRadios = form.querySelectorAll('input[name="rol"]');

  function setDisabled(groupEl, disabled){
    groupEl.querySelectorAll('input,select,textarea').forEach(el => el.disabled = disabled);
  }
  function setRequired(el, req){ if (el) el.required = req; }

  function applyRoleUI(role){
    const isAsis = role === 'asistente';
    grupoA.style.display = isAsis ? '' : 'none';
    grupoO.style.display = isAsis ? 'none' : '';
    setDisabled(grupoA, !isAsis);
    setDisabled(grupoO,  isAsis);
    setRequired($('#nombreA'), isAsis);
    setRequired($('#apellidoA'), isAsis);
    setRequired($('#nacA'), isAsis);
    setRequired($('#nombreO'), !isAsis);
    setRequired($('#descO'),   !isAsis);
  }

  const rolActual = form.querySelector('input[name="rol"]:checked')?.value || 'asistente';
  applyRoleUI(rolActual);

  rolRadios.forEach(r => r.addEventListener('change', e => applyRoleUI(e.target.value)));

  // vista previa de imagen
  const inputImg = $('#imagen');
  const preview = $('#preview');
  inputImg?.addEventListener('change', e => {
    const file = e.target.files[0];
    if (file) {
      preview.src = URL.createObjectURL(file);
      preview.style.display = 'block';
    } else {
      preview.style.display = 'none';
      preview.src = '';
    }
  });
})();
</script>

<script>
(function () {
  var ctx = "<%=ctx%>";

  var form     = document.getElementById('altaForm');
  if (!form) return;

  var nick     = document.getElementById('nick');
  var email    = document.getElementById('email');
  var nickMsg  = document.getElementById('nick-msg');
  var emailMsg = document.getElementById('email-msg');

  // --- helpers UI (sin tocar el botón submit) ---
  function setChecking(input, msgEl) {
    if (input) { input.classList.remove('ok','error'); }
    if (msgEl) { msgEl.textContent = 'Verificando...'; msgEl.className = 'hint checking'; }
  }
  function setResult(input, msgEl, available) {
    if (input) {
      input.classList.remove('ok','error');
      input.classList.add(available ? 'ok' : 'error');
    }
    if (msgEl) {
      msgEl.textContent = available ? 'Disponible' : 'No disponible';
      msgEl.className = 'hint ' + (available ? 'ok' : 'error');
    }
  }
  function clearField(input, msgEl) {
    if (input) input.classList.remove('ok','error');
    if (msgEl) { msgEl.textContent = ''; msgEl.className = 'hint'; }
  }
  function parseOK(txt) { return /^OK$/i.test((txt || '').trim()); }

  // --- fetch texto plano OK/NO (sin await) ---
  function fetchPlain(url, onOk, onErr){
    var full = url + (url.indexOf('?') >= 0 ? '&' : '?') + '_=' + Date.now(); // cache-bust
    fetch(full, { headers:{'Accept':'text/plain'}, credentials:'same-origin', cache:'no-store' })
      .then(function(res){ return res.text().then(function(txt){ return { status: res.status, ok: res.ok, txt: txt }; }); })
      .then(function(r){
        console.debug('[VALIDAR]', full, '→', r.status, JSON.stringify(r.txt));
        // aunque !ok, igual decidimos por el texto
        if (typeof onOk === 'function') onOk(r.txt);
      })
      .catch(function(err){
        console.error('[VALIDAR error]', err);
        if (typeof onErr === 'function') onErr(err);
      });
  }

  // --- validadores con debounce propio ---
  var tNick, tEmail;

  function validateNickAjax(){
    if (!nick) return;
    var v = (nick.value || '').trim();
    if (!v) { clearField(nick, nickMsg); return; }
    setChecking(nick, nickMsg);
    fetchPlain(ctx + '/usuario/validar?nick=' + encodeURIComponent(v),
      function(txt){
        // si vino HTML por un filtro, lo marcamos como error visible
        if ((txt || '').trim().charAt(0) === '<') {
          nick.classList.add('error');
          if (nickMsg) { nickMsg.textContent = 'No se pudo validar'; nickMsg.className = 'hint error'; }
          return;
        }
        setResult(nick, nickMsg, parseOK(txt));
      },
      function(){
        nick.classList.add('error');
        if (nickMsg) { nickMsg.textContent = 'Error validando'; nickMsg.className = 'hint error'; }
      }
    );
  }

  function validateEmailAjax(){
    if (!email) return;
    var v = (email.value || '').trim();
    if (!v) { clearField(email, emailMsg); return; }
    setChecking(email, emailMsg);
    fetchPlain(ctx + '/usuario/validar?email=' + encodeURIComponent(v),
      function(txt){
        if ((txt || '').trim().charAt(0) === '<') {
          email.classList.add('error');
          if (emailMsg) { emailMsg.textContent = 'No se pudo validar'; emailMsg.className = 'hint error'; }
          return;
        }
        setResult(email, emailMsg, parseOK(txt));
      },
      function(){
        email.classList.add('error');
        if (emailMsg) { emailMsg.textContent = 'Error validando'; emailMsg.className = 'hint error'; }
      }
    );
  }

  function debounce(fn, ms, tokenRefName){
    return function(){
      var args = arguments, self = this;
      clearTimeout(tokenRefName === 'nick' ? tNick : tEmail);
      var t = setTimeout(function(){ fn.apply(self, args); }, ms || 350);
      if (tokenRefName === 'nick') tNick = t; else tEmail = t;
    };
  }

  // eventos (no bloqueamos el submit)
  if (nick){
    nick.addEventListener('input', debounce(validateNickAjax, 350, 'nick'));
    nick.addEventListener('blur',  validateNickAjax);
  }
  if (email){
    email.addEventListener('input', debounce(validateEmailAjax, 350, 'email'));
    email.addEventListener('blur',  validateEmailAjax);
  }

  // si hay valores precargados (vuelta de POST), validar de entrada
  if (nick && nick.value.trim())  validateNickAjax();
  if (email && email.value.trim()) validateEmailAjax();
})();
</script>





</body>
</html>