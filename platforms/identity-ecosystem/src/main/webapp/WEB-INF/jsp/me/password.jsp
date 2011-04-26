<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8"/>
  <title>Change Password - Identity Ecosystem</title>
</head>
<body>
  <h1>Change Password</h1>

  <form:form method="post" action="${pageContext.request.contextPath}/me/password" commandName="changePasswordForm">
    <p>
      <label for="currentPassword">Current password</label><br/>
      <form:password path="currentPassword" id="currentPassword"/>
      <form:errors path="currentPassword"/>
    </p>

    <p>
      <label for="newPassword">New password</label><br/>
      <form:password path="newPassword" id="newPassword"/>
      <form:errors path="newPassword"/>
    </p>

    <p>
      <label for="confirmPassword">Confirm new password</label><br/>
      <form:password path="confirmPassword" id="confirmPassword"/>
      <form:errors path="confirmPassword"/>
    </p>

    <p>
      <input type="submit" value="Change password"/>
    </p>
  </form:form>

  <p><a href="${pageContext.request.contextPath}/me">Back to my area</a></p>
</body>
</html>
