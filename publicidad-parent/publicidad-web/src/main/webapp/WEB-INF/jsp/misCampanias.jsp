<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="cabecera.jsp">
    <jsp:param name="titulo" value="Mis campañas"/>
</jsp:include>

<h2 class="mb-3">Mis campañas</h2>

<div class="table-responsive">
    <table class="table table-striped table-hover bg-white text-nowrap">
        <thead>
        <tr>
            <th>N°</th>
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
                <td><c:out value="${c.cuenta.nombre}"/></td>
                <td><c:out value="${c.plataforma.nombre}"/></td>
                <td><c:out value="${c.tipoContenido.nombre}"/></td>
                <td><fmt:formatNumber value="${c.costoTotal}" type="currency" currencySymbol="$"/></td>
                <td><c:out value="${c.horasProduccion}"/> h</td>
                <td><span class="badge text-bg-secondary"><c:out value="${c.estado}"/></span></td>
                <td>
                    <%-- Una pendiente se puede iniciar y una iniciada se puede finalizar --%>
                    <c:if test="${c.estado.name() == 'PENDIENTE'}">
                        <button type="button" class="btn btn-sm btn-success btn-ajax"
                                data-url="${pageContext.request.contextPath}/mis-campanias" data-accion="iniciar" data-id="${c.id}"
                                data-confirmar="¿Iniciar la campaña N° ${c.id}?">Iniciar</button>
                    </c:if>
                    <c:if test="${c.estado.name() == 'INICIADA'}">
                        <button type="button" class="btn btn-sm btn-warning btn-ajax"
                                data-url="${pageContext.request.contextPath}/mis-campanias" data-accion="finalizar" data-id="${c.id}"
                                data-confirmar="¿Finalizar la campaña N° ${c.id}?">Finalizar</button>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty campanias}">
            <tr><td colspan="8" class="text-center text-muted">Todavía no tiene campañas asignadas</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<jsp:include page="pie.jsp"/>
