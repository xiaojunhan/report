package com.youge.report.myreport;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.youge.report.exception.DbException;
import com.youge.report.service.ReportService;
import com.youge.report.util.DateUtil;

public class CoopDayReport extends ReportService{
	
	private static final String[] head = {"ID","用户名","时间"};
	@Override
	public void init(HttpServletRequest req) throws DbException {
		//check auth
		String sql = "select id,address,ntime from test_report where id > ?";
		String countsql = "select count(*) from test_report where id > ?";
		Object[] params = {1};
		doSql(sql,params,countsql,params);
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
	public List<?> getTbody() {
		return getTbodyList();
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
	@Override
	public boolean checkAuth(HttpServletRequest req){
		return true;
	}
}
