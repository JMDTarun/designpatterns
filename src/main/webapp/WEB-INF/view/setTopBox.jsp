<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<meta charset="utf-8">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Expires" content="Sat, 01 Dec 2001 00:00:00 GMT">
<title>Area</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/themes/base/jquery.ui.base.css" />
<link rel="stylesheet" type="text/css" media="screen"
	href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/themes/redmond/jquery-ui.css" />
<link rel="stylesheet" type="text/css" media="screen"
	href="/resources/css/ui.jqgrid.css" />
</head>
<body>

	<jsp:include page="templates/header.jsp" />
	<section>
		<div class="container">
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
					<form method="POST" action="/uploadSetTopBoxesFile" enctype="multipart/form-data">
						<input type="file" name="file" /><br />
						<br /> <input type="submit" value="Submit" />
					</form>
				</div>
			</div>
		</div>
	</section>

	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
	<script
		src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/jquery-ui.min.js"></script>
	<script src="/resources/js/lib/grid.locale-en.js"></script>
	<script src="/resources/js/lib/jquery.jqGrid.src.js"></script>

	<script src="/resources/js/setTopBox.js"></script>
	<!-- <script src="/resources/js/subarea.js"></script>
	<script src="/resources/js/networkChannel.js"></script> -->
</body>
</html>
