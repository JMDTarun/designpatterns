<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>List All Users</title>

<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
	<link rel="stylesheet" href="https://cdn.datatables.net/1.10.12/css/jquery.dataTables.min.css">
	<script src="https://cdn.datatables.net/1.10.12/js/jquery.dataTables.min.js"></script>
</head>

<body class="my-login-page">
	<jsp:include page="templates/header.jsp" />
	<section class="h-100">
		<div class="container h-100">
			<div class="row justify-content-md-center">
				<div class="card">

					<div class="card card-body table-responsive">
						<h1>Employees Table</h1>
<table id="employeesTable" class="table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Firstname</th>
                    <th>Lastname</th>
                    <th>Position</th>
                    <th>Age</th>
                    <th>Salary</th>
                    <th>Office</th>
                </tr>
            </thead>
        </table>					</div>
				</div>
				<input type="hidden" name="currentPage" value="${currentPage}"
					id="currentPageNo">
				<%--</nav>--%>
			</div>
		</div>
	</section>
	<jsp:include page="templates/pageScript.jsp" />
</body>
</html>