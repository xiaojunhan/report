package com.youge.report.myreport.coop;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

import com.youge.report.model.ReportInfo;
import com.youge.report.service.ReportService;
import com.youge.report.util.DateUtil;
/**
 * 合作网点日报表
 * @author hanxj
 *
 */
public class ReportCoopDay extends ReportService{
	private static final String[] head = {"投件数量,20","投件积分,20","取件数量,20","取件积分(预充),20","取件积分(投币),20"};
	@Override
	protected void init() throws Exception {
		String sql ="select sum(tou_count),sum(tou_ofee),sum(qu_count),sum(qu_ofee),sum(qu_cfee) from coop_report where coop_comp_id = ?  and report_date = ?";
		String compId = getRequest().getParameter("compId");
		String date = getRequest().getParameter("date");
		Object[] params = {compId,date};
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
		return compName+"日报表"+date;
	}

	@Override
	public String[] getThead() {
		return head;
	}

	@Override
	protected ReportInfo getReportInfoOpr() {
		ReportInfo ri = getReportInfoDb();
		if(ri==null){
			return null;
		}
		String path = getRequest().getContextPath();
		String date = getRequest().getParameter("date");
		String compName = getRequest().getParameter("compName");
		String compId = getRequest().getParameter("compId");
		String token = getRequest().getParameter("token");
		StringBuilder tou = new StringBuilder();
		tou.append(path).append("/report.do?report=report_coop_tou&date=")
		.append(date)
		.append("&compName=").append(compName)
		.append("&compId=").append(compId)
		.append("&token=").append(token);
		
		StringBuilder qu = new StringBuilder();
		qu.append(path).append("/report.do?report=report_coop_qu&date=")
		.append(date)
		.append("&compName=").append(compName)
		.append("&compId=").append(compId)
		.append("&token=").append(token);
		List<String[]> list = ri.getReportList();
		if(list!=null && list.size()>0){
			for(String[] arr:list){
				arr[0] = getAlink(tou.toString(), arr[0]);
				arr[2] = getAlink(qu.toString(), arr[2]);
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
