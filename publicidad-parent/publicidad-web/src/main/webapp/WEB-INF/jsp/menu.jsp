<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Menu de arriba. Las opciones salen de la base (tabla perfil_opcion) segun el perfil del usuario,
     asi no hay ningun if por perfil. --%>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <span class="navbar-brand">Agencia de Publicidad</span>
        <ul class="navbar-nav me-auto">
            <c:forEach items="${sessionScope.usuario.opciones}" var="opcion">
                <li class="nav-item">
                    <a class="nav-link ${requestScope['javax.servlet.forward.servlet_path'] == opcion.url ? 'active' : ''}"
                       href="${pageContext.request.contextPath}${opcion.url}"><c:out value="${opcion.texto}"/></a>
                </li>
            </c:forEach>
        </ul>
        <span class="navbar-text me-3">
            <c:out value="${sessionScope.usuario.usuario}"/> (<c:out value="${sessionScope.usuario.perfil.nombre}"/>)
        </span>
        <%-- El logout es POST porque cambia el estado: cierra la sesion --%>
        <form action="${pageContext.request.contextPath}/logout" method="post" class="d-flex">
            <button type="submit" class="btn btn-outline-light btn-sm">Cerrar sesión</button>
        </form>
    </div>
</nav>
