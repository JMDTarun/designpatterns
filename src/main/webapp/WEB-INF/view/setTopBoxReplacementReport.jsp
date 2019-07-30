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
<title>Set Top Box Replacement Report</title>
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
			<label>Set Top Box Replacement</label> 
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
			<label>Charge Between</label> 
			<input type="text" name="chargeStart" id="chargeStart" />
			<input type="text" name="chargeEnd" id="chargeEnd" />
		</div>
		
		<div>
			<label>Replacement Reason</label> 
			<select id="selectReplacementReason">
				<option value="">Select Replacement Reason</option>
				<option value="FAULTY">FAULTY</option>
				<option value="BLOCK">BLOCK</option>
				<option value="ALLOTED">ALLOTED</option>
			</select>
		</div>
		
		<div>
			<label>Replacement Type</label> 
			<select id="selectReplacementType">
				<option value="">Select Replacement Type</option>
				<option value="TEMPORARY">TEMPORARY</option>
				<option value="PERMANENT">PERMANENT</option>
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
			<table id="setTopBoxReplacementReport"></table>
			<div id="pagerSetTopBoxReplacementReport"></div>
		</div>
	</div>
	<script src="/resources/js/setTopBoxReplacementReport.js"></script>
</body>
</html>
