<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>Network Manager</title>
</head>

<body class="my-login-page">
	<jsp:include page="templates/header.jsp" />
	<section class="h-100">
		<div class="container h-100">
			<div class="row h-100">
				<div class="col">
					<div class="card-wrapper">
						<div class="card fat">
							<div class="card-body">
								<h4 class="card-title">Network Manager</h4>
								<c:if test="${not empty param.error}">
									<label id="error" class="alert alert-danger">${param.error}</label>
								</c:if>
								<form action="/addNetwork" method="POST">

									<div class="form-group">
										<label for="name">Name</label> <input id="name" type="text"
											class="form-control" name="name" required autofocus>
									</div>
									
									<div class="form-group no-margin">
										<button type="submit" class="btn btn-primary btn-block">
											Create Network</button>
									</div>
								</form>
							</div>
						</div>
						<jsp:include page="templates/copyright.jsp" />
					</div>
				</div>
				<div class="col">
					<div class="card-wrapper">
						<div class="card fat">
							<div class="card-body">
								<h4 class="card-title">Channel</h4>
								<c:if test="${not empty param.error}">
									<label id="error" class="alert alert-danger">${param.error}</label>
								</c:if>
								<form action="/addChannel" method="POST">

									<div class="form-group">
										<label for="name">Name</label> <input
											id="name" type="text" class="form-control"
											name="name" required>
									</div>
									<div class="form-group no-margin">
										<button type="submit" class="btn btn-primary btn-block">
											Create Channel</button>
									</div>
								</form>
							</div>
						</div>
						<jsp:include page="templates/copyright.jsp" />
					</div>
				</div>
				<div class="col">
					<div class="card-wrapper">
						<div class="card fat">
							<div class="card-body">
								<h4 class="card-title">Network Channel</h4>
								<c:if test="${not empty param.error}">
									<label id="error" class="alert alert-danger">${param.error}</label>
								</c:if>
								<form action="/addNetworkChannel" method="POST">
								
									<div class="form-group">
										<label for="name">Name</label> <input
											id="name" type="text" class="form-control"
											name="name" required>
									</div>

									<div class="form-group">
										<label for="monthlyRent">Monthly Rent</label> <input
											id="monthlyRent" type="text" class="form-control"
											name="monthlyRent" required autofocus>
									</div>
									
									<div class="form-group">
										<label for="surcharge">Surcharge</label> <input
											id="surcharge" type="text" class="form-control"
											name="surcharge" required autofocus>
									</div>
									
									<div class="form-group">
										<label for="entTax">Ent Tax</label> <input
											id="entTax" type="text" class="form-control"
											name="entTax" required autofocus>
									</div>
									
									<div class="form-group">
										<label for="otherAmount">Other Amount</label> <input
											id="otherAmount" type="text" class="form-control"
											name="otherAmount" required autofocus>
									</div>
									
									<div class="form-group">
										<label for="areaName">Network</label>
										<form:select path="networkChannel.network.id" items="${networkList}"
											itemLabel="name" itemValue="id" />
									</div>
									
									<div class="form-group">
										<label for="areaName">Channel</label>
										<form:select path="networkChannel.channel.id" items="${channelList}"
											itemLabel="name" itemValue="id" />
									</div>

									<div class="form-group no-margin">
										<button type="submit" class="btn btn-primary btn-block">
											Create Network Channel</button>
									</div>
								</form>
							</div>
						</div>
						<jsp:include page="templates/copyright.jsp" />
					</div>
				</div>
			</div>

		</div>
	</section>
	<jsp:include page="templates/footer.jsp" />
</body>
</html>