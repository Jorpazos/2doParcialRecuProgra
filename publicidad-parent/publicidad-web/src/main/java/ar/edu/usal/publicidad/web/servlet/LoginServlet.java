package ar.edu.usal.publicidad.web.servlet;

import ar.edu.usal.publicidad.domain.Usuario;
import ar.edu.usal.publicidad.exception.DAOException;
import ar.edu.usal.publicidad.exception.ValidacionException;
import ar.edu.usal.publicidad.service.UsuarioService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

// Login. GET muestra el formulario y POST recibe usuario y contraseña
// (los datos de un login van por POST para que no queden en la URL).
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Si ya inicio sesion lo mando al root, que lo lleva a su pagina
        if (req.getSession(false) != null && req.getSession(false).getAttribute("usuario") != null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nombreUsuario = req.getParameter("usuario");
        String password = req.getParameter("password");
        boolean recordarme = req.getParameter("recordarme") != null;   // el checkbox solo llega si esta tildado
        try {
            // El service comprueba usuario y contraseña. Si estan mal lanza ValidacionException
            Usuario usuario = usuarioService.login(nombreUsuario, password);

            req.getSession();        // me aseguro de que exista una sesion (si no hay, la creo)
            req.changeSessionId();   // cambio el id de sesion al iniciar sesion (mas seguro)
            req.getSession().setAttribute("usuario", usuario);   // guardo el usuario en la Session

            // "Recordarme": guardo SOLO el nombre de usuario en una cookie (nunca la contraseña)
            Cookie cookie = new Cookie("usuarioRecordado", URLEncoder.encode(usuario.getUsuario(), "UTF-8"));
            cookie.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
            if (recordarme) {
                cookie.setMaxAge(60 * 60 * 24 * 30);   // dura 30 dias
            } else {
                cookie.setMaxAge(0);                   // si no lo tilda, borro la cookie
            }
            resp.addCookie(cookie);

            resp.sendRedirect(req.getContextPath() + usuario.getPerfil().getUrlInicio());
        } catch (ValidacionException e) {
            // Datos incorrectos: vuelvo al login mostrando el mensaje
            req.setAttribute("error", e.getMessage());
            req.setAttribute("usuarioIngresado", nombreUsuario);
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        } catch (DAOException e) {
            req.setAttribute("error", "No se pudo conectar con la base de datos");
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        }
    }
}
