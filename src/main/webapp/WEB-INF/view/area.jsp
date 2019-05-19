<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>List All Areas</title>
</head>

<body class="my-login-page">
<jsp:include page="templates/header.jsp"/>
<section class="h-100">
    <div class="container h-100">
        <div class="row justify-content-md-center">
            <div class="card">
                <div class="card-header">
                    <h4 class="float-left">List of Areas</h4>
                    <ul class="float-right">
                        <li>
                            <form action="/areaSearchBox" class="form-inline my-2 my-lg-0 ">
                                <input class="form-control mr-sm-2" name="searchTerm" type="search" placeholder="Search"
                                       aria-label="Search">
                                <input type="hidden" name="page" value="0"/>
                                <input type="hidden" name="size" value="${maxTraySize}"/>
                                <input class="btn btn-outline-primary my-2 my-sm-0" value="Search" type="submit">
                            </form>
                        </li>
                        <li class="text-right">
                            <a href="/areaAdvanceSearch">Advanced Search</a>
                        </li>
                    </ul>
                </div>
                <div class="card card-body table-responsive">
                    <c:choose>
                        <c:when test="${allAreas.totalPages > 0}">
                            <table class="table table-hover">
                                <thead>
                                <tr>
                                	<th>Area Id</th>
                                    <th>Name</th>
                                    <th>Name 2</th>
                                    <th>Area Code</th>
                                    <th>LCO Code</th>
                                    <th>LCO Name</th>
                                    <th colspan="2">Action</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="area" items="${allAreas.content}">
                                    <tr>
                                        <td>
                                            <label>${area.getId()}</label>
                                        </td>
                                        <td>
                                            <label id="name_${area.getId()}">
                                                    ${area.getName()}
                                            </label>
                                            <input required type="text" name="name" class="form-control"
                                                   value="${area.getName()}"
                                                   style="display: none;"
                                                   id="text_name_${area.getId()}">
                                        </td>
                                        <td>
                                            <label id="name2_${area.getId()}">
                                                    ${area.getName2()}
                                            </label>
                                            <input required type="text" name="name2" class="form-control"
                                                   value="${area.getName2()}"
                                                   style="display: none;"
                                                   id="text_name2_${area.getId()}">
                                        </td>
                                        <td>
                                            <label id="areaCode_${area.getId()}">
                                                    ${area.getAreaCode()}
                                            </label>
                                            <input required type="text" name="areaCode" class="form-control"
                                                   value="${area.getAreaCode()}"
                                                   style="display: none;"
                                                   id="text_areaCode_${area.getId()}">
                                        </td>
                                        <td>
                                            <label id="lcoCode_${area.getId()}">
                                                    ${area.getLcoCode()}
                                            </label>
                                            <input required type="text" name="lcoCode" class="form-control"
                                                   value="${area.getLcoCode()}"
                                                   style="display: none;"
                                                   id="text_lcoCode_${area.getId()}">
                                        </td>
                                        <td>
                                            <label id="lcoName_${area.getId()}">
                                                    ${area.getLcoName()}
                                            </label>
                                            <input required type="text" name="lcoName" class="form-control"
                                                   value="${area.getLcoName()}"
                                                   style="display: none;"
                                                   id="text_lcoName_${area.getId()}">
                                        </td>
                                        <td>
                                            <a href="/updateArea" id="update_${area.getId()}" class="updateAreaData"
                                               onclick="event.preventDefault();"><i class="fa fa-edit"></i></a>
                                            <a href="/saveArea" id="saveArea_${area.getId()}" class="saveData"
                                               onclick="event.preventDefault();saveData(${area.getId()});"
                                               style="display: none;"><i class="fa fa-save"></i></a>
                                        </td>
                                        <td><a href="/deleteArea/${area.getId()}" class="deleteData"><i
                                                class="fa fa-trash"></i></a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>

                            </table>
                        </c:when>
                        <c:otherwise>
                            <h5>No users Found... Search again!</h5>
                        </c:otherwise>
                    </c:choose>

                </div>
            </div>
            <c:if test="${allUsers.totalPages > 0}">
                <nav aria-label="Page navigation example" style="margin:auto;">
                    <ul class="pagination">
                        <c:set var="prev" value="0"/>
                        <c:if test="${param.page > 0}">
                            <c:set var="prev" value="${param.page -1}"/>
                        </c:if>
                        <c:if test="${allUsers.totalPages > 0}">
                            <li class='page-item <c:if test="${empty param.page || param.page eq 0}">disabled</c:if>'>
                                <a class="page-link" href="/?page=${prev}&size=${maxTraySize}">Prev</a></li>
                        </c:if>
                        <c:forEach var="i" begin="0" end="${allUsers.totalPages -1}">
                            <li class='page-item <c:if test="${param.page eq i || (empty param.page && i eq 0)}">active</c:if>'>
                                <a class="page-link" href="/?page=${i}&size=${maxTraySize}">${i+1}</a>
                            </li>
                        </c:forEach>
                        <c:if test="${allUsers.totalPages > 0}">
                            <li class='page-item <c:if test="${allUsers.totalPages <= (param.page + 1)}">disabled</c:if>'>
                                <a class="page-link" href="/?page=${param.page + 1}&size=${maxTraySize}">Next</a>
                            </li>
                        </c:if>
                    </ul>
                </nav>
            </c:if>
            <input type="hidden" name="currentPage" value="${currentPage}" id="currentPageNo">
            <%--</nav>--%>
        </div>
    </div>
</section>
<jsp:include page="templates/pageScript.jsp"/>
</body>
</html>