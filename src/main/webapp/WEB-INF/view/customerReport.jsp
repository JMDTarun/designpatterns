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
<title>Customer Report</title>
</head>
<body>

	<jsp:include page="templates/header.jsp" />

	<div class="alert alert-success" id="successDiv"></div>
	<div class="alert alert-danger" id="errorDiv"></div>

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
			<label>Customer Status</label> 
			<select id="selectCustomerStatus">
				<option value="">ALL</option>
				<option value="ACTIVE">ACTIVE</option>
				<option value="DEACTIVE">DEACTIVE</option>
			</select>
		</div>
		
		<div>
			<label>Pack</label> 
			<select id="selectPack">
				<option value="">Select Pack</option>
			</select>
		</div>
		
		<div>
			<label>Rent</label> 
			<select id="selectRent">
				<option value="">Select Rent</option>
			</select>
		</div>
		
		<div>
			<label>Total Charge</label> 
			<input type="text" id="totalCharge">
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
		
		<div>
			<label>Payment Day</label> 
			<input type="text" name="paymentDayStart" id="paymentDayStart" />
			<input type="text" name="paymentDayEnd" id="paymentDayEnd" />
		</div>	
		<div>
			<label>Assigned Set Top Boxes</label> 
			<span> 
				<select id="selectAssignedSetTopBoxes">
					<option value="">ALL</option>
					<option value="1">Only Box Issue</option>
					<option value="0">Box Not Issue</option>
				</select>
			</span>
		</div>
		
		<div>
			<label>Outstanding Between</label> 
			<input type="text" name="outstandingStart" id="outstandingStart" />
			<input type="text" name="outstandingEnd" id="outstandingEnd" />
		</div>
		
		<div>
			<label>Customer Type</label> 
			<select id="selectCustomerType">
				<option value="">ALL</option>
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
			<table id="customerReport"></table>
			<div id="pagerCustomerReport"></div>
		</div>
	</div>
	<script src="/resources/js/customerReport.js"></script>
</body>
</html>
