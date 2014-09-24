package com.youge.report.myreport.coop;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.youge.report.model.ReportInfo;
import com.youge.report.service.ReportService;
import com.youge.report.util.DateUtil;

public class ReportCoopQuDetail extends ReportService{
	private static final String[] head = {"网点","箱号","格号","取件时间","快递单号","取件人","积分","状态"};
	@Override
	protected void init() throws Exception {
		String sql="select branch_name,box_no,gird_show_no,"+
		"end_time,"+
		"awb,kphone,uphone,uscore,status,ust "+
		"from rent_info   "+
		"where "+
		"branch_id in (select branch_id "+
		"from coop_branch where comp_id = ?)  "+
		"and end_time >= ? and end_time <=?";
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
		return compName+date+"取件明细";
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
		List<String[]> tempList = null;
		List<String[]> list = ri.getReportList();
		if(list!=null && list.size()>0){
			tempList = new ArrayList<String[]>(list.size()<<2);
			for(String[] arr:list){
				String status = arr[8];
				String ust = arr[9];
				String[] tempArr = new String[head.length];
				tempArr[0]=arr[0];
				tempArr[1]=arr[1];
				tempArr[2]=arr[2];
				if(arr[3]!=null && arr[3].length()>10){
					tempArr[3]=arr[3].substring(10);//取件时间
				}
				tempArr[4]=arr[4];
				if("2".equals(status)){
					tempArr[5]=arr[6];
				}
				if("3".equals(status)){
					tempArr[5]=arr[5];
				}
				if("2".equals(status)){
					if("1".equals(ust)){
						tempArr[6]=arr[7]+"(预充)";
					}else if("2".equals(ust)){
						tempArr[6]=arr[7]+"(投币)";
					}else{
						tempArr[6]="0";
					}
				}else{
					tempArr[6]="0";
				}
				if("2".equals(status)){
					tempArr[7]="用户取走";
				}
				if("3".equals(status)){
					tempArr[7]="快递员取回";
				}
				if("4".equals(status)){
					tempArr[7]="系统取回";
				}
				tempList.add(tempArr);
			}
		}
		ri.setReportList(tempList);
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
