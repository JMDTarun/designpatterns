<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<meta charset="utf-8">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Expires" content="Sat, 01 Dec 2001 00:00:00 GMT">
<title>Manage Credentials</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="/resources/css/customerReport.css" />
</head>
<body>

	<jsp:include page="templates/header.jsp" />

	<div class="alert alert-success" id="success-alert">
		<button type="button" class="close" data-dismiss="alert">x</button>
		<strong>Success! </strong> Credentials updated.
	</div>

	<form class="form-inline">
      <div class="form-group">
        <label for="username">Username</label>
        <input type="text" class="form-control" id="fastwayUsername" value="<c:out value="${username}"/>"/>
      </div>
      <div class="form-group">
        <label for="password">Password</label>
        <input type="text" class="form-control" id="fastwayPassword" value="<c:out value="${password}"/>">
      </div>
      <button id="saveCredentials" class="btn btn-default btn-primary">Save Credentials</button>
    </form>

	<script src="/resources/js/manageCredentials.js"></script>
</body>
</html>
