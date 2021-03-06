<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<meta charset="utf-8">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Expires" content="Sat, 01 Dec 2001 00:00:00 GMT">
<title>Customer Type</title>
</head>
<body>

	<jsp:include page="templates/header.jsp" />
	<div class="row mt-3">
		<div class="col">
			<table id="customerTypes"></table>
			<div id="pagerCustomerTypes"></div>
		</div>
	</div>

	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
	<script
		src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/jquery-ui.min.js"></script>
	<script src="/resources/js/lib/grid.locale-en.js"></script>
	<script src="/resources/js/lib/jquery.jqGrid.src.js"></script>

	<script src="/resources/js/customerType.js"></script>
</body>
</html>
