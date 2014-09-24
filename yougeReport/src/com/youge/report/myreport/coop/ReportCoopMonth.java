package com.youge.report.myreport.coop;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

import com.youge.report.model.ReportInfo;
import com.youge.report.service.ReportService;
import com.youge.report.util.DateUtil;
import com.youge.report.util.StringUtil;
/**
 * 合作网点月报表
 * @author hanxj
 *
 */
public class ReportCoopMonth extends ReportService{
	private static final String[] head = {"投件数量,20","投件积分,20","取件数量,20","取件积分(预充),20","取件积分(投币),20"};
	@Override
	protected void init() throws Exception {
		String sql ="select sum(tou_count) as tou_count,sum(tou_ofee) as tou_ofee,sum(qu_count) as qu_count,sum(qu_ofee) as qu_ofee,sum(qu_cfee) as qu_cfee from coop_report where coop_comp_id = ?  and report_date like ?";
		String compId = getRequest().getParameter("compId");
		String date = getRequest().getParameter("date");
		date = date+"-__";
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
		return compName+"月报表"+date;
	}

	@Override
	public String[] getThead() {
		return head;
	}

	@Override
	protected ReportInfo getReportInfoOpr() {
		ReportInfo ri = getReportInfoDb();
		if(ri!=null && ri.getReportList()!=null && ri.getReportList().size()>0){
			for(String[] arr:ri.getReportList()){
				if(arr!=null){
					for(int i=0;i<arr.length;i++){
						if(StringUtil.isEmpty(arr[i])){
							arr[i]="0";
						}
					}
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
