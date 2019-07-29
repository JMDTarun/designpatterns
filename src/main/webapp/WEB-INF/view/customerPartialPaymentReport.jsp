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
<title>Customer Partial Payment Report</title>
</head>
<body>

	<jsp:include page="templates/header.jsp" />

	<div class="alert alert-success" id="successDiv"></div>
	<div class="alert alert-danger" id="errorDiv"></div>

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
		
		<div>
			<label>Outstanding</label> 
			<select id="selectOutstanding">
				<option value="">ALL</option>
				<option value="0">Equal To 0</option>
				<option value="1">Greater Then 0</option>
				<option value="-1">Less Then 0</option>
			</select>
		</div>
		
		<div class="buttonPadding">
			<button type="button" class="btn btn-primary" id="submitFilters">Submit</button>
		</div>
		
		<div class="buttonPadding">
			<a id="downloadAnchor" class="btn btn-info" target="_blank" href="javascript(void);">Download Excel</a>
		</div>
		
		<div class="buttonPadding">
			<button type="button" class="btn btn-primary" id="deactivateSetTopBoxes">Deactivate</button>
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
