<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
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
								<h4 class="card-title">Customer Manager</h4>
								<c:if test="${not empty param.error}">
									<label id="error" class="alert alert-danger">${param.error}</label>
								</c:if>
								<form action="/addArea" method="POST">

									<div class="form-group">
										<label for="name">Name</label> <input id="name" type="text"
											class="form-control" name="name" required autofocus>
									</div>

									
									<div class="form-group no-margin">
										<button type="submit" class="btn btn-primary btn-block">
											Create Area</button>
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
								<h4 class="card-title">Sub Area Manager</h4>
								<c:if test="${not empty param.error}">
									<label id="error" class="alert alert-danger">${param.error}</label>
								</c:if>
								<form action="/addSubArea" method="POST">

									<div class="form-group">
										<label for="wardNumber">Ward Number</label> <input
											id="wardNumber" type="text" class="form-control"
											name="wardNumber" required>
									</div>

									<div class="form-group">
										<label for="wardNumber2">Ward Number 2</label> <input
											id="wardNumber2" type="text" class="form-control"
											name="wardNumber2" required>
									</div>

									<div class="form-group">
										<label for="areaName">Area Name</label>
										<form:select path="subArea.area.id" items="${areaList}"
											itemLabel="name" itemValue="id" />
									</div>

									<div class="form-group no-margin">
										<button type="submit" class="btn btn-primary btn-block">
											Create Sub Area</button>
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
								<h4 class="card-title">Add Street</h4>
								<c:if test="${not empty param.error}">
									<label id="error" class="alert alert-danger">${param.error}</label>
								</c:if>
								<form action="/addStreet" method="POST">

									<div class="form-group">
										<label for="streetNumber">Street Number</label> <input
											id="streetNumber" type="text" class="form-control"
											name="streetNumber" required autofocus>
									</div>

									<div class="form-group">
										<label for="streetNumber2">Name 2</label> <input
											id="streetNumber2" type="text" class="form-control"
											name="streetNumber2" required>
									</div>

									<div class="form-group">
										<label for="areaName">Area</label>
										<form:select path="subArea.area.id" items="${areaList}"
											itemLabel="name" itemValue="id" />
									</div>

									<div class="form-group no-margin">
										<button type="submit" class="btn btn-primary btn-block">
											Create Area</button>
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