<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<meta charset="utf-8">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Expires" content="Sat, 01 Dec 2001 00:00:00 GMT">
<title>Customer Payment</title>

</head>
<body>

	<jsp:include page="templates/header.jsp" />
	<div class="row mt-3">
		<div class="col">
			<table id="customerPayments"></table>
			<div id="pagerCustomerPayments"></div>
		</div>
	</div>
	<script src="/resources/js/customerPayment.js"></script>
</body>
</html>
