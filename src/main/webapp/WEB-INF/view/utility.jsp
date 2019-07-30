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
<title>Utility</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="/resources/css/customerReport.css" />
</head>
<body>

	<jsp:include page="templates/header.jsp" />

	<div class="alert alert-success" id="success-alert">
		<button type="button" class="close" data-dismiss="alert">x</button>
		<strong>Success! </strong> Done with utility.
	</div>

	<div class="reportFilters">
		<div>
			<label>Select Month</label> 
			<span> 
				<select id="selectMonth">
					<option value="">Select Month</option>
					<option value="1">JANUARY</option>
					<option value="2">FEBRUARY</option>
					<option value="3">MARCH</option>
					<option value="4">APRIL</option>
					<option value="5">MAY</option>
					<option value="6">JUNE</option>
					<option value="7">JULY</option>
					<option value="8">AUGUST</option>
					<option value="9">SEPTEMBER</option>
					<option value="10">OCTOBER</option>
					<option value="11">NOVEMBER</option>
					<option value="12">DECEMBER</option>
				</select>
			</span>
		</div>
		
		<div class="buttonPadding">
			<button type="button" class="btn btn-primary" id=runUtility>Run Utility</button>
		</div>
		
		<div class="buttonPadding">
			<button type="button" class="btn btn-danger" id=revertUtility>Revert Utility</button>
		</div>
	</div>

	<script src="/resources/js/utility.js"></script>
</body>
</html>
