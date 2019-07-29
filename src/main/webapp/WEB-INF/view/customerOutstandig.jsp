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
<title>Customer Outstanding</title>
</head>
<body>

	<jsp:include page="templates/header.jsp" />

	<div class="reportFilters">
		<div>
			<label>Area</label> 
			<span> 
				<select id="selectArea">
						<option value="">Select Area</option>
				</select>
			</span>
		</div>
		<div>
			<label>Sub Area</label> 
			<span> 
				<select id="selectSubArea">
					<option value="">Select Sub Area</option>
				</select>
			</span>
		</div>
		<div>
			<label>Street</label> 
			<span> 
				<select id="selectStreet">
					<option value="">Select Street</option>
				</select>
			</span>
		</div>
		<div>
			<label>Customer Status</label> 
			<span> 
				<select id="selectCustomerStatus">
					<option value="">ALL</option>
					<option value="ACTIVE">ACTIVE</option>
					<option value="DEACTIVE">DEACTIVE</option>
				</select>
			</span>
		</div>
		
		<div>
			<label>Pack</label> 
			<span> 
				<select id="selectPack">
					<option value="">Select Pack</option>
				</select>
			</span>
		</div>
		
		<div>
			<label>Rent</label> 
			<span> 
				<select id="selectRent">
					<option value="">Select Rent</option>
				</select>
			</span>
		</div>
		
		<div>
			<label>Total Charge</label> 
			<span> 
				<input type="text" id="totalCharge">
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
			<table id="customerReport"></table>
			<div id="pagerCustomerReport"></div>
		</div>
	</div>
	<script src="/resources/js/customerReport.js"></script>
</body>
</html>
