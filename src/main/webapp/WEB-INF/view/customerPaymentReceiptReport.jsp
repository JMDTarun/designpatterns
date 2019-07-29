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
<title>Customer Payment Receipt</title>
</head>
<body>

	<jsp:include page="templates/header.jsp" />

	<div class="reportFilters">
		<div>
			<label>Area</label> 
			<select id="selectArea">
					<option value="">Select Area</option>
			</select>
		</div>
		<div>
			<label>Sub Area</label> 
			<select id="selectSubArea">
				<option value="">Select Sub Area</option>
			</select>
		</div>
		<div>
			<label>Street</label> 
			<select id="selectStreet">
				<option value="">Select Street</option>
			</select>
		</div>
		
		<div>
			<label>Machine Number</label> 
			<input type="text" id="machineId" name="machineId">
			<!-- <select id="selectMachine">
				<option value="">Select Machine</option>
			</select> -->
		</div>
		
		<div>
			<label>Date Between</label> 
			<input type="text" name="startDate" id="startDate" />
			<input type="text" name="endDate" id="endDate" />
		</div>
		
		<div>
			<label>Payment Mode</label> 
			<span> 
				<select id="selectPaymentMode">
					<option value="">ALL</option>
					<option value="CASH">CASH</option>
					<option value="CHEQUE">CHEQUE</option>
				</select>
			</span>
		</div>	
		<div>
			<label>Payment Type</label> 
			<span> 
				<select id="selectPaymentType">
					<option value="">ALL</option>
					<option value="RENTAL">RENTAL</option>
					<option value="BOX">BOX</option>
				</select>
			</span>
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
			<table id="customerPaymentReceiptReport"></table>
			<div id="pagerCustomerPaymentReceiptReport"></div>
		</div>
	</div>
	<script src="/resources/js/customerPaymentReceiptReport.js"></script>
</body>
</html>
