<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>

<html>

<head>
<meta charset="utf-8">

<title><fmt:message key="app.title" /></title>

<link href="<c:url value="/favicon.ico" />" rel="shortcut icon">
<link href="<c:url value="/libs/bootstrap/css/bootstrap.css" />"
    rel="stylesheet" media="screen" />
<link href="<c:url value="/libs/library/css/common.css" />" rel="stylesheet" />
<link href="<c:url value="/libs/library/css/login.css" />" rel="stylesheet" />
</head>

<body>
    <div class="container">
        <c:if test="${not empty lastLoginFailed}">
            <!-- Notification -->
            <div class="alert alert-error">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                <fmt:message key="error.login" />
            </div>
        </c:if>

        <!--  Form for Login -->
        <form name="loginForm" action="<c:url value="j_spring_security_check"/>"
            method="POST" class="form-signin">
            <h2 class="form-signin-heading">
                <fmt:message key="app.login.title" />
            </h2>

            <input name="username" type="text" class="input-block-level"
                placeholder="<fmt:message key="app.login.username" />" />
            <input name="password" type="password" class="input-block-level"
                placeholder="<fmt:message key="app.login.password" />" />
            <button name="submit" type="submit" class="btn btn-primary">
                <fmt:message key="app.login" />
            </button>
        </form>
    </div>

    <script src="<c:url value="/libs/jquery/jquery.js" />"></script>
    <script src="<c:url value="/libs/bootstrap/js/bootstrap.js" />"></script>
</body>

</html>