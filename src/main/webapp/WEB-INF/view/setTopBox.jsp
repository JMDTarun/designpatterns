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
<title>Set Top Box</title>
</head>
<body>

	<jsp:include page="templates/header.jsp" />

	<div class="alert alert-success" id="successDiv"></div>
	<div class="row">
		<div class="col">
			<div class="card-body">
				<table id="setTopBoxes"></table>
				<div id="pagerSetTopBoxes"></div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col">
			<form method="POST" action="/uploadSetTopBoxesFile"
				enctype="multipart/form-data">
				<input type="file" name="file" /><br /> <br /> <input
					type="submit" value="Submit" />
			</form>
		</div>
	</div>

	<script src="/resources/js/setTopBox.js"></script>
</body>
</html>
