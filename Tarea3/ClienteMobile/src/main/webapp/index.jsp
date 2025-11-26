<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="publicadores.PublicadorEventoService, publicadores.DtEvento, publicadores.DtEventoArray, java.util.ArrayList, java.util.List, java.util.Map" %>
<%
  String ctx = request.getContextPath();
  String rol = (String) session.getAttribute("rol");
  String nick = (String) session.getAttribute("nick");
  boolean precargado = Boolean.TRUE.equals(application.getAttribute("datosPrecargados"));

  // Use the generated webservice client to fetch events instead of calling logic directly
  List<DtEvento> eventos = new ArrayList<>();
  try {
      PublicadorEventoService svc = new PublicadorEventoService();
      publicadores.PublicadorEvento port = svc.getPublicadorEventoPort();
      DtEventoArray arr = port.listarEventos();
      if (arr != null && arr.getItem() != null) {
          eventos.addAll(arr.getItem());
      }
  } catch (Exception ex) {
      // If the webservice call fails, log and fallback to any request attribute that might exist
      ex.printStackTrace();
      Object reqEvents = request.getAttribute("eventos");
      if (reqEvents instanceof java.util.List) {
          for (Object o : (java.util.List) reqEvents) {
              if (o instanceof DtEvento) {
                  eventos.add((DtEvento) o);
              }
          }
      }
  }

  Map<String,String> imgUrls = (java.util.Map<String,String>) request.getAttribute("imgUrls");
  //quisiera hacer responsive el index.jsp siguiendo los lineamientos de rwd
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Eventos.uy</title>
  <link rel="stylesheet" href="<%=ctx%>/css/style.css">
  <link rel="stylesheet" href="<%=ctx%>/css/layoutMenu.css">
  <link rel="stylesheet" href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css">
</head>
<body>
  <jsp:include page="/WEB-INF/templates/header.jsp" />
  <div class="container row layout-inicio">
    <main class="main-inicio">
      <h1>Bienvenido!</h1>
    </main>
  </div>
</body>
</html>