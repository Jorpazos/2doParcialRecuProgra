<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="cabecera.jsp">
    <jsp:param name="titulo" value="Diseñadores"/>
</jsp:include>

<h2 class="mb-3">Diseñadores</h2>

<div class="table-responsive mb-4">
    <table class="table table-striped table-hover bg-white text-nowrap">
        <thead>
        <tr>
            <th>Legajo</th>
            <th>Nombre</th>
            <th>Apellido</th>
            <th>DNI</th>
            <th>Nacimiento</th>
            <th>Especialización</th>
            <th>Celular</th>
            <th>Acciones</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${disenadores}" var="d">
            <tr>
                <td><c:out value="${d.legajo}"/></td>
                <td><c:out value="${d.nombre}"/></td>
                <td><c:out value="${d.apellido}"/></td>
                <td><c:out value="${d.dni}"/></td>
                <td><fmt:formatDate value="${d.fechaNacimiento}" pattern="dd/MM/yyyy"/></td>
                <td><c:out value="${d.especializacion.nombre}"/></td>
                <td><c:out value="${d.celular}"/></td>
                <td>
                    <a class="btn btn-sm btn-outline-primary"
                       href="${pageContext.request.contextPath}/disenadores?legajo=${d.legajo}">Editar</a>
                    <button type="button" class="btn btn-sm btn-outline-danger btn-ajax"
                            data-url="${pageContext.request.contextPath}/disenadores" data-accion="eliminar" data-legajo="${d.legajo}"
                            data-confirmar="¿Eliminar al diseñador <c:out value='${d.apellido}'/>?">Eliminar</button>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty disenadores}">
            <tr><td colspan="8" class="text-center text-muted">No hay diseñadores cargados</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<%-- Si el servlet mando un diseñador, el formulario es de EDICION (PUT); si no, es de ALTA (POST) --%>
<div class="card shadow-sm">
    <div class="card-header">
        <c:choose>
            <c:when test="${not empty disenador}">Editar diseñador</c:when>
            <c:otherwise>Nuevo diseñador</c:otherwise>
        </c:choose>
    </div>
    <div class="card-body">
        <form class="form-ajax row g-3"
              action="${pageContext.request.contextPath}/disenadores"
              method="post"
              data-destino="${pageContext.request.contextPath}/disenadores">
            <input type="hidden" name="accion" value="${not empty disenador ? 'modificar' : 'crear'}">

            <div class="col-md-3">
                <label class="form-label" for="legajo">Legajo</label>
                <%-- El legajo es la clave: al editar no se puede cambiar --%>
                <input type="number" class="form-control" id="legajo" name="legajo" required
                       value="<c:out value='${disenador.legajo}'/>" ${not empty disenador ? 'readonly' : ''}>
            </div>
            <div class="col-md-3">
                <label class="form-label" for="nombre">Nombre</label>
                <input type="text" class="form-control" id="nombre" name="nombre" required
                       value="<c:out value='${disenador.nombre}'/>">
            </div>
            <div class="col-md-3">
                <label class="form-label" for="apellido">Apellido</label>
                <input type="text" class="form-control" id="apellido" name="apellido" required
                       value="<c:out value='${disenador.apellido}'/>">
            </div>
            <div class="col-md-3">
                <label class="form-label" for="dni">DNI</label>
                <input type="text" class="form-control" id="dni" name="dni" required
                       value="<c:out value='${disenador.dni}'/>">
            </div>
            <div class="col-md-3">
                <label class="form-label" for="fechaNacimiento">Fecha de nacimiento</label>
                <fmt:formatDate value="${disenador.fechaNacimiento}" pattern="yyyy-MM-dd" var="fechaIso"/>
                <input type="date" class="form-control" id="fechaNacimiento" name="fechaNacimiento" required
                       value="<c:out value='${fechaIso}'/>">
            </div>
            <div class="col-md-5">
                <label class="form-label" for="especializacion">Especialización</label>
                <select class="form-select" id="especializacion" name="especializacion" required>
                    <option value="">Elegir...</option>
                    <c:forEach items="${especializaciones}" var="e">
                        <option value="<c:out value='${e.nombre}'/>"
                                ${e.nombre == disenador.especializacion.nombre ? 'selected' : ''}>
                            <c:out value="${e.nombre}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-4">
                <label class="form-label" for="celular">Celular</label>
                <input type="text" class="form-control" id="celular" name="celular" required
                       value="<c:out value='${disenador.celular}'/>">
            </div>

            <div class="col-md-6">
                <label class="form-label">Cuentas autorizadas</label>
                <div class="border rounded p-2 bg-white">
                    <c:forEach items="${todasCuentas}" var="c" varStatus="fila">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="cuentas"
                                   id="cuenta-${fila.index}" value="<c:out value='${c.nombre}'/>"
                                   ${cuentasElegidas.contains(c.nombre) ? 'checked' : ''}>
                            <label class="form-check-label" for="cuenta-${fila.index}"><c:out value="${c.nombre}"/></label>
                        </div>
                    </c:forEach>
                </div>
            </div>
            <div class="col-md-6">
                <label class="form-label">Plataformas que puede usar</label>
                <div class="border rounded p-2 bg-white">
                    <c:forEach items="${todasPlataformas}" var="p" varStatus="fila">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="plataformas"
                                   id="plataforma-${fila.index}" value="<c:out value='${p.nombre}'/>"
                                   ${plataformasElegidas.contains(p.nombre) ? 'checked' : ''}>
                            <label class="form-check-label" for="plataforma-${fila.index}"><c:out value="${p.nombre}"/></label>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <div class="col-12">
                <button type="submit" class="btn btn-primary">Guardar</button>
                <c:if test="${not empty disenador}">
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/disenadores">Cancelar</a>
                </c:if>
            </div>
        </form>
    </div>
</div>

<jsp:include page="pie.jsp"/>
