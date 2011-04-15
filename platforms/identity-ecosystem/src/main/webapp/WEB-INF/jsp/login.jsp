<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8"/>
  <title>Identity Ecosystem - Sign In</title>
</head>
<body>
  <h1>Sign In</h1>

  <c:if test="${not empty param.error}">
    <p>Invalid e-mail or password.</p>
  </c:if>

  <c:if test="${not empty param.logout}">
    <p>You have signed out.</p>
  </c:if>

  <form action="<c:url value='/j_spring_security_check'/>" method="post">
    <p>
      <label for="j_username">E-mail</label>
      <input id="j_username" name="j_username" type="text"/>
    </p>
    <p>
      <label for="j_password">Password</label>
      <input id="j_password" name="j_password" type="password"/>
    </p>
    <p>
      <button type="submit">Sign in</button>
    </p>
  </form>
</body>
</html>
