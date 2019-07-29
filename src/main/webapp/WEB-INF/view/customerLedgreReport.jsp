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
<title>Customer Ledgre Report</title>
</head>
<body>

	<jsp:include page="templates/header.jsp" />

	<div class="reportFilters">
		<div>
			<label>Customer Code</label> 
			<select id="selectCustomerCode">
					<option value="">Select Customer code</option>
			</select>
		</div>
		<div>
			<label>Customer Name</label> 
			<select id="selectCustomerName">
					<option value="">Select Customer Name</option>
			</select>
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
			<table id="customerLedgreReport"></table>
		</div>
	</div>
	<script src="/resources/js/customerLedgreReport.js"></script>
</body>
</html>
