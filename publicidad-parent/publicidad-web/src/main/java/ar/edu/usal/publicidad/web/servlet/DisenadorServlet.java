package ar.edu.usal.publicidad.web.servlet;

import ar.edu.usal.publicidad.domain.Cuenta;
import ar.edu.usal.publicidad.domain.Disenador;
import ar.edu.usal.publicidad.domain.Especializacion;
import ar.edu.usal.publicidad.domain.Plataforma;
import ar.edu.usal.publicidad.exception.DAOException;
import ar.edu.usal.publicidad.exception.ValidacionException;
import ar.edu.usal.publicidad.service.CuentaService;
import ar.edu.usal.publicidad.service.DisenadorService;
import ar.edu.usal.publicidad.service.PlataformaService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// ABM de diseñadores.
//   GET  -> mostrar la pagina (listado y formulario): consultar no cambia nada
//   POST -> todo lo que cambia datos. El parametro "accion" dice que hacer:
//           crear, modificar o eliminar.
// El servlet NO tiene logica: recibe los datos, llama al service y devuelve la respuesta.
@WebServlet("/disenadores")
public class DisenadorServlet extends HttpServlet {

    private DisenadorService disenadorService = new DisenadorService();
    private CuentaService cuentaService = new CuentaService();
    private PlataformaService plataformaService = new PlataformaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("disenadores", disenadorService.listar());
            req.setAttribute("especializaciones", disenadorService.listarEspecializaciones());
            req.setAttribute("todasCuentas", cuentaService.listar());
            req.setAttribute("todasPlataformas", plataformaService.listar());

            // Si viene ?legajo=... es para EDITAR: cargo ese diseñador en el formulario
            String legajo = req.getParameter("legajo");
            if (legajo != null) {
                Disenador disenador = disenadorService.buscar(Integer.parseInt(legajo));
                if (disenador != null) {
                    req.setAttribute("disenador", disenador);
                    // Guardo los nombres seleccionados para marcar los checkbox en el JSP
                    Set<String> cuentasElegidas = new HashSet<>();
                    for (Cuenta c : disenador.getCuentas()) {
                        cuentasElegidas.add(c.getNombre());
                    }
                    Set<String> plataformasElegidas = new HashSet<>();
                    for (Plataforma p : disenador.getPlataformas()) {
                        plataformasElegidas.add(p.getNombre());
                    }
                    req.setAttribute("cuentasElegidas", cuentasElegidas);
                    req.setAttribute("plataformasElegidas", plataformasElegidas);
                }
            }
            req.getRequestDispatcher("/WEB-INF/jsp/disenadores.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El legajo debe ser un numero");
        } catch (DAOException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        String accion = req.getParameter("accion");
        try {
            String mensaje;
            if ("crear".equals(accion)) {
                if (!disenadorService.agregar(armarDisenador(req))) {
                    throw new ValidacionException("No se pudo guardar el diseñador");
                }
                mensaje = "Diseñador guardado correctamente";
            } else if ("modificar".equals(accion)) {
                if (!disenadorService.modificar(armarDisenador(req))) {
                    throw new ValidacionException("No se pudo modificar el diseñador");
                }
                mensaje = "Diseñador modificado correctamente";
            } else if ("eliminar".equals(accion)) {
                int legajo = numeroEntero(req.getParameter("legajo"), "El legajo");
                if (!disenadorService.eliminar(legajo)) {
                    throw new ValidacionException("No se pudo eliminar el diseñador");
                }
                mensaje = "Diseñador eliminado correctamente";
            } else {
                throw new ValidacionException("La accion no es valida");
            }
            // La respuesta empieza con "OK:" si salio bien o con "ERROR:" si no; el JavaScript lo lee
            resp.getWriter().write("OK:" + mensaje);
        } catch (ValidacionException e) {
            resp.getWriter().write("ERROR:" + e.getMessage());
        } catch (DAOException e) {
            resp.getWriter().write("ERROR:Error en la base de datos: " + e.getMessage());
        }
    }

    // Arma un Disenador con los datos que llegaron del formulario
    private Disenador armarDisenador(HttpServletRequest req) throws ValidacionException {
        Disenador disenador = new Disenador();
        disenador.setLegajo(numeroEntero(req.getParameter("legajo"), "El legajo"));
        disenador.setNombre(req.getParameter("nombre"));
        disenador.setApellido(req.getParameter("apellido"));
        disenador.setDni(req.getParameter("dni"));
        disenador.setFechaNacimiento(fecha(req.getParameter("fechaNacimiento")));
        disenador.setEspecializacion(new Especializacion(req.getParameter("especializacion")));
        disenador.setCelular(req.getParameter("celular"));

        // Los checkbox marcados llegan como varios valores con el mismo nombre
        List<Cuenta> cuentas = new ArrayList<>();
        String[] nombresCuentas = req.getParameterValues("cuentas");
        if (nombresCuentas != null) {
            for (String nombre : nombresCuentas) {
                cuentas.add(new Cuenta(nombre));
            }
        }
        disenador.setCuentas(cuentas);

        List<Plataforma> plataformas = new ArrayList<>();
        String[] nombresPlataformas = req.getParameterValues("plataformas");
        if (nombresPlataformas != null) {
            for (String nombre : nombresPlataformas) {
                Plataforma plataforma = new Plataforma();
                plataforma.setNombre(nombre);
                plataformas.add(plataforma);
            }
        }
        disenador.setPlataformas(plataformas);
        return disenador;
    }

    // Convierte un texto a numero entero. Si no se puede, avisa con un mensaje claro
    private int numeroEntero(String texto, String campo) throws ValidacionException {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new ValidacionException(campo + " debe ser un numero");
        }
    }

    // Convierte un texto con formato aaaa-mm-dd (el que manda el input type="date") a una fecha
    private Date fecha(String texto) throws ValidacionException {
        try {
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            formato.setLenient(false);
            return formato.parse(texto);
        } catch (ParseException | NullPointerException e) {
            throw new ValidacionException("La fecha de nacimiento no es valida");
        }
    }
}
