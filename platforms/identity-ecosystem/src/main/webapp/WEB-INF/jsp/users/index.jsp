<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8"/>
  <title>Users - Identity Ecosystem</title>
</head>
<body>
  <h1>Users</h1>

  <p><a href="${pageContext.request.contextPath}/users/new">Create user</a></p>

  <table>
    <thead>
      <tr>
        <th>Public ID</th>
        <th>E-mail</th>
        <th>Display name</th>
        <th>Status</th>
        <th>Created at</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="user" items="${users}">
        <tr>
          <td><c:out value="${user.publicId}"/></td>
          <td><c:out value="${user.email}"/></td>
          <td><c:out value="${user.displayName}"/></td>
          <td><c:out value="${user.status}"/></td>
          <td><fmt:formatDate value="${user.createdAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
        </tr>
      </c:forEach>
    </tbody>
  </table>

  <p><a href="${pageContext.request.contextPath}/">Home</a></p>
</body>
</html>
