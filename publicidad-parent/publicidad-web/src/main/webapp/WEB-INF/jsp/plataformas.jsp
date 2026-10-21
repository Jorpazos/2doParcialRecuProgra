<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="cabecera.jsp">
    <jsp:param name="titulo" value="Plataformas"/>
</jsp:include>

<h2 class="mb-3">Plataformas de medios</h2>

<div class="table-responsive mb-4">
    <table class="table table-striped table-hover bg-white text-nowrap">
        <thead>
        <tr>
            <th>Nombre</th>
            <th>Formato principal</th>
            <th>Alcance máximo (millones)</th>
            <th>Costo fijo</th>
            <th>CPC estimado</th>
            <th>Acciones</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${plataformas}" var="p">
            <tr>
                <td><c:out value="${p.nombre}"/></td>
                <td><c:out value="${p.formatoPrincipal}"/></td>
                <td><fmt:formatNumber value="${p.alcanceMaximo}" maxFractionDigits="2"/></td>
                <td><fmt:formatNumber value="${p.costoFijo}" type="currency" currencySymbol="$"/></td>
                <td><fmt:formatNumber value="${p.cpc}" type="currency" currencySymbol="$"/></td>
                <td>
                    <%-- El nombre puede tener espacios, por eso lo mando codificado con c:url --%>
                    <c:url var="urlPlataforma" value="/plataformas">
                        <c:param name="nombre" value="${p.nombre}"/>
                    </c:url>
                    <a class="btn btn-sm btn-outline-primary" href="${urlPlataforma}">Editar</a>
                    <button type="button" class="btn btn-sm btn-outline-danger btn-ajax"
                            data-url="${pageContext.request.contextPath}/plataformas" data-accion="eliminar" data-nombre="<c:out value='${p.nombre}'/>"
                            data-confirmar="¿Eliminar la plataforma <c:out value='${p.nombre}'/>?">Eliminar</button>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty plataformas}">
            <tr><td colspan="6" class="text-center text-muted">No hay plataformas cargadas</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<%-- Si el servlet mando una plataforma, el formulario es de EDICION (PUT); si no, es de ALTA (POST) --%>
<div class="card shadow-sm">
    <div class="card-header">
        <c:choose>
            <c:when test="${not empty plataforma}">Editar plataforma</c:when>
            <c:otherwise>Nueva plataforma</c:otherwise>
        </c:choose>
    </div>
    <div class="card-body">
        <form class="form-ajax row g-3"
              action="${pageContext.request.contextPath}/plataformas"
              method="post"
              data-destino="${pageContext.request.contextPath}/plataformas">
            <input type="hidden" name="accion" value="${not empty plataforma ? 'modificar' : 'crear'}">

            <div class="col-md-4">
                <label class="form-label" for="nombre">Nombre</label>
                <%-- El nombre es la clave: al editar no se puede cambiar --%>
                <input type="text" class="form-control" id="nombre" name="nombre" required
                       value="<c:out value='${plataforma.nombre}'/>" ${not empty plataforma ? 'readonly' : ''}>
            </div>
            <div class="col-md-4">
                <label class="form-label" for="formatoPrincipal">Formato principal</label>
                <input type="text" class="form-control" id="formatoPrincipal" name="formatoPrincipal" required
                       value="<c:out value='${plataforma.formatoPrincipal}'/>">
            </div>
            <div class="col-md-4">
                <label class="form-label" for="alcanceMaximo">Alcance máximo (millones de usuarios)</label>
                <input type="number" step="any" min="0" class="form-control" id="alcanceMaximo" name="alcanceMaximo" required
                       value="<c:out value='${plataforma.alcanceMaximo}'/>">
            </div>
            <div class="col-md-4">
                <label class="form-label" for="costoFijo">Costo de publicación fijo ($)</label>
                <input type="number" step="any" min="0" class="form-control" id="costoFijo" name="costoFijo" required
                       value="<c:out value='${plataforma.costoFijo}'/>">
            </div>
            <div class="col-md-4">
                <label class="form-label" for="cpc">CPC estimado ($)</label>
                <input type="number" step="any" min="0" class="form-control" id="cpc" name="cpc" required
                       value="<c:out value='${plataforma.cpc}'/>">
            </div>

            <div class="col-12">
                <button type="submit" class="btn btn-primary">Guardar</button>
                <c:if test="${not empty plataforma}">
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/plataformas">Cancelar</a>
                </c:if>
            </div>
        </form>
    </div>
</div>

<jsp:include page="pie.jsp"/>
