package com.youge.report.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.youge.report.exception.DbException;
import com.youge.report.service.ReportService;
import com.youge.report.service.ReportServiceFactory;
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
	 * report 报表名   必须 
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
		
		try {
			rs.init(req);
		} catch (Exception e) {
			//访问异常
			req.setAttribute(MESSAGE, "服务器异常，请稍后再试");
			req.getRequestDispatcher("/WEB-INF/jsp/common/error.jsp").forward(req, resp);
			return;
		}

		req.setAttribute("title",rs.getTitle());
		req.setAttribute("thead",rs.getThead());
		if(rs.getThead()!=null){
			req.setAttribute("collen",rs.getThead().length);
		}else{
			req.setAttribute("collen",1);
		}
		try {
			req.setAttribute("tbody",rs.getTbodyWithCache());
		} catch (Exception e) {
			//访问异常
			req.setAttribute(MESSAGE, "服务器异常，请稍后再试");
			req.getRequestDispatcher("/WEB-INF/jsp/common/error.jsp").forward(req, resp);
			return;
		}
		if(rs.getTfoot()!=null){
			if(!rs.isNeedCut() || rs.getPage()==rs.getPageCount()){//不需要分页 或 分页时最后一页
				req.setAttribute("tfoot",rs.getTfoot());
			}
		}
		req.setAttribute("footer",rs.getFooter());
		
		req.setAttribute("count",rs.getTotalCount());//总记录条数
		req.setAttribute("pageCount",rs.getPageCount());//总页数
		req.setAttribute("page",rs.getPage());//当前页
		
		req.getRequestDispatcher(rs.getJsp()).forward(req, resp);
	}
}
