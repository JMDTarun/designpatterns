<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>Area Manager</title>
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
								<h4 class="card-title">Set Top Box</h4>
								<c:if test="${not empty param.error}">
									<label id="error" class="alert alert-danger">${param.error}</label>
								</c:if>
								<form action="/addArea" method="POST">

									<div class="form-group">
										<label for="setTopBoxNumber">Number</label> <input
											id="setTopBoxNumber" type="text" class="form-control"
											name="setTopBoxNumber" required autofocus>
									</div>
									<div class="form-group">
										<label for="cardNumber">Card Number</label> <input
											id="cardNumber" type="text" class="form-control"
											name="cardNumber" required autofocus>
									</div>
									<div class="form-group">
										<label for="safeCode">Safe Code</label> <input id="safeCode"
											type="text" class="form-control" name="safeCode" required
											autofocus>
									</div>
									<div class="form-group no-margin">
										<button type="submit" class="btn btn-primary btn-block">
											Add Set Top Box</button>
									</div>
								</form>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</section>
	<jsp:include page="templates/footer.jsp" />
</body>
</html>