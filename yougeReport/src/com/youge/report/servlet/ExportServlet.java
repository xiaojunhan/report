package com.youge.report.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.youge.report.exception.DbException;
import com.youge.report.service.ReportService;
import com.youge.report.service.ReportServiceFactory;
import com.youge.report.util.Stringutil;

public class ExportServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5876152456587859819L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String report = req.getParameter("report");
		/**
		 * 可以按页导出数据
		 */
		String spage = req.getParameter("spage");
		String epage = req.getParameter("epage");
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
		if(Stringutil.isEmpty(spage)&&Stringutil.isEmpty(epage)){
			rs.setNeedCut(false);
		}
		try {
			rs.init1(req);
		} catch (DbException e) {
			//访问异常
			req.getRequestDispatcher("/WEB-INF/jsp/common/error.jsp").forward(req, resp);
			return;
		}
//		req.setAttribute("title",rs.getTitle());
//		req.setAttribute("thead",rs.getThead());
//		if(rs.getThead()!=null){
//			req.setAttribute("collen",rs.getThead().length);
//		}else{
//			req.setAttribute("collen",1);
//		}
//		req.setAttribute("tbody",rs.getTbody());
//		req.setAttribute("tfoot",rs.getTfoot());
//		req.setAttribute("footer",rs.getFooter());
		File file = createExcel(rs);
		String fileName = rs.getTitle();
        resp.setContentType("application/octet-stream");
        resp.addHeader("Content-Disposition", "attachment; filename=" + fileName);
        resp.addHeader("Content-Length" ,Long.toString(file.length()));  
        ServletOutputStream servletOutputStream=resp.getOutputStream();
        FileInputStream fileInputStream=new FileInputStream(file);
        BufferedInputStream bufferedInputStream=new BufferedInputStream(fileInputStream);
        int size=0;
        byte[] b=new byte[4096];
        while ((size=bufferedInputStream.read(b))!=-1) {
            servletOutputStream.write(b, 0, size);
        }
        servletOutputStream.flush();
        servletOutputStream.close();
        bufferedInputStream.close();
	}
	
	public File createExcel(ReportService rs){
		return new File("/tmp/testjxlwrite.xls");
	}
}
