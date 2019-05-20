<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<meta charset="utf-8">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Expires" content="Sat, 01 Dec 2001 00:00:00 GMT">
<title>Books</title>
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
			<table id="networks"></table>
			<div id="pagerNetworks"></div>
		</div>
		<div class="col">
			<table id="channels"></table>
			<div id="pagerChannels"></div>
		</div>
	</div>

	<div class="row">
		<div class="col">
			<table id="channelNetworks"></table>
			<div id="pagerChannelNetworks"></div>
		</div>
	</div>

	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
	<script
		src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/jquery-ui.min.js"></script>
	<script src="/resources/js/lib/grid.locale-en.js"></script>
	<script src="/resources/js/lib/jquery.jqGrid.src.js"></script>

	<script src="/resources/js/network.js"></script>
	<script src="/resources/js/channel.js"></script>
	<script src="/resources/js/networkChannel.js"></script>
</body>
</html>
