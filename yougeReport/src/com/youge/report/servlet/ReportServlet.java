package com.youge.report.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.youge.report.model.PageInfo;
import com.youge.report.model.ReportInfo;
import com.youge.report.model.Thead;
import com.youge.report.service.ReportService;
import com.youge.report.service.ReportServiceFactory;
import com.youge.report.util.PropertiesUtil;
import com.youge.report.util.StringUtil;
/**
 * 报表主servlet
 * @author hanxj
 *
 */
public class ReportServlet extends HttpServlet{
	private static final String MESSAGE = "MESSAGE"; 
	/**
	 * 
	 */
	private static final long serialVersionUID = -2322888475138310285L;
	/**
	 * page   当前页数 非必须 默认1
	 * psize  每页记录 非必须 默认15
	 * report 报表名   必须  英文、数字
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String report = req.getParameter("report");
		ReportService rs = ReportServiceFactory.getInstance(report);
		if(rs==null){
			//没有该报表
			req.setAttribute(MESSAGE, "该报表不存在");
			req.getRequestDispatcher("/WEB-INF/jsp/common/error.jsp").forward(req, resp);
			return;
		}
		if(!rs.checkAuth(req)){
			//没有访问权限
			req.setAttribute(MESSAGE, "没有访问该报表的权限，您可以刷新页面再次尝试");
			req.getRequestDispatcher("/WEB-INF/jsp/common/error.jsp").forward(req, resp);
			return;
		}
		rs.setRequest(req);
		try {
			rs.initReportInfo();
		} catch (Exception e) {
			//访问异常
			req.setAttribute(MESSAGE, "服务器异常，请稍后再试");
			req.getRequestDispatcher("/WEB-INF/jsp/common/error.jsp").forward(req, resp);
			return;
		}

		req.setAttribute("title",rs.getTitle());
		String[] theadArr = rs.getThead();
		if(theadArr!=null && theadArr.length>0){
			Thead[] theadArr1 = new Thead[theadArr.length];
			for(int i=0;i<theadArr.length;i++){
				String[] tempArr = theadArr[i].split(",");
				Thead thead = new Thead();
				if(tempArr.length==2){
					thead.setName(tempArr[0]);
					thead.setWidth(tempArr[1]);
				}
				if(tempArr.length==1){
					thead.setName(tempArr[0]);
				}
				theadArr1[i]=thead;
			}
			req.setAttribute("thead",theadArr1);
		}
		if(rs.getThead()!=null){
			req.setAttribute("collen",rs.getThead().length);
		}else{
			req.setAttribute("collen",1);
		}
		ReportInfo ri = rs.getReportInfo();
		PageInfo pi = new PageInfo();
		if(ri!=null){
			req.setAttribute("tbody",ri.getReportList());
			if(ri.getPageInfo()!=null){
				pi = ri.getPageInfo();
			}
		}
		if(rs.getTfoot()!=null){
			if(!rs.isNeedCut() || pi.getPage()==pi.getPageCount()){//不需要分页 或 分页时最后一页
				req.setAttribute("tfoot",rs.getTfoot());
			}
		}
		req.setAttribute("footer",rs.getFooter());
		
		req.setAttribute("count",pi.getTotalCount());//总记录条数
		req.setAttribute("pageCount",pi.getPageCount());//总页数
		req.setAttribute("page",pi.getPage());//当前页
		

		String server = req.getParameter("server");
		if(StringUtil.isEmpty(server)){
			server = PropertiesUtil.get("WEB_SERVER");
		}
		req.setAttribute("server",server);

		req.getRequestDispatcher(rs.getJsp()).forward(req, resp);
	}
}
