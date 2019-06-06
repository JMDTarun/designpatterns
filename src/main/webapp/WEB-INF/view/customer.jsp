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
<title>Customer</title>
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
<body>

	<jsp:include page="templates/header.jsp" />
	<div id="customerContainer" style="height: 73%;">
		<div class="row">
			<div class="col">
				<table id="customers"></table>
				<div id="pagerCustomers"></div>
			</div>
		</div>
	</div>
	<div id="myDialog">
		<input type="hidden" id="customerId"> <input type="hidden"
			id="customerSetTopBoxId">
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
			<div class="col">Please Select Set Top Box</div>
		</div>
	</div>

	<div id="setTopBoxAlreadyActive">
		<div class="row">
			<div class="col">Set Top Box Already Active</div>
		</div>
	</div>
	
	<div id="setTopBoxAlreadyDeactive">
		<div class="row">
			<div class="col">Set Top Box Already Deactive</div>
		</div>
	</div>

	<div id="channelRemoveDialog">
		<input type="hidden" id="rcCid"> <input type="hidden"
			id="rcCustomerId"> <input type="hidden"
			id="rcCustomerSetTopBoxId">
		<div class="row">
			<div class="col">Channel Remove Date</div>
			<div class="col">
				<input type="text" name="channelRemoveDate" id="channelRemoveDate">
			</div>
		</div>
		<div class="row">
			<div class="col">Reason</div>
			<div class="col">
				<input type="text" name="rcReason" id="rcReason">
			</div>
		</div>
	</div>

	<div id="setTopBoxActivate">
		<input type="hidden" id="activateCid"> <input type="hidden"
			id="activateCustomerId"> <input type="hidden"
			id="activateCustomerSetTopBoxId">
		<div class="row">
			<div class="col">Activate Date</div>
			<div class="col">
				<input type="text" name="activateDate" id="activateDate">
			</div>
		</div>
		<div class="row">
			<div class="col">Reason</div>
			<div class="col">
				<input type="text" name="activateReason" id="activateReason">
			</div>
		</div>
	</div>

	<div id="setTopBoxDeactivate">
		<input type="hidden" id="deactivateCid"> <input type="hidden"
			id="deactivateCustomerId"> <input type="hidden"
			id="deactivateCustomerSetTopBoxId">
		<div class="row">
			<div class="col">Deactivate Date</div>
			<div class="col">
				<input type="text" name="deactivateDate" id="deactivateDate">
			</div>
		</div>
		<div class="row">
			<div class="col">Reason</div>
			<div class="col">
				<input type="text" name="deactivateReason" id="deactivateReason">
			</div>
		</div>
	</div>

	<div id="setTopBoxReplace">
		<input type="hidden" id="replaceCustomerId"> 
		<div class="row">
			<div class="col">Set Top Box</div>
			<div class="col">
				<select id="currentSetTopBox" name="currentSetTopBox"></select>
			</div>
		</div>
		<div class="row">
			<div class="col">Replace With</div>
			<div class="col">
				<select id="replacedSetTopBox" name="replacedSetTopBox"></select>
			</div>
		</div>
		<div class="row">
			<div class="col">Replacement Type</div>
			<div class="col">
				<select id="replacementType" name="replacementType">
					<option value="TEMPORARY">TEMPORARY</option>
					<option value="PERMANENT">PERMANENT</option>
				</select>
			</div>
		</div>
		<div class="row">
			<div class="col">Replacement Reason</div>
			<div class="col">
				<select id="replacementReason" name="replacementReason">
					<option value="FAULTY">FAULTY</option>
					<option value="BLOCK">BLOCK</option>
					<option value="ALLOTED">ALLOTED</option>
				</select>
			</div>
		</div>
		<div class="row">
			<div class="col">Replacement Charge</div>
			<div class="col">
				<input type="text" id="replacementCharge" name="replacementCharge">
			</div>
		</div>
	</div>

	<div id="myAdditionalDiscountDialog">
		<input type="hidden" id="adCustomerId"> <input type="hidden"
			id="adCustomerSetTopBoxId">
		<div class="row">
			<div class="col">Add Discount</div>
			<div class="col">
				<input type="text" name="additionalDiscount" id="additionalDiscount">
			</div>
		</div>
		<div class="row">
			<div class="col">Reason</div>
			<div class="col">
				<input type="text" name="adReason" id="adReason">
			</div>
		</div>
	</div>

	<div id="myAdditionalChargeDialog">
		<input type="hidden" id="acCustomerId"> <input type="hidden"
			id="acCustomerSetTopBoxId">
		<div class="row">
			<div class="col">Add Charge</div>
			<div class="col">
				<input type="text" name="additionalCharge" id="additionalCharge">
			</div>
		</div>
		<div class="row">
			<div class="col">Reason</div>
			<div class="col">
				<input type="text" name="adReason" id="adReason">
			</div>
		</div>
	</div>
	<script src="/resources/js/customer.js"></script>
</body>
</html>
