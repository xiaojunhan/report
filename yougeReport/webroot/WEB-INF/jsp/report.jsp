<%@ page language="java" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/common/base.jsp"%>
<!DOCTYPE html>
<html>
    <head>
    <meta charset="UTF-8">
    <title>${title}</title>
    <link href="${path}/css/one.css" rel="stylesheet">
    <link href="${path}/css/report.css" rel="stylesheet">
    <link href="${path}/css/layer.css" rel="stylesheet">
    <script type="text/javascript">
        document.createElement("section");
    	function showLayer(){
    		document.getElementById("layermbox").style.display="";
    	}
    	function exportreport(){
    		document.getElementById("layermbox").style.display="none";
    		document.getElementById("exportform").submit();
    	}
    	function closeLayer(){
    		document.getElementById("layermbox").style.display="none";
    	}
    	function showHeight(){
    		var height = 0;
    		//if(document.documentElement){
    		//	height = document.documentElement.clientHeight;
    		//}else{
    			height = document.body.clientHeight;
    		//}
    		//alert("height="+height);
    		var webserver = document.getElementById("webserver").value;
    		if(webserver!=null&&webserver!=""){
    			document.getElementById("proxyFrame").src = webserver+"/html/proxy.html#"+height;
    		}
    	}
    </script>
    </head>
    <body onload="showHeight()">
    <div class="wrapper">
    	<div class="title">${title}</div>
    	<a href="javascript:showLayer()" style="float: right;position: relative;margin-top: -40px"><img src="${path}/imgs/excel.png"/></a>
    </div>
    <div class="wrapper">
        <table class="ui-table">
	        	<thead>
	        		<c:if test="${! empty thead}">
	        		<tr>
	        		 	<c:forEach items="${thead}" var="th">
	        				<th style="font-weight: bold;" 
	        				<c:if test="${!empty th.width}">
	        					width="${th.width}%"
	        				</c:if>
	        				>${th.name}</th>
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
	        				<td colspan="${collen}" align="center">没有查询到记录</td>
	        			</tr>
	        		</c:if>
	        		
	        		<c:if test="${! empty tfoot}">
		        		<tr>
		        		 	<c:forEach items="${tfoot}" var="td">
		        				<td>${td}</td>
		        			</c:forEach>
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
	
	<div id="layermbox" class="layermbox layermbox0 layermshow" style="display: none;">
		<div class="layermmain">
			<section>
				<div class="layermchild layermanim">
					<h3 style="">提示</h3>
					<div class="layermcont">确定要导出该报表吗?</div>
					<div class="layermbtn">
						<span onclick="closeLayer()">取消</span>
						<span onclick="exportreport()">确定</span>
					</div>
				</div>
			</section>
		</div>
	</div>
	<input id="webserver" type="hidden" value="${server}">
	<iframe id="proxyFrame" width="0" height="0" style="display: none" src=""></iframe>
    </body>
</html>