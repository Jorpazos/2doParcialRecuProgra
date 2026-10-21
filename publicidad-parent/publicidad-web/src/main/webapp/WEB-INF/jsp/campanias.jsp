<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="cabecera.jsp">
    <jsp:param name="titulo" value="Campañas"/>
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h2 class="mb-0">Campañas</h2>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/campanias?accion=nueva">Nueva campaña</a>
</div>

<div class="table-responsive">
    <table class="table table-striped table-hover bg-white text-nowrap">
        <thead>
        <tr>
            <th>N°</th>
            <th>Diseñador</th>
            <th>Cuenta</th>
            <th>Plataforma</th>
            <th>Contenido</th>
            <th>Costo estimado</th>
            <th>Producción</th>
            <th>Estado</th>
            <th>Acciones</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${campanias}" var="c">
            <tr>
                <td><c:out value="${c.id}"/></td>
                <td><c:out value="${c.disenador.legajo}"/> - <c:out value="${c.disenador.apellido}"/> <c:out value="${c.disenador.nombre}"/></td>
                <td><c:out value="${c.cuenta.nombre}"/></td>
                <td><c:out value="${c.plataforma.nombre}"/></td>
                <td><c:out value="${c.tipoContenido.nombre}"/></td>
                <td><fmt:formatNumber value="${c.costoTotal}" type="currency" currencySymbol="$"/></td>
                <td><c:out value="${c.horasProduccion}"/> h</td>
                <td><span class="badge text-bg-secondary"><c:out value="${c.estado}"/></span></td>
                <td>
                    <%-- Solo se puede modificar una campaña pendiente --%>
                    <c:if test="${c.estado.name() == 'PENDIENTE'}">
                        <a class="btn btn-sm btn-outline-primary"
                           href="${pageContext.request.contextPath}/campanias?accion=editar&id=${c.id}">Editar</a>
                    </c:if>
                    <button type="button" class="btn btn-sm btn-outline-danger btn-ajax"
                            data-url="${pageContext.request.contextPath}/campanias" data-accion="eliminar" data-id="${c.id}"
                            data-confirmar="¿Eliminar la campaña N° ${c.id}?">Eliminar</button>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty campanias}">
            <tr><td colspan="9" class="text-center text-muted">No hay campañas cargadas</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<jsp:include page="pie.jsp"/>
