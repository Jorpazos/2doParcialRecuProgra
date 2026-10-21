<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="cabecera.jsp">
    <jsp:param name="titulo" value="Campaña"/>
</jsp:include>

<%-- Esta pagina sirve para dos cosas: si el servlet mando "campania" es una MODIFICACION,
     si no, es una campaña NUEVA --%>
<c:choose>

    <%-- ============ MODIFICAR una campaña pendiente ============ --%>
    <c:when test="${not empty campania}">
        <h2 class="mb-3">Modificar campaña N° <c:out value="${campania.id}"/></h2>
        <div class="card shadow-sm">
            <div class="card-body">
                <p class="mb-1"><strong>Diseñador:</strong>
                    <c:out value="${campania.disenador.legajo}"/> - <c:out value="${campania.disenador.apellido}"/>
                    <c:out value="${campania.disenador.nombre}"/></p>
                <p class="mb-3"><strong>Plataforma:</strong> <c:out value="${campania.plataforma.nombre}"/></p>

                <form class="form-ajax row g-3"
                      action="${pageContext.request.contextPath}/campanias"
                      method="post"
                      data-destino="${pageContext.request.contextPath}/campanias">
                    <input type="hidden" name="id" value="<c:out value='${campania.id}'/>">
                    <input type="hidden" name="accion" value="modificar">
                    <div class="col-md-6">
                        <label class="form-label" for="cuenta">Cuenta</label>
                        <select class="form-select" id="cuenta" name="cuenta" required>
                            <c:forEach items="${cuentas}" var="cu">
                                <option value="<c:out value='${cu.nombre}'/>"
                                        ${cu.nombre == campania.cuenta.nombre ? 'selected' : ''}>
                                    <c:out value="${cu.nombre}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label" for="tipo">Tipo de contenido</label>
                        <select class="form-select" id="tipo" name="tipo" required>
                            <c:forEach items="${tipos}" var="t">
                                <option value="<c:out value='${t.nombre}'/>"
                                        ${t.nombre == campania.tipoContenido.nombre ? 'selected' : ''}>
                                    <c:out value="${t.nombre}"/> (<c:out value="${t.horasProduccion}"/> h, factor <c:out value="${t.factor}"/>)
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-12">
                        <button type="submit" class="btn btn-primary">Guardar</button>
                        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/campanias">Cancelar</a>
                    </div>
                </form>
            </div>
        </div>
    </c:when>

    <%-- ============ CAMPAÑA NUEVA ============ --%>
    <c:otherwise>
        <h2 class="mb-3">Nueva campaña</h2>

        <%-- Paso 1: elegir el diseñador por su legajo (GET: es una consulta, no cambia datos) --%>
        <div class="card shadow-sm mb-4">
            <div class="card-header">1. Elegir diseñador</div>
            <div class="card-body">
                <form class="row g-3" action="${pageContext.request.contextPath}/campanias" method="get">
                    <input type="hidden" name="accion" value="nueva">
                    <div class="col-md-8">
                        <label class="form-label" for="legajo">Legajo del diseñador</label>
                        <select class="form-select" id="legajo" name="legajo" required>
                            <option value="">Elegir...</option>
                            <c:forEach items="${disenadores}" var="d">
                                <option value="<c:out value='${d.legajo}'/>"
                                        ${d.legajo == disenadorElegido.legajo ? 'selected' : ''}>
                                    <c:out value="${d.legajo}"/> - <c:out value="${d.apellido}"/> <c:out value="${d.nombre}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-4 d-flex align-items-end">
                        <button type="submit" class="btn btn-outline-primary">Ver opciones</button>
                    </div>
                </form>
            </div>
        </div>

        <%-- Paso 2: con el diseñador elegido, se muestran SUS cuentas y plataformas libres --%>
        <c:if test="${not empty disenadorElegido}">
            <div class="card shadow-sm">
                <div class="card-header">
                    2. Asignar campaña a <c:out value="${disenadorElegido.apellido}"/> <c:out value="${disenadorElegido.nombre}"/>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${empty plataformas}">
                            <div class="alert alert-warning mb-0">
                                Este diseñador no tiene plataformas disponibles: no puede gestionar ninguna
                                o todas tienen una campaña activa.
                            </div>
                        </c:when>
                        <c:when test="${empty cuentas}">
                            <div class="alert alert-warning mb-0">Este diseñador no tiene cuentas autorizadas.</div>
                        </c:when>
                        <c:otherwise>
                            <form class="form-ajax row g-3"
                                  action="${pageContext.request.contextPath}/campanias"
                                  method="post"
                                  data-destino="${pageContext.request.contextPath}/campanias">
                                <input type="hidden" name="legajo" value="<c:out value='${disenadorElegido.legajo}'/>">
                                <input type="hidden" name="accion" value="crear">
                                <div class="col-md-4">
                                    <label class="form-label" for="cuenta">Cuenta del cliente</label>
                                    <select class="form-select" id="cuenta" name="cuenta" required>
                                        <c:forEach items="${cuentas}" var="cu">
                                            <option value="<c:out value='${cu.nombre}'/>"><c:out value="${cu.nombre}"/></option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label" for="plataforma">Plataforma disponible</label>
                                    <select class="form-select" id="plataforma" name="plataforma" required>
                                        <c:forEach items="${plataformas}" var="p">
                                            <option value="<c:out value='${p.nombre}'/>">
                                                <c:out value="${p.nombre}"/> (alcance <c:out value="${p.alcanceMaximo}"/> M,
                                                CPC $<c:out value="${p.cpc}"/>, fijo $<c:out value="${p.costoFijo}"/>)
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label" for="tipo">Tipo de contenido</label>
                                    <select class="form-select" id="tipo" name="tipo" required>
                                        <c:forEach items="${tipos}" var="t">
                                            <option value="<c:out value='${t.nombre}'/>">
                                                <c:out value="${t.nombre}"/> (<c:out value="${t.horasProduccion}"/> h, factor <c:out value="${t.factor}"/>)
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-12">
                                    <p class="text-muted small mb-2">
                                        Al asignar, el sistema calcula el costo total estimado de la pauta
                                        (alcance × CPC × factor del contenido + costo fijo) y el tiempo de producción.
                                    </p>
                                    <button type="submit" class="btn btn-primary">Asignar campaña</button>
                                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/campanias">Cancelar</a>
                                </div>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </c:if>
    </c:otherwise>
</c:choose>

<jsp:include page="pie.jsp"/>
