package ar.edu.usal.publicidad.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

// Logout. Este servlet NO lleva @WebServlet: lo registro en el web.xml (la consigna pide
// usar las dos formas, anotaciones y web.xml). Cierra la sesion y borra las cookies.
// Es POST porque cambia el estado (cierra la sesion).
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1) Limpio la Session
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        // 2) Limpio las cookies: pongo a cada una vida 0 para que el navegador la borre
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                c.setValue("");
                c.setMaxAge(0);
                c.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
                resp.addCookie(c);
            }
        }
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
