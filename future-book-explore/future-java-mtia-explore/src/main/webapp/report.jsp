<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="cn.locusc.mtia.chapter3.codelist.case02.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Statistics</title>
</head>
<body>
Request Count:<%= Indicator321.getInstance().getRequestCount() %><br>
Success Count:<%= Indicator321.getInstance().getSuccessCount() %><br>
Failure Count:<%= Indicator321.getInstance().getFailureCountCount() %><br>
</body>
</html>