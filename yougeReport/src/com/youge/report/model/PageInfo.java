package com.youge.report.model;

import java.io.Serializable;

public class PageInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8091409878451146763L;
	private int page;//当前页
	private int pageCount;//总页数
	private int totalCount;//总记录条数
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	@Override
	public String toString() {
		return "PageInfo [page=" + page + ", pageCount=" + pageCount
				+ ", totalCount=" + totalCount + "]";
	}
}
