package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/buscar")
public class BuscarServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String q = req.getParameter("q");
        String ctx = req.getContextPath();

        if (q == null || q.trim().isEmpty()) {
            // If empty, go to listing of events
            resp.sendRedirect(ctx + "/evento/listado");
            return;
        }

        // Simple behavior: treat the query as a category filter (best-effort)
        // Redirect to evento/listado with 'categoria' = q so the existing listing can handle it.
        resp.sendRedirect(ctx + "/evento/listado?categoria=" + java.net.URLEncoder.encode(q.trim(), java.nio.charset.StandardCharsets.UTF_8));
    }
}
