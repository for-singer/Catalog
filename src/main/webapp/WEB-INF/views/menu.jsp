<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="req" value="${pageContext.request.servletPath}" />
<c:set var="relURL" value='${fn:split(req, "/")}' />
<c:set var="viewName" value="${relURL[fn:length(relURL)-1]}" />
<c:set var="viewName" value="${fn:replace(viewName, '.jsp', '')}" />

<ul class="nav nav-pills">
    <c:choose>
        <c:when test="${viewName=='index'}">
            <li class="active"><a href="${contextPath}/">Main</a></li>
        </c:when>    
        <c:otherwise>
            <li><a href="${contextPath}/">Main</a></li>
        </c:otherwise>
    </c:choose>
    <c:choose>
        <c:when test="${viewName=='upload'}">
            <li class="active"><a href="${contextPath}/upload">Upload</a></li>
        </c:when>    
        <c:otherwise>
            <li><a href="${contextPath}/upload">Upload</a></li>
        </c:otherwise>
    </c:choose>
    <c:choose>
        <c:when test="${viewName=='download'}">
            <li class="active"><a href="${contextPath}/download">Download</a></li>
        </c:when>    
        <c:otherwise>
            <li><a href="${contextPath}/download">Download</a></li>
        </c:otherwise>
    </c:choose>
</ul>