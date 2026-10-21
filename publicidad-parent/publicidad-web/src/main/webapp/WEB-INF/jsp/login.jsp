<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="cabecera.jsp">
    <jsp:param name="titulo" value="Ingresar"/>
</jsp:include>

<div class="row justify-content-center mt-5">
<div class="col-11 col-sm-8 col-md-6 col-lg-4">
<div class="card shadow-sm">
    <div class="card-body p-4">
        <h4 class="card-title text-center mb-4">Agencia de Publicidad</h4>
        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="mb-3">
                <label for="usuario" class="form-label">Usuario</label>
                <%-- Si el usuario tildo "recordarme", el nombre viene de la cookie usuarioRecordado --%>
                <input type="text" class="form-control" id="usuario" name="usuario" required
                       value="<c:out value='${not empty usuarioIngresado ? usuarioIngresado : cookie.usuarioRecordado.value}'/>">
            </div>
            <div class="mb-3">
                <label for="password" class="form-label">Contraseña</label>
                <input type="password" class="form-control" id="password" name="password" required>
            </div>
            <div class="form-check mb-3">
                <input class="form-check-input" type="checkbox" id="recordarme" name="recordarme"
                       ${not empty cookie.usuarioRecordado ? 'checked' : ''}>
                <label class="form-check-label" for="recordarme">Recordarme</label>
            </div>
            <button type="submit" class="btn btn-primary w-100">Ingresar</button>
        </form>
    </div>
</div>
</div>
</div>
<%-- Si el servlet mando un error, lo dejo en este div y app.js lo muestra con SweetAlert --%>
<div id="mensaje" data-error="<c:out value='${error}'/>"></div>

<jsp:include page="pie.jsp"/>
