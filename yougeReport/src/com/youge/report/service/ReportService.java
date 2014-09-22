package com.youge.report.service;

import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.youge.report.Constants;
import com.youge.report.model.PageInfo;
import com.youge.report.model.ReportInfo;
import com.youge.report.util.HttpUtil;
import com.youge.report.util.JdbcUtil;
import com.youge.report.util.MemCacheUtil;
import com.youge.report.util.StringUtil;

public abstract class ReportService {

	private boolean needCut = true;//导出时 不需要分页的情况
	private boolean readonly = true;//导出文件是否为只读
	private boolean cache = true;
	private HttpServletRequest request;
	private ReportInfo reportInfoDb;
	private ReportInfo reportInfoCache;
	
	protected static JdbcUtil jdbcUtil = new JdbcUtil(Constants.DATA_SOURCE_ID);
	private static  Logger logger = LogManager.getLogger(ReportService.class);
	 
	/** 数据初始化 **/
	protected abstract void init()throws Exception ;
	
	public abstract String getTitle();//标题
	
	public abstract String[] getThead();//表头
//	public abstract <T> List<T> getTbody();
	/**
	 * 做数据库中查询出的数据做二次处理
	 * @return
	 */
	protected abstract ReportInfo getReportInfoOpr();
	
	/** 最后一页 才有footer **/
	public abstract String[] getTfoot();
	
	/** 每页都有 **/
	public abstract String getFooter();
	
	/** 默认返回页 **/
	public String getJsp(){
		return "/WEB-INF/jsp/report.jsp";
	}
	public void doSql(String sql,Object[] param1) throws Exception{
		setNeedCut(false);//调用这个方法说明不需要分页
		List<String[]> list = jdbcUtil.getListp(sql, String[].class, param1);
		ReportInfo ri = new ReportInfo();
		ri.setReportList(list);
		setReportInfoDb(ri);
	}
	
	public void doSql(HttpServletRequest req,String sql,Object[] param1,String countSql,Object[] param2) throws Exception{
		if(!needCut){//不需要分页
			doSql(sql, param1);
			return;
		}
		int page = getFromRequest(req,"page",1);
		page = page < 1 ? 1 : page;
		int psize = getFromRequest(req,"psize",15);
		int spage = getFromRequest(req,"spage",-1);
		int epage = getFromRequest(req,"epage",-1);
		int count = jdbcUtil.getIntp(countSql,param2);
		if(count<=0){
			return;
		}
		int pageCount = (count % psize == 0) ? count / psize : count / psize + 1;
		if(page>pageCount){
			page = pageCount;
		}
		int start = 0;
		int num = 0;
		if(spage==-1 && epage==-1){
			start = psize * (page - 1);
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
		
		ReportInfo ri = new ReportInfo();
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPage(page);
		pageInfo.setPageCount(pageCount);
		pageInfo.setTotalCount(count);
		ri.setReportList(list);
		ri.setPageInfo(pageInfo);
		setReportInfoDb(ri);
	}
	/**
	 * 对getReportInfoOpr中数据做缓存处理
	 * @return
	 */
	public void initReportInfo()throws Exception{
		if(!isCache()){
			init();
			ReportInfo ri = getReportInfoOpr();
			setReportInfoCache(ri);
			return;
		}
		String report = getRequest().getParameter("report");
		StringBuilder sb = new StringBuilder();
		sb.append("YOUGE_REPORT_INFO_")
		.append(report).append("_")
		.append(needCut).append("_")
		.append(getKeyByRequest(getRequest()));
		String key = sb.toString();
		logger.info("key=="+key);
		ReportInfo ri = MemCacheUtil.nsget(report,key);
		if(ri==null){
			init();
			ri = getReportInfoOpr();
			if(ri!=null){
				MemCacheUtil.nsset(report,key, 30*60, ri);
			}
		}
		setReportInfoCache(ri);
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
	/**
	 * 
	 * @param request
	 * @param key
	 * @param defau 默认值
	 * @return
	 */
	public int getFromRequest(HttpServletRequest request,String key,int defau){
		String str = request.getParameter(key);
		if(StringUtil.isEmpty(str)){
			return defau;
		}
		int temp = defau;
		try{
			temp = Integer.parseInt(str);
		}catch(Exception e){}
		return temp;
	}
	
	public String getKeyByRequest(HttpServletRequest request){
		Enumeration<String> e = request.getParameterNames();
		StringBuilder sb = new StringBuilder();
		while(e.hasMoreElements()){
			String name = e.nextElement();
			String value = request.getParameter(name);
			if(!"token".equals(name)){
				sb.append(name).append("=").append(value).append("&");
			}
		}
		return StringUtil.MD5(sb.toString());
	}
	
	public String getAlink(String href,String value){
		return getAlink(href, value,"_self");
	}
	public String getAlink(String href,String value,String targe){
		StringBuilder sb = new StringBuilder();
		sb.append("<a href=\"").append(href).append("\" target=\""+targe+"\">")
		.append(value).append("</a>");
		return sb.toString();
	}
	public boolean isNeedCut() {
		return needCut;
	}
	public void setNeedCut(boolean needCut) {
		this.needCut = needCut;
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	protected ReportInfo getReportInfoDb() {
		return reportInfoDb;
	}
	private void setReportInfoDb(ReportInfo reportInfoDb) {
		this.reportInfoDb = reportInfoDb;
	}
	
	public ReportInfo getReportInfo() {
		return reportInfoCache;
	}
	private void setReportInfoCache(ReportInfo reportInfoCache) {
		this.reportInfoCache = reportInfoCache;
	}

	public boolean isReadonly() {
		return readonly;
	}
	/**
	 * 设置导出文件是否为只读
	 * @param readonly
	 */
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public boolean isCache() {
		return cache;
	}

	public void setCache(boolean cache) {
		this.cache = cache;
	}
}
