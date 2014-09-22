package com.youge.report.myreport.wuye;

import java.util.Date;

import com.youge.report.model.ReportInfo;
import com.youge.report.service.ReportService;
import com.youge.report.util.DateUtil;

public class ReportWuYeDetail extends ReportService{
	private static final String[] head = {"时间","快递单号","收件人","操作员"};
	@Override
	protected void init() throws Exception {
		String sql ="select start_time,awb,phone,wuye_user_phone from wuye_express  where comp_id = ? and start_time >= ? and start_time <=?";
		String compId = getRequest().getParameter("compId");
		String startDate = getRequest().getParameter("startDate");
		String endDate = getRequest().getParameter("endDate");
		startDate = startDate+" 00:00:00";
		endDate = endDate+" 23:59:59";
		Object[] params = {compId,startDate,endDate};
		String countSql ="select count(*) from wuye_express where comp_id = ? and start_time >= ? and start_time <=?";
		doSql(sql,params,countSql,params);
	}

	@Override
	public String getTitle() {
		String startDate = getRequest().getParameter("startDate");
		String endDate = getRequest().getParameter("endDate");
		return startDate+"到"+endDate+"明细";
	}

	@Override
	public String[] getThead() {
		return head;
	}

	@Override
	protected ReportInfo getReportInfoOpr() {
		return  getReportInfoDb();
	}

	@Override
	public String[] getTfoot() {
		return null;
	}

	@Override
	public String getFooter() {
		return DateUtil.format(new Date());
	}
	@Override
	public boolean isCache() {
		return false;
	}
	@Override
	public boolean isReadonly() {
		return false;
	}

}
