<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- Fijo el idioma (Argentina) para que fechas y montos salgan con el formato de aca --%>
<fmt:setLocale value="es_AR" scope="request"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${param.titulo}"/> - Agencia de Publicidad</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<%-- Si hay un usuario en la Session, muestro el menu --%>
<c:if test="${not empty sessionScope.usuario}">
    <jsp:include page="menu.jsp"/>
</c:if>
<div class="container py-4">
