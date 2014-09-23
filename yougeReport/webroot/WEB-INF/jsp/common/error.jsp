<%@ page language="java" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/common/base.jsp"%>
<!DOCTYPE html>
<html>
    <head>
    <meta charset="UTF-8">
    <title>出错啦</title>
    <link href="${path}/css/one.css" rel="stylesheet">
    <link href="${path}/css/report.css" rel="stylesheet">
    </head>
    <body>
    <div class="wrapper" style="margin-top: 200px">
		<div class="ui-tipbox ui-tipbox-error">
		    <div class="ui-tipbox-icon">
		        <i class="iconfont" title="出错">&#xF045;</i>    
		    </div>
		    <div class="ui-tipbox-content-simple">
		        <h3 class="ui-tipbox-title">${empty MESSAGE ? '出错啦' : MESSAGE}</h3>
		    </div>
		</div>
	</div>
    </body>
</html>