package web;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import publicadores.DtEvento;
import publicadores.DtEventoArray;

@WebFilter("/*")
public class CategoriasFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            try {
                // Crear el port EXACTAMENTE con estas dos líneas
                publicadores.PublicadorEventoService service = new publicadores.PublicadorEventoService();
                publicadores.PublicadorEvento port = service.getPublicadorEventoPort();

                // 1) Traer eventos
                List<DtEvento> eventos = new ArrayList<>();
                DtEventoArray arr = null;
                try {
                    arr = port.listarEventos();
                } catch (Exception ignore) { /* si falla, dejamos lista vacía */ }

                if (arr != null && arr.getItem() != null) {
                    eventos.addAll(arr.getItem());
                }

                // 2) Extraer categorías únicas
                Set<String> cats = new HashSet<>();
                for (DtEvento ev : eventos) {
                    if (ev == null) continue;
                    DtEvento.Categorias c = ev.getCategorias();
                    if (c == null) continue;
                    List<String> lista = c.getCategoria(); // nunca null por contrato, pero igual chequeamos
                    if (lista != null) {
                        for (String s : lista) {
                            if (s != null && !s.isBlank()) cats.add(s);
                        }
                    }
                }

                // 3) Exponer como lista simple para el menú/JSP
                request.setAttribute("dtCategorias", new ArrayList<>(cats));

            } catch (Exception e) {
                // Si algo revienta, no bloqueamos la request
                request.setAttribute("dtCategorias", java.util.List.of());
            }
        }

        chain.doFilter(request, response);
    }

    @Override public void init(FilterConfig filterConfig) throws ServletException {}
    @Override public void destroy() {}
}
