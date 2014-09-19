package com.youge.report.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.youge.report.Constants;
import com.youge.report.exception.DbException;
import com.youge.report.model.PageInfo;
import com.youge.report.model.ReportInfo;
import com.youge.report.util.HttpUtil;
import com.youge.report.util.JdbcUtil;
import com.youge.report.util.MemCacheUtil;
import com.youge.report.util.StringUtil;

public abstract class ReportService {
	private int page;
	private int psize;
	private int pageCount;
	private int totalCount;
	private int spage=-1;//开始页 导出文件时使用
	private int epage=-1;//结束页 导出文件时使用
	private boolean needCut = true;//导出时 不需要分页的情况
	private List<String[]> tbodyList;
	private HttpServletRequest request;
	protected static JdbcUtil jdbcUtil = new JdbcUtil(Constants.DATA_SOURCE_ID);
	private static  Logger logger = LogManager.getLogger(ReportService.class);
//	protected Map<String,String> getRequestMap(HttpServletRequest request){
//		Map<String,String> map = new HashMap<String,String>();
//		Enumeration<String> e = request.getParameterNames();
//		while(e.hasMoreElements()){
//			String name = e.nextElement();
//			String value = request.getParameter(name);
//			map.put(name, value);
//		}
//		return map;
//	}
	public void init(HttpServletRequest req){
		String pageStr = req.getParameter("page");
		String psizeStr = req.getParameter("psize");
		if(StringUtil.isNotEmpty(pageStr)){
			try{
				page = Integer.parseInt(pageStr);
			}catch(Exception e){}
		}
		if(page<1){
			page = 1;
		}
		if(StringUtil.isNotEmpty(psizeStr)){
			try{
				psize = Integer.parseInt(psizeStr);
			}catch(Exception e){}
		}
		if(psize==0){
			psize = 15;
		}
//		init(req);
	}
	public void doSql(String sql,Object[] param1) throws Exception{
		setNeedCut(false);//调用这个方法说明不需要分页
		//TODO 根据 String sql,Object[] param1 做缓存
		
		StringBuilder sb = new StringBuilder();
		sb.append("YOUGE_REPORT_DOSQL_SIMPLE_").append(MemCacheUtil.getKey(sql,param1));
		String key = sb.toString();
		logger.info("key=="+key);
		ReportInfo reportInfo = MemCacheUtil.get(key);
		if(reportInfo!=null){
			setTbodyList(reportInfo.getReportList());
			return;
		}
		
		List<String[]> list = jdbcUtil.getListp(sql, String[].class, param1);
		setTbodyList(list);
		
		reportInfo = new ReportInfo();
		reportInfo.setReportList(list);
		MemCacheUtil.set(key, 60*30, reportInfo);//缓存30分钟
	}
	
	public void doSql(String sql,Object[] param1,String countSql,Object[] param2) throws Exception{
		if(!needCut){//不需要分页
			doSql(sql, param1);
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("YOUGE_REPORT_DOSQL_")
		.append(needCut).append("_")
		.append(page).append("_")
		.append(psize).append("_")
		.append(spage).append("_")
		.append(epage).append("_")
		.append(MemCacheUtil.getKey(sql,param1,countSql,param2));
		String key = sb.toString();
		
		logger.info("key=="+key);
		ReportInfo reportInfo = MemCacheUtil.get(key);
		if(reportInfo!=null){
//			logger.info(reportInfo.toString());
			setTbodyList(reportInfo.getReportList());
			setTotalCount(reportInfo.getPageInfo().getTotalCount());
			setPage(reportInfo.getPageInfo().getPage());
			setPageCount(reportInfo.getPageInfo().getPageCount());
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
		
		reportInfo = new ReportInfo();
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPage(page);
		pageInfo.setPageCount(pageCount);
		pageInfo.setTotalCount(count);
		reportInfo.setReportList(list);
		reportInfo.setPageInfo(pageInfo);
		MemCacheUtil.set(key, 60*30, reportInfo);//缓存30分钟
	}
	public int getPage() {
		return page;
	}
	private void setPage(int page) {
		this.page = page;
	}
	public int getPsize() {
		return psize;
	}
	public void setPsize(int psize) {
		this.psize = psize;
	}
	
//	public abstract void init(HttpServletRequest req)throws Exception ;
	public abstract String getTitle();//标题
	
	public abstract String[] getThead();//表头
	public abstract <T> List<T> getTbody();
	public abstract String[] getTfoot();
	
	public abstract String getFooter();
	
	public String getJsp(){
		return "/WEB-INF/jsp/report.jsp";
	}
	
	public List<?> getTbodyWithCache(){
		
		List<?> list = getTbody();
		
		return list;
	}
	/**
	 * 是否有权限
	 * @return
	 */
	public boolean checkAuth(HttpServletRequest request){
		String token = request.getParameter("token");
		String compId = request.getParameter("compId");
		String branchId = request.getParameter("branchId");
//		System.out.println(token+"==="+compId+"==="+branchId);
		if(StringUtil.isEmpty(token)){
			return false;
		}
		if(StringUtil.isNotEmpty(branchId)){
			return HttpUtil.checkReport(token, branchId);
		}else{
			if(StringUtil.isEmpty(compId)){
				return false;
			}
			try {
				String value = (String)MemCacheUtil.get(token);
				if(compId.equals(value)){
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	protected List<String[]> getTbodyList() {
		return tbodyList;
	}
	private void setTbodyList(List<String[]> tbodyList) {
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
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
}
