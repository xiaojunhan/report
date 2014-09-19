package com.youge.report.model;

import java.io.Serializable;
import java.util.List;

public class ReportInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6923705607190985164L;
	private PageInfo pageInfo;
	private List<String[]> reportList;
	public PageInfo getPageInfo() {
		return pageInfo;
	}
	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
	public List<String[]> getReportList() {
		return reportList;
	}
	public void setReportList(List<String[]> reportList) {
		this.reportList = reportList;
	}
	@Override
	public String toString() {
		return "ReportInfo [pageInfo=" + pageInfo + ", reportList="
				+ reportList + "]";
	}
}
