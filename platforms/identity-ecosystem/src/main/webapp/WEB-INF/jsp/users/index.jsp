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

  <c:if test="${param.stateChanged == 'true'}">
    <p>User state updated successfully.</p>
  </c:if>

  <c:if test="${param.stateChangeError == 'true'}">
    <p>User state could not be updated.</p>
  </c:if>

  <p><a href="${pageContext.request.contextPath}/users/new">Create user</a></p>

  <table>
    <thead>
      <tr>
        <th>Public ID</th>
        <th>E-mail</th>
        <th>Display name</th>
        <th>Status</th>
        <th>Created at</th>
        <th>State</th>
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
          <td>
            <form method="post" action="${pageContext.request.contextPath}/users/state">
              <input type="hidden" name="publicId" value="${user.publicId}"/>
              <select name="status">
                <option value="ACTIVE" <c:if test="${user.status == 'ACTIVE'}">selected="selected"</c:if>>ACTIVE</option>
                <option value="SUSPENDED" <c:if test="${user.status == 'SUSPENDED'}">selected="selected"</c:if>>SUSPENDED</option>
                <option value="LOCKED" <c:if test="${user.status == 'LOCKED'}">selected="selected"</c:if>>LOCKED</option>
                <option value="INACTIVE" <c:if test="${user.status == 'INACTIVE'}">selected="selected"</c:if>>INACTIVE</option>
              </select>
              <input type="submit" value="Update"/>
            </form>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>

  <p><a href="${pageContext.request.contextPath}/">Home</a></p>
</body>
</html>
