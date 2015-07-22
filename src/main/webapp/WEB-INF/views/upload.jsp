<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Catalog - Upload</title>
        
        <meta name="viewport" content="initial-scale=1, maximum-scale=1">
        <link rel="stylesheet" href="webjars/bootstrap/3.2.0/css/bootstrap.min.css">
        <script type="text/javascript" src="webjars/bootstrap/3.2.0/js/bootstrap.min.js"></script>
        <script type="text/javascript" src="webjars/jquery/2.1.1/jquery.min.js"></script>
    
        <script type="text/javascript" src="<c:url value="/resources/js/bootstrap-filestyle.min.js" />"> </script>
        <script type="text/javascript">$(":file").filestyle({icon: false});</script>
        
        <link href="<c:url value="/resources/css/main.css" />" rel="stylesheet">
    </head>
    <body>
        <div class="container-fluid">
            <%@ include file="menu.jsp" %>

            <h1>Upload</h1>
            <form method="POST" enctype="multipart/form-data" action="upload-file">
                <div class="input-div"><label>Select file to upload: <input type="file" name="file" class="filestyle" data-icon="false" /></label></div><br />
                <input type="submit" value="Upload" class="btn btn-default" />
            </form>
        </div>
    </body>
</html>
