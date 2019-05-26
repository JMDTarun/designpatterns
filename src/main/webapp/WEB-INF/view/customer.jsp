<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<meta charset="utf-8">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Expires" content="Sat, 01 Dec 2001 00:00:00 GMT">
<title>Customer</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/themes/base/jquery.ui.base.css" />
<link rel="stylesheet" type="text/css" media="screen"
	href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/themes/redmond/jquery-ui.css" />
<link rel="stylesheet" type="text/css" media="screen"
	href="/resources/css/ui.jqgrid.css" />
</head>
<body>

	<jsp:include page="templates/header.jsp" />
	<div class="row">
		<div class="col">
			<table id="customers"></table>
			<div id="pagerCustomers"></div>
		</div>
	</div>

	<div id="myDialog">
		<input type="hidden" id="customerId">
		<input type="hidden" id="customerSetTopBoxId">
		<div class="row">
			<div class="col">Status</div>
			<div class="col">
				<select id="status" name="status">
					<option value="FREE">FREE</option>
					<option value="FAULTY">FAULTY</option>
					<option value="TEMPORARY_REMOVED">BLOCK</option>
				</select>
			</div>
		</div>
		<div class="row">
			<div class="col">Reason</div>
			<div class="col">
				<input type="text" name="reason" id="reason" />
			</div>
		</div>
		
		<div class="row">
			<div class="col">Amount</div>
			<div class="col">
				<input type="text" name="amount" id="amount" />
			</div>
		</div>
	</div>

	<div id="mySelectRowDialog">
		<div class="row">
			<div class="col">Please Select Row To Delete</div>
		</div>
	</div>

	<!-- <div id="myAdditionalDiscountDialog">
		<div class="row">
			<div class="col">Additional Discount</div>
			<div class="col">
				<input type="text" name="additionalDiscount" id="additionalDiscount">
			</div>
		</div>
	</div>
	
	<div id="myAdditionalDiscountDialog">
		<input type="hidden" id="customerId">
		<input type="hidden" id="customerSetTopBoxId">
		<div class="row">
			<div class="col">Additional Discount</div>
			<div class="col">
				<input type="text" name="additionalDiscount" id="additionalDiscount">
			</div>
		</div>
	</div>

	<div id="myActiveDeactiveDialog">
		<input type="hidden" id="customerId">
		<input type="hidden" id="customerSetTopBoxId">
		<div class="row">
			<div class="col">Status</div>
			<div class="col">
				<input type="text" name="additionalDiscount" id="additionalDiscount">
			</div>
		</div>
	</div> -->

	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
	<script
		src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/jquery-ui.min.js"></script>
	<script src="/resources/js/lib/grid.locale-en.js"></script>
	<script src="/resources/js/lib/jquery.jqGrid.src.js"></script>

	<script src="/resources/js/customer.js"></script>
	<!-- <script src="/resources/js/subarea.js"></script>
	<script src="/resources/js/networkChannel.js"></script> -->
</body>
</html>
