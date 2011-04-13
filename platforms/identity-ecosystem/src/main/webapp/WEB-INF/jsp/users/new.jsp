<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8"/>
  <title>Create User - Identity Ecosystem</title>
</head>
<body>
  <h1>Create User</h1>

  <c:if test="${not empty successMessage}">
    <p>${successMessage}</p>
  </c:if>

  <form:form method="post" action="${pageContext.request.contextPath}/users" commandName="createUserForm">
    <p>
      <label for="email">E-mail</label><br/>
      <form:input path="email" id="email"/>
      <form:errors path="email"/>
    </p>

    <p>
      <label for="displayName">Display name</label><br/>
      <form:input path="displayName" id="displayName"/>
      <form:errors path="displayName"/>
    </p>

    <p>
      <label for="password">Password</label><br/>
      <form:password path="password" id="password"/>
      <form:errors path="password"/>
    </p>

    <p>
      <label for="confirmPassword">Confirm password</label><br/>
      <form:password path="confirmPassword" id="confirmPassword"/>
      <form:errors path="confirmPassword"/>
    </p>

    <p>
      <input type="submit" value="Create user"/>
    </p>
  </form:form>

  <p><a href="${pageContext.request.contextPath}/">Home</a></p>
</body>
</html>
