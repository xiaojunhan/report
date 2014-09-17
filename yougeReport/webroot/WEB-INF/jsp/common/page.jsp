<%@ page contentType="text/html;charset=UTF-8" language="java"%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${pageCount>1}">
<script type="text/javascript">
function goto_page(page){
	document.getElementById('page').value=page;
	document.getElementById("form").submit();
}
function goto_page1(){
	var page = document.getElementById('some_no').value;
	page = page.replace(/(^\s*)|(\s*$)/g, "");
	if(/^\d+$/.test(page)){
		goto_page(page);
	}
}
</script>
<div class="wrapper">
	 <div class="ui-paging" style="float: right;margin: 5px 0">
		    <span class="ui-paging-info">第<span class="ui-paging-bold">${page}/${pageCount}</span>页</span>
		    <c:if test="${page<=1 }">
			    <span class="ui-paging-prev">
			        第一页
			    </span>
			    <span class="ui-paging-prev">
			        <i class="iconfont" title="左三角形">&#xF039;</i> 上一页
			    </span>
		    </c:if>
		    <c:if test="${page > 1 }">
			    <a href="javascript:goto_page(1)" class="ui-paging-prev">
			        第一页
			    </a>
			    <a href="javascript:goto_page(${page-1 <=0 ? 1 : page-1})" class="ui-paging-prev">
			        <i class="iconfont" title="左三角形">&#xF039;</i> 上一页
			    </a>
		    </c:if>
		    <c:if test="${page < pageCount}">
			    <a href="javascript:goto_page(${page+1 >=pageCount ? pageCount : page+1})" class="ui-paging-next">
			        下一页 <i class="iconfont" title="右三角形">&#xF03A;</i>
			    </a>
			     <a href="javascript:goto_page(${pageCount})" class="ui-paging-prev">
			        最后页
			    </a>
		    </c:if>
		     <c:if test="${page >= pageCount}">
			    <span class="ui-paging-next">
			        下一页 <i class="iconfont" title="右三角形">&#xF03A;</i>
			    </span>
			     <span class="ui-paging-prev">
			        最后页
			    </span>
		    </c:if>
		    <span class="ui-paging-which"><input id="some_no" value="" type="text"></span>
   			<a class="ui-paging-info ui-paging-goto" href="javascript:goto_page1()">跳转</a>
	</div>
	<form id="form" action="${path}/report.do" method="get" style="display: none;">
		<input type="hidden" id="page" name="page" value="${page}">
		<c:forEach var="pageParameter" items="${param}">
		    <c:if test="${pageParameter.key!='page'}"> 
				<input type="hidden" name="${pageParameter.key}" value="${pageParameter.value}">
			</c:if>
      	</c:forEach>
	</form>
</div>
</c:if>