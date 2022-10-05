<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 10/4/2022
  Time: 9:29 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page='../boostrap/boostrap.jsp'>
    <jsp:param name="articleId" value=""/>
</jsp:include>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h1>WELCOME : ${user.name}</h1>
<a href="users?action=logout">LOG OUT</a>
<a href="users?action=change_avatar">change avatar</a>
<c:if test="${requestScope['success'] != null}">
    <p style="color: lightseagreen">${requestScope['success']}</p>
</c:if>

${sessionScope['userLogin'].name} <br>
<a href="users?action=change_password">change password</a>
</body>
</html>
