package com.youge.report.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.youge.report.exception.DbException;
import com.youge.report.myreport.CoopDayReport;
import com.youge.report.service.ReportService;
import com.youge.report.service.ReportServiceFactory;

public class ReportServlet extends HttpServlet{

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
			req.getRequestDispatcher("/WEB-INF/jsp/common/error.jsp").forward(req, resp);
			return;
		}
		if(!rs.checkAuth(req)){
			//没有访问权限
			req.getRequestDispatcher("/WEB-INF/jsp/common/error.jsp").forward(req, resp);
			return;
		}
		
		try {
			rs.init1(req);
		} catch (DbException e) {
			//访问异常
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
		req.setAttribute("tbody",rs.getTbody());
		req.setAttribute("tfoot",rs.getTfoot());
		req.setAttribute("footer",rs.getFooter());
		
		req.setAttribute("count",rs.getTotalCount());//总记录条数
		req.setAttribute("pageCount",rs.getPageCount());//总页数
		req.setAttribute("page",rs.getPage());//当前页
		
		req.getRequestDispatcher(rs.getJsp()).forward(req, resp);
	}
}
