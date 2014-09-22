package com.youge.report.myreport.coop;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

import com.youge.report.model.ReportInfo;
import com.youge.report.service.ReportService;
import com.youge.report.util.DateUtil;

public class ReportCoopTouDetail extends ReportService{
	
	private static final String[] head = {"网点","箱号","格号","大小","投件时间","快递单号","快递员","积分"};
	@Override
	protected void init() throws Exception {
		String sql = "select branch_name,box_no,gird_show_no,size,start_time,awb,kphone,kscore "+
		"from rent_info  "+
		"where "+
		"branch_id in (select branch_id "+
		"from coop_branch where comp_id = ?)  "+
		"and start_time >= ? and start_time <=?";
		String compId = getRequest().getParameter("compId");
		String date = getRequest().getParameter("date");
		String startTime = date+" 00:00:00";
		String endTime = date+" 23:59:59";
		Object[] params = {compId,startTime,endTime};
		doSql(sql,params);
	}

	@Override
	public String getTitle() {
		String compName = getRequest().getParameter("compName");
		try {
			compName = URLDecoder.decode(compName,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String date = getRequest().getParameter("date");
		return compName+date+"投件明细";
	}
	@Override
	public String[] getThead() {
		return head;
	}
	@Override
	protected ReportInfo getReportInfoOpr() {
		return getReportInfoDb();
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
