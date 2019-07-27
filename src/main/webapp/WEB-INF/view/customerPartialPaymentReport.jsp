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
<title>Books</title>
<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
	<script
		src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/jquery-ui.min.js"></script>
	<script src="/resources/js/lib/grid.locale-en.js"></script>
	<script src="/resources/js/lib/jquery.jqGrid.src.js"></script>
<link rel="stylesheet" type="text/css" media="screen"
	href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/themes/base/jquery.ui.base.css" />
<link rel="stylesheet" type="text/css" media="screen"
	href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/themes/redmond/jquery-ui.css" />
<link rel="stylesheet" type="text/css" media="screen"
	href="/resources/css/ui.jqgrid.css" />
<link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.7/css/select2.min.css" rel="stylesheet" />
<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.7/js/select2.min.js"></script>
<link rel="stylesheet" type="text/css" media="screen"
	href="/resources/css/customerReport.css" />
</head>
<body>

	<jsp:include page="templates/header.jsp" />

	<div class="reportFilters">
		<div class="reportFiltersWidth">
			<label>Payment Between</label>
			<input type="text" name="partialPaymanetBetweenStart" id="partialPaymanetBetweenStart" />
			<input type="text" name="partialPaymanetBetweenEnd" id="partialPaymanetBetweenEnd" />
		</div>
		
		<div class="reportFiltersWidth">
			<label>Payment Type</label>
			<input type="radio" name="paymentBetween" value="partialPayment" checked> Partial Payment<br>
  			<input type="radio" name="paymentBetween" value="noPayment">No Payment<br>
		</div>
		
		<div class="buttonPadding">
			<button type="button" class="btn btn-primary" id="submitFilters">Submit</button>
		</div>
		
		<div class="buttonPadding">
			<a id="downloadAnchor" class="btn btn-info" target="_blank" href="javascript(void);">Download Excel</a>
		</div>
	</div>

	<div class="row mt-3">
		<div class="col">
			<table id="customerPartialPaymentReport"></table>
			<div id="pagerCustomerPartialPaymentReport"></div>
		</div>
	</div>
	<script src="/resources/js/customerPartialPaymentReport.js"></script>
</body>
</html>
