package com.youge.report.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.youge.report.myreport.CoopDayReport;
import com.youge.report.service.ReportService;

public class ReportServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2322888475138310285L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String report = req.getParameter("report");
		ReportService rs = getReportService(report);
		List<?> list = rs.getList();
		req.setAttribute("list",list);
		req.getRequestDispatcher("/WEB-INF/jsp/report.jsp").forward(req, resp);
	}
	
	private ReportService getReportService(String report){
		if("test".equals(report)){
			return new CoopDayReport();
		}
		return null;
	}

}
