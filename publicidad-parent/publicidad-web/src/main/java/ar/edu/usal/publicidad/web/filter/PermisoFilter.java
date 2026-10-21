package ar.edu.usal.publicidad.web.filter;

import ar.edu.usal.publicidad.domain.OpcionMenu;
import ar.edu.usal.publicidad.domain.Usuario;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

// Filtro: se ejecuta antes que cualquier servlet y decide si el usuario puede entrar.
// 1) Sin sesion solo se puede ver el login (y los css/js).
// 2) Con sesion, solo puede entrar a las paginas que su perfil tiene en la base
//    (tabla perfil_opcion). Asi no comparo nombres de perfil en el codigo.
@WebFilter("/*")
public class PermisoFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        req.setCharacterEncoding("UTF-8");   // para que las tildes y la ñ lleguen bien

        String ruta = req.getServletPath();   // por ejemplo /campanias

        // Estas rutas son publicas
        boolean esPublica = ruta.isEmpty() || ruta.equals("/") || ruta.equals("/login")
                || ruta.startsWith("/css/") || ruta.startsWith("/js/");
        if (esPublica) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        Usuario usuario = null;
        if (session != null) {
            usuario = (Usuario) session.getAttribute("usuario");
        }
        if (usuario == null) {
            resp.sendRedirect(req.getContextPath() + "/login");   // sin sesion: al login
            return;
        }
        if (ruta.equals("/logout")) {
            chain.doFilter(request, response);   // cualquier usuario logueado puede salir
            return;
        }
        // Reviso si la ruta esta entre las opciones de su perfil
        for (OpcionMenu opcion : usuario.getOpciones()) {
            if (opcion.getUrl().equals(ruta)) {
                chain.doFilter(request, response);
                return;
            }
        }
        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "No tiene permiso para entrar a esta pagina");
    }
}
