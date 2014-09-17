package com.youge.report.service;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.youge.report.Constants;
import com.youge.report.exception.DbException;
import com.youge.report.util.JdbcUtil;
import com.youge.report.util.Stringutil;

public abstract class ReportService {
	private int page;
	private int psize;
	private int pageCount;
	private int totalCount;
	private int spage=-1;//开始页 导出文件时使用
	private int epage=-1;//结束页 导出文件时使用
	private boolean needCut = true;//导出时 不需要分页的情况
	private List<?> tbodyList;
	protected static JdbcUtil jdbcUtil = new JdbcUtil(Constants.DATA_SOURCE_ID);
	protected Map<String,String> getRequestMap(HttpServletRequest request){
		Map<String,String> map = new HashMap<String,String>();
		Enumeration<String> e = request.getParameterNames();
		while(e.hasMoreElements()){
			String name = e.nextElement();
			String value = request.getParameter(name);
			map.put(name, value);
		}
		return map;
	}
	public void init1(HttpServletRequest req) throws DbException{
//		String needcut = req.getParameter("needcut");
//		if("1".equals(needcut)){
//			setNeedCut(true);
			String pageStr = req.getParameter("page");
			String psizeStr = req.getParameter("psize");
			if(Stringutil.isNotEmpty(pageStr)){
				try{
					page = Integer.parseInt(pageStr);
				}catch(Exception e){}
			}
			if(page<1){
				page = 1;
			}
			if(Stringutil.isNotEmpty(psizeStr)){
				try{
					psize = Integer.parseInt(psizeStr);
				}catch(Exception e){}
			}
			if(psize==0){
				psize = 15;
			}
//		}else{
//			setNeedCut(false);
//		}
		init(req);
	}
	
//	public int getSqlCount(String sql,Object... params) throws DbException{
//		return jdbcUtil.getIntp(sql,params);
//	}
	
	public void doSql(String sql,Object[] param1) throws DbException{
		List<String[]> list = jdbcUtil.getListp(sql, String[].class, param1);
		setTbodyList(list);
	}
	
	public void doSql(String sql,Object[] param1,String countSql,Object[] param2) throws DbException{
		if(!needCut){//不需要分页
			doSql(countSql, param1);
			return;
		}
		int count = jdbcUtil.getIntp(countSql,param2);
		setTotalCount(count);
		if(count<=0){
			setTbodyList(null);
			return;
		}
		pageCount = (count % psize == 0) ? count / psize : count / psize + 1;
		if(page>pageCount){
			page = pageCount;
		}
		int start = 0;
		int num = 0;
		if(spage==-1 && epage==-1){
			start = getPsize() * (getPage() - 1);
			num = psize;
		}else{
			if(spage<1){spage=1;}
			if(spage>pageCount){spage=pageCount;}
			if(epage<1){epage=1;}
			if(epage>epage){epage=pageCount;}
			if(spage<=epage){
				start = psize * (spage - 1);
				num = (epage-spage+1)*psize;
			}else{
				start = psize * (epage - 1);
				num = (spage-epage+1)*psize;
			}
		}
		sql = sql + " limit " + start + " , "+num;
		List<String[]> list = jdbcUtil.getListp(sql, String[].class, param1);
		setTbodyList(list);
	}
	public int getPage() {
		return page;
	}
//	public void setPage(int page) {
//		this.page = page;
//	}
	public int getPsize() {
		return psize;
	}
	public void setPsize(int psize) {
		this.psize = psize;
	}
	
	public abstract void init(HttpServletRequest req)throws DbException ;
	public abstract String getTitle();//标题
	
	public abstract String[] getThead();//表头
	public abstract <T> List<T> getTbody();
	public abstract String[] getTfoot();
	
	public abstract String getFooter();
	
	//总记录 分页时才用到这个
//	public abstract int getTotalCount()throws DbException;
	
	public String getJsp(){
		return "/WEB-INF/jsp/report.jsp";
	}
	/**
	 * 是否有权限
	 * @return
	 */
	public boolean checkAuth(HttpServletRequest req){
		return false;
	}
//	public boolean isNeedCut() {
//		return needCut;
//	}
//	public void setNeedCut(boolean needCut) {
//		this.needCut = needCut;
//	}
	public List<?> getTbodyList() {
		return tbodyList;
	}
	public void setTbodyList(List<?> tbodyList) {
		this.tbodyList = tbodyList;
	}
	public final int  getTotalCount() {
		return totalCount;
	}
	public final void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	public boolean isNeedCut() {
		return needCut;
	}
	public void setNeedCut(boolean needCut) {
		this.needCut = needCut;
	}
	public void setSpage(int spage) {
		this.spage = spage;
	}
	public void setEpage(int epage) {
		this.epage = epage;
	}
	
	
}
