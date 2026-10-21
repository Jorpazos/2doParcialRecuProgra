package ar.edu.usal.publicidad.web.servlet;

import ar.edu.usal.publicidad.domain.Usuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

// Es el punto de entrada: la aplicacion inicia desde el path root ("").
// No uso un index.jsp con redirect: este servlet decide a donde mandar al usuario.
@WebServlet("")
public class InicioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);   // false: no creo una sesion nueva si no hay
        Usuario usuario = null;
        if (session != null) {
            usuario = (Usuario) session.getAttribute("usuario");
        }
        if (usuario == null) {
            // Todavia no inicio sesion: lo mando al login
            resp.sendRedirect(req.getContextPath() + "/login");
        } else {
            // Ya inicio sesion: lo mando a la pagina de inicio de SU perfil (viene de la base)
            resp.sendRedirect(req.getContextPath() + usuario.getPerfil().getUrlInicio());
        }
    }
}
