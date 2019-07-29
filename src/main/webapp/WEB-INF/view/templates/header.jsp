<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" href="resources/css/style.css" />
<link rel="stylesheet"
	href="/resources/css/font-awesome.min.css">

<script src="/resources/js/common/jquery.min.js"></script>
<script src="/resources/js/common/jquery-ui.min.js"></script>
<script src="/resources/js/lib/grid.locale-en.js"></script>
<script src="/resources/js/lib/jquery.jqGrid.src.js"></script>
<link rel="stylesheet" type="text/css" media="screen"
	href="/resources/css/jquery-ui.css" />
<link rel="stylesheet" type="text/css" media="screen"
	href="/resources/css/ui.jqgrid.css" />
<link href="/resources/css/select2.min.css" rel="stylesheet" />
<script src="/resources/js/common/select2.min.js"></script>
<script src="/resources/js/common/URI.min.js"></script>
</head>
<div id="navBar" style="height: 11%;">
	<nav class="navbar sticky-top navbar-expand-lg navbar-dark bg-primary">
		<a class="navbar-brand" href="/"> <img
			src="resources/images/logo.png" width="50" height="50"
			class="d-inline-block align-top" alt="">
		</a>
		<button class="navbar-toggler" type="button" data-toggle="collapse"
			data-target="#navbarSupportedContent"
			aria-controls="navbarSupportedContent" aria-expanded="false"
			aria-label="Toggle navigation">
			<span class="navbar-toggler-icon"></span>
		</button>


		<div class="collapse navbar-collapse" id="navbarSupportedContent">
			<ul class="navbar-nav mr-auto">
				<li class="nav-item active"><a class="nav-link" href="/">Home
						<span class="sr-only">(current)</span>
				</a></li>
				<li class="nav-item active"><a class="nav-link"
					href="/users">User</a></li>
				<li class="nav-item active"><a class="nav-link"
					href="/users">User</a></li>
				<li class="nav-item active"><a class="nav-link" href="/area">Area</a>
				</li>
				<li class="nav-item active"><a class="nav-link" href="/network">Network</a>
				</li>
				<li class="nav-item active"><a class="nav-link" href="/packs">Pack</a>
				</li>
				<li class="nav-item active"><a class="nav-link"
					href="/customerType">Customer Type</a></li>
				<li class="nav-item active"><a class="nav-link"
					href="/setTopBox">Set Top Box</a></li>
				<li class="nav-item active"><a class="nav-link"
					href="/customer">Customer</a></li>
				<li class="nav-item active"><a class="nav-link"
					href="/customerPayment">Customer Payment</a></li>
				<li class="nav-item dropdown-content"><a href="#">Reports
						&#9662;</a>
					<ul class="dropdown">
						<li class="nav-item active"><a class="nav-link"
							href="/customerReports">Customer Reports</a></li>
						<li><a class="nav-link" href="/customerLedgreReports">Customer
								Ledgre Report</a></li>
						<li><a class="nav-link" href="/customerOutstandingReports">Customer
								Outstanding Report</a></li>
						<li><a class="nav-link" href="/customerPartialPaymentReports">Customer
								Partial Payment Report</a></li>
						<li><a class="nav-link" href="/paymentReceipts">Payment
								Receipt</a></li>
						<li><a class="nav-link" target="_blank" href="/uploadActions">Fastway
								Utility</a></li>
					</ul></li>
				<li class="nav-item active"><a class="nav-link"
					href="/utility">Utility</a></li>	
			</ul>
			<div>
				<a style="text-align: right; color: #fff;"
					href="<c:url value="logout" />">Logout <i
					class="fa fa-sign-out fa-lg"></i>
				</a>
			</div>
		</div>
	</nav>
</div>
