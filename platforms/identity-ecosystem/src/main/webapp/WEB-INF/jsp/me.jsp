<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8"/>
  <title>Identity Ecosystem - Me</title>
</head>
<body>
  <h1>Authenticated Area</h1>
  <p>The Spring Security filter chain is active.</p>
  <p><a href="${pageContext.request.contextPath}/me/password">Change password</a></p>
  <p><a href="logout">Sign out</a></p>
</body>
</html>
