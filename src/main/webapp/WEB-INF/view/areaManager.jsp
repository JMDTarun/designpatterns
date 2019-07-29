<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<meta charset="utf-8">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Expires" content="Sat, 01 Dec 2001 00:00:00 GMT">
<title>Area</title>

</head>
<body>

	<jsp:include page="templates/header.jsp" />
	<div class="row mt-3">
		<div class="col">
			<table id="areas"></table>
			<div id="pagerAreas"></div>
		</div>
		<div class="col">
			<table id="subAreas"></table>
			<div id="pagerSubAreas"></div>
		</div>
	</div>
	<div class="row mt-3">
		<div class="col">
			<table id="streets"></table>
			<div id="pagerStreets"></div>
		</div>
	</div>

	<script src="/resources/js/area.js"></script>
	<script src="/resources/js/subArea.js"></script>
	<script src="/resources/js/streets.js"></script>
	<!-- <script src="/resources/js/subarea.js"></script>
	<script src="/resources/js/networkChannel.js"></script> -->
</body>
</html>
