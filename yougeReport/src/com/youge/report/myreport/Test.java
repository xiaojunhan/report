package com.youge.report.myreport;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.youge.report.model.ReportInfo;
import com.youge.report.service.ReportService;
import com.youge.report.util.DateUtil;

public class Test extends ReportService{
	private static final String[] head = {"ID","用户名","时间"};
	@Override
	public boolean checkAuth(HttpServletRequest req){
		return true;
	}
	
	@Override
	public void init() throws Exception {
		HttpServletRequest req = getRequest();
		String sql = "select id,address,ntime from test_report where id > ?";
		String countsql = "select count(*) from test_report where id > ?";
		Object[] params = {1};
		doSql(req,sql,params,countsql,params);
	}
	@Override
	public String getTitle() {
		return "网点日报 2014-09-09";
	}
	@Override
	public String[] getThead() {
		return head;
	}
	/**
	 * 对查询出的结果 做处理
	 * 若不需要处理 直接返回
	 * @return
	 */
	@Override
	public ReportInfo getReportInfoOpr(){
		return getReportInfoDb();
	}
	@Override
	public String[] getTfoot() {
		//最后一页 才有footer
		return null;
	}
	@Override
	public String getFooter() {
		return DateUtil.format(new Date());
	}

}
