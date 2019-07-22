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
				<option value="true">Greater Then 0</option>
				<option value="false">Less Then 0</option>
			</select>
		</div>
		
		<div>
			<label>Outstanding Between</label> 
			<input type="text" name="outstandingStart" id="outstandingStart" />
			<input type="text" name="outstandingEnd" id="outstandingEnd" />
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
		
		<div class="buttonPadding">
			<button type="button" class="btn btn-primary" id="submitFilters">Submit</button>
		</div>
		
		<div class="buttonPadding">
			<a id="downloadAnchor" class="btn btn-info" target="_blank" href="javascript(void);">Download Excel</a>
		</div>
	</div>

	<div class="row mt-3">
		<div class="col">
			<table id="customerOutstandingReport"></table>
			<div id="pagerCustomerOutstandingReport"></div>
		</div>
	</div>
	<script src="/resources/js/customerOutstandingReport.js"></script>
</body>
</html>
