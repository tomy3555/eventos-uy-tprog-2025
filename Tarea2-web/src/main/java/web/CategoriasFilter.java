package web;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import logica.fabrica;
import logica.interfaces.IControladorEvento;
import logica.datatypes.DTCategorias;

@WebFilter("/*")
public class CategoriasFilter implements Filter {
    private final IControladorEvento controladorEv = fabrica.getInstance().getIControladorEvento();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            List<DTCategorias> dtCategorias = controladorEv.listarDTCategorias();
            request.setAttribute("dtCategorias", dtCategorias);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}
    @Override
    public void destroy() {}
}
