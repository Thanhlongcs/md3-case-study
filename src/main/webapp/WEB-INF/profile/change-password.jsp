<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: THINKPAD
  Date: 10/4/2022
  Time: 3:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

<c:if test="${requestScope['message'] != null}">
    <p>${requestScope['message']}</p>
</c:if>

<div class="mainDiv">
    <div class="cardStyle">
        <form action="" method="post" name="signupForm" id="signupForm">

            <img src="" id="signupLogo"/>

            <h2 class="formTitle">
                Login to your account
            </h2>
            <div class="inputDiv">
                <label class="inputLabel" >Old Password</label>
                <input type="password" id="current_password" name="current_password" required>
            </div>

            <div class="inputDiv">
                <label class="inputLabel" >New Password</label>
                <input type="password" id="new_password" name="new_password" required>
            </div>

            <div class="inputDiv">
                <label class="inputLabel" for="confirmPassword">Confirm Password</label>
                <input type="password" id="confirmPassword" name="confirm_password">
            </div>

            <div class="buttonWrapper">
                <button type="submit" id="submitButton"  class="submitButton pure-button pure-button-primary">
                    <span>Change Password</span>
                    <span id="loader"></span>
                </button>
            </div>

        </form>
    </div>
</div>
</body>
</html>
