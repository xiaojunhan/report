package com.youge.report.myreport.wuye;

import java.util.Date;

import com.youge.report.model.ReportInfo;
import com.youge.report.service.ReportService;
import com.youge.report.util.DateUtil;

public class ReportWuYeTotal extends ReportService{
	private static final String[] head = {"日期","数量","积分"};
	@Override
	protected void init() throws Exception {
		String sql ="select report_date,num,fee from wuye_report_new where comp_id = ?  and report_date >= ? and report_date <= ? order by report_date asc";
		String compId = getRequest().getParameter("compId");
		String startDate = getRequest().getParameter("startDate");
		String endDate = getRequest().getParameter("endDate");
		Object[] params = {compId,startDate,endDate};
		String countSql ="select count(*) from wuye_report_new where comp_id = ?  and report_date >= ? and report_date <= ? ";
		doSql(sql,params,countSql,params);
		
	}

	@Override
	public String getTitle() {
		String startDate = getRequest().getParameter("startDate");
		String endDate = getRequest().getParameter("endDate");
		return startDate+"到"+endDate+"总计";
	}

	@Override
	public String[] getThead() {
		return head;
	}

	@Override
	protected ReportInfo getReportInfoOpr() {
		ReportInfo ri = getReportInfoDb();
		if(ri!=null && ri.getReportList()!=null && ri.getReportList().size()>0){
			String path = getRequest().getContextPath();
			String startDate = getRequest().getParameter("startDate");
			String endDate = getRequest().getParameter("endDate");
			String compId = getRequest().getParameter("compId");
			String token = getRequest().getParameter("token");
			StringBuilder detail = new StringBuilder();
			detail.append(path).append("/report.do?report=report_wuye_detail&startDate=")
			.append(startDate)
			.append("&endDate=").append(endDate)
			.append("&compId=").append(compId)
			.append("&token=").append(token);
			for(String[] arr:ri.getReportList()){
				if(arr!=null){
					arr[0] = getAlink(detail.toString(), arr[0]);
				}
			}
		}
		return ri;
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
