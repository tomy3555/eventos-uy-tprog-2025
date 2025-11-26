<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // Invalida la sesión actual
    session.invalidate();

    request.removeAttribute("error");

    // Redirigir al inicio o al login
    response.sendRedirect(request.getContextPath() + "/inicio");
%>
