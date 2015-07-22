<%@page language="java" contentType="text/html charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Catalog</title>
        
        <meta name="viewport" content="initial-scale=1, maximum-scale=1">
        <link rel="stylesheet" href="${contextPath}/webjars/bootstrap/3.2.0/css/bootstrap.min.css">
        <script type="text/javascript" src="${contextPath}/webjars/bootstrap/3.2.0/js/bootstrap.min.js"></script>
        <script type="text/javascript" src="${contextPath}/webjars/jquery/2.1.1/jquery.min.js"></script>
    
        <link href="<c:url value="/resources/css/main.css" />" rel="stylesheet">
    </head>
    <body>
        <div class="container-fluid">
            <%@ include file="menu.jsp" %>

            <h1>CD list</h1>
            <c:choose>
                <c:when test="${not empty list}">
                    <table class="table table-bordered">
                        <thead>
                            <tr>
                                <th>TITLE</th>
                                <th>ARTIST</th>
                                <th>COUNTRY</th>
                                <th>COMPANY</th>
                                <th>PRICE</th>
                                <th>YEAR</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="cd" items="${list.getPageList()}">
                                <tr>
                                    <td>${cd.getTitle()}</td>
                                    <td>${cd.getArtist()}</td>
                                    <td>${cd.getCountry()}</td>
                                    <td>${cd.getCompany()}</td>
                                    <td>${cd.getPrice()}</td>
                                    <td>${cd.getYear()}</td>                            
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <!-- Pagination Bar -->
                    <div class="bs-example">
                        <ul class="pagination">
                            <c:if test="${list.getPage() > 2}">
                                <li><a href="${contextPath}/page/1">&laquo;</a>&nbsp;</li>
                            </c:if>

                            <c:if test="${! list.isFirstPage()}">
                                <li><a href="${contextPath}/page/${(list.getPage())}">&lt;</a>&nbsp;</li>
                            </c:if>

                            <c:if test="${list.getPage() > 0}">
                                <c:choose>
                                    <c:when test="${(list.getPage() - 3) < 1}">
                                        <c:set var="start" value="1" scope="page"/>                
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="start" value="${list.getPage() - 2}" scope="page"/> 
                                    </c:otherwise>
                                </c:choose>
                                <c:set var="end" value="${((list.getPage()))}" scope="page"/>    
                                <c:forEach var="i" begin="${start}" end="${end}">
                                    <li><a href="${contextPath}/page/${i}">${i}</a>&nbsp;</li>
                                </c:forEach>
                            </c:if>                        

    <!--                        <li>${list.getPage() + 1}&nbsp;</li>-->
                            <li><a href="${contextPath}/page/${(list.getPage() + 2)}" class="active">${list.getPage() + 1}</a>&nbsp;</li>

                            <c:if test="${list.getPage() < list.getPageCount() - 1}">
                                <c:set var="start" value="${list.getPage() + 2}" scope="page"/>
                                <c:choose>
                                    <c:when test="${(list.getPage() + 3) < list.getPageCount()}">
                                        <c:set var="end" value="${list.getPage() + 4}" scope="page"/>                
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="end" value="${list.getPageCount()}" scope="page"/> 
                                    </c:otherwise>
                                </c:choose>   
                                <c:forEach var="i" begin="${start}" end="${end}">
                                    <li><a href="${contextPath}/page/${i}">${i}</a>&nbsp;</li>
                                </c:forEach>
                            </c:if>                                               

                            <c:if test="${! list.isLastPage()}">
                                <li><a href="${contextPath}/page/${(list.getPage() + 2)}">&gt;</a>&nbsp;</li>
                            </c:if>

                            <c:if test="${list.getPage() <= (list.getPageCount()-3)}">
                                <li><a href="${contextPath}/page/${list.getPageCount()}">&raquo;</a>&nbsp;</li>
                            </c:if>
                        </ul>
                    </div>

                </c:when>
                <c:otherwise>
                    <p>Catalog is empty</p>
                </c:otherwise>    

            </c:choose>
        </div>            
    </body>
</html>
