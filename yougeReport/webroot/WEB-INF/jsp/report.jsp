<%@ page language="java" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/common/base.jsp"%>
<!DOCTYPE html>
<html>
    <head>
    <meta charset="UTF-8">
    <title>${title}</title>
    <link href="${path}/css/one.css" rel="stylesheet">
    <link href="${path}/css/report.css" rel="stylesheet">
    <script type="text/javascript">
    	function exportreport(){
    		if(window.confirm("确定要导出该报表吗?")){
    			alert("s");
    		}
    	}
    </script>
    </head>
    <body>
    <div class="wrapper">
    	<div class="title">${title}</div>
    	<a href="javascript:exportreport()" style="float: right;position: relative;margin-top: -40px"><img src="${path}/imgs/excel.png"/></a>
    </div>
    <div class="wrapper">
        <table class="ui-table">
	        	<thead>
	        		<c:if test="${! empty thead}">
	        		<tr>
	        		 	<c:forEach items="${thead}" var="th">
	        				<th style="font-weight: bold;">${th}</th>
	        			</c:forEach>
	        		</tr>
	        		</c:if>
	        	</thead>
	        	<tbody>
	        		<c:if test="${! empty tbody}">
		        			<c:forEach items="${tbody}" var="tb">
		        				<tr>
									<c:forEach items="${tb}" var="td">
				        				<td>${td}</td>
				        			</c:forEach>
								</tr>
			        		</c:forEach>
	        		</c:if>
	        		<c:if test="${empty tbody}">
	        			<tr>
	        				<td colspan="${collen}">没有查询到记录</td>
	        			</tr>
	        		</c:if>
        		</tbody>
        </table>
    </div>
	<%@include file="/WEB-INF/jsp/common/page.jsp"%>
	<div class="fn-clear"></div>
    <div class="wrapper"><div class="footer">${footer}</div></div>
	    <form id="exportform" action="${path}/export.do" method="get" style="display: none;">
			<c:forEach var="pageParameter" items="${param}">
				<input type="hidden" name="${pageParameter.key}" value="${pageParameter.value}">
	      	</c:forEach>
		</form>
    </body>
</html>