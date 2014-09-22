package com.youge.report.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.CellView;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.youge.report.model.ReportInfo;
import com.youge.report.service.ReportService;
import com.youge.report.service.ReportServiceFactory;
import com.youge.report.util.NumberUtil;
import com.youge.report.util.StringUtil;
/**
 * 用于导出excel
 * @author hanxj
 *
 */
public class ExportServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5876152456587859819L;
	
	private static  Logger logger = LogManager.getLogger(ExportServlet.class);
	/**
	 * 存放生成的excel文件
	 */
	private static final String REPORT_PATH = "/usr/local/app/reportfile/";
	private static final String TEMP_PATH = "/usr/local/app/reportfiletemp/";
	
	private static final String SIZE_CODE = "GBK"; 
	private static final String MESSAGE = "MESSAGE"; 

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String report = req.getParameter("report");
		/**
		 * 可以按页导出数据
		 */
		String spage = req.getParameter("spage");
		String epage = req.getParameter("epage");
		ReportService rs = ReportServiceFactory.getInstance(report);
		if(rs==null){
			//没有该报表
			req.setAttribute(MESSAGE, "该报表不存在");
			req.getRequestDispatcher("/WEB-INF/jsp/common/error.jsp").forward(req, resp);
			return;
		}
		if(!rs.checkAuth(req)){
			//没有访问权限
			req.setAttribute(MESSAGE, "没有访问该报表的权限，您可以刷新页面再次尝试");
			req.getRequestDispatcher("/WEB-INF/jsp/common/error.jsp").forward(req, resp);
			return;
		}
		if(StringUtil.isEmpty(spage)&&StringUtil.isEmpty(epage)){
			rs.setNeedCut(false);
		}
		rs.setRequest(req);
		String key = rs.getKeyByRequest(req);
		//TODO 未来这里弄个文件夹 不要都放一个目录下
		String filePath = REPORT_PATH+report+"-"+key;
		File reportFile = new File(filePath);
		
		if(reportFile.exists()){
			if(!rs.isCache()){
				reportFile.delete();
				try {
					rs.initReportInfo();
				} catch (Exception e) {
					//访问异常
					req.setAttribute(MESSAGE, "服务器异常，请稍后再试");
					req.getRequestDispatcher("/WEB-INF/jsp/common/error.jsp").forward(req, resp);
					return;
				}
				reportFile = createExcel(rs,reportFile);
			}else{
				logger.info("use cache,file exsit,"+filePath);
			}
		}else{
				try {
					rs.initReportInfo();
				} catch (Exception e) {
					//访问异常
					req.setAttribute(MESSAGE, "服务器异常，请稍后再试");
					req.getRequestDispatcher("/WEB-INF/jsp/common/error.jsp").forward(req, resp);
					return;
				}
				reportFile = createExcel(rs,reportFile);
		}
		String fileName = getFileName(req,rs.getTitle(),"xls");
        resp.setContentType("application/octet-stream");
        resp.addHeader("Content-Disposition", "attachment; filename=" + fileName);
        resp.addHeader("Content-Length" ,Long.toString(reportFile.length()));  
        ServletOutputStream servletOutputStream=resp.getOutputStream();
        FileInputStream fileInputStream=new FileInputStream(reportFile);
        BufferedInputStream bufferedInputStream=new BufferedInputStream(fileInputStream);
        int size=0;
        byte[] b=new byte[4096];
        while ((size=bufferedInputStream.read(b))!=-1) {
            servletOutputStream.write(b, 0, size);
        }
        servletOutputStream.flush();
        servletOutputStream.close();
        bufferedInputStream.close();
	}
	
	public String getFileName(HttpServletRequest req,String name,String ext){
		String userAgent = req.getHeader("User-Agent");
		if(userAgent!=null && userAgent.indexOf("Firefox")>-1){
			//for firefox
			try {
				name = new String(name.getBytes("UTF-8"),"iso-8859-1");
				return name + "." + ext;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		//for 其他 ie chrome 测试正常
		try {
			name = URLEncoder.encode(name, "UTF-8");
			name = name.replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return name + "." + ext;
	}
	
	public File createExcel(ReportService rs,File reportFile) throws UnsupportedEncodingException{
		String title = rs.getTitle();
		String[] head = rs.getThead();
		String[] foot = rs.getTfoot();
		String footer = rs.getFooter();
		ReportInfo ri = rs.getReportInfo();
		List<String[]> bodyList = null;//TODO rs.getTbody();
		if(ri!=null){
			bodyList = ri.getReportList();
		}
		int collen = 0;
		if(head!=null && head.length>0){
			collen = head.length-1;
		}else{
			if(bodyList!=null && bodyList.size()>0){
				String[] arr = bodyList.get(0);
				collen = arr.length-1;
			}
		}
		//保存每列最大字节数
		//用于动态计算列宽度
		//title * 1.5
		int[] cloSize = new int[collen+1];
		//涉及到跨列的3个
		int titleSize = title.getBytes(SIZE_CODE).length;
		titleSize = (int)(titleSize*1.5)+1;
		int norecordSize = "没有查询到记录".getBytes(SIZE_CODE).length;
		int footerSize = 0;
		if(StringUtil.isNotEmpty(footer)){
			footerSize = footer.getBytes(SIZE_CODE).length;
		}
		int max = NumberUtil.getMax(titleSize,norecordSize,footerSize);
		String tempName = TEMP_PATH + System.nanoTime() +NumberUtil.getRandom(100000, 999999);
		OutputStream os = null;
        try {
            os = new FileOutputStream(tempName);
            WritableWorkbook wwb = Workbook.createWorkbook(os);
            
            WritableSheet sheet = wwb.createSheet("report", 0);
            if(rs.isReadonly()){
            	wwb.setProtected(true);//设为只读
            	sheet.getSettings().setProtected(true);//设为只读
            }
            WritableFont wfTitle = new jxl.write.WritableFont(
                    WritableFont.ARIAL, 18, WritableFont.BOLD, false);
            WritableCellFormat wcfTitle = new WritableCellFormat(wfTitle);
            wcfTitle.setAlignment(Alignment.CENTRE);
            wcfTitle.setVerticalAlignment(VerticalAlignment.CENTRE);
            wcfTitle.setWrap(true);
            if(collen>0){
            	sheet.mergeCells(0, 0, collen, 1);
            }
            Label titleCell = new Label(0, 0, title, wcfTitle);
            sheet.addCell(titleCell);
            int headSize = 0;
            if(head!=null && head.length>0){
            	headSize = 1;
            	for(int i=0;i<head.length;i++){
        			 Label labelCell = new Label(i,2,head[i]);
        			 sheet.addCell(labelCell);
        			 cloSize[i] = head[i].getBytes(SIZE_CODE).length;
        		}
            }
            int bodySize = 0;
            if(bodyList!=null && bodyList.size()>0){
            	bodySize =  bodyList.size();
            	for(int i=0;i<bodyList.size();i++){
            		String[] arr = bodyList.get(i);
            		for(int j=0;j<arr.length;j++){
            			 Label labelCell = new Label(j, i+2+headSize, arr[j]);
            			 sheet.addCell(labelCell);
            			 int tempSize = arr[j].getBytes(SIZE_CODE).length;
            			 if(tempSize > cloSize[j]){
            				 cloSize[j] = tempSize;
            			 }
            		}
            	}
            }else{
            	bodySize = 1;
            	WritableFont wf = new WritableFont(WritableFont.TIMES, 12,WritableFont.NO_BOLD, false);
                WritableCellFormat wcf = new WritableCellFormat(NumberFormats.TEXT);
                wcf.setFont(wf);
                wcf.setAlignment(Alignment.CENTRE);
                wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
                if(collen>0){
                	sheet.mergeCells(0, 2+headSize, collen, 2+headSize);
                }
	            Label cell = new Label(0, 2+headSize, "没有查询到记录",wcf);
	            sheet.addCell(cell);
            }
            int footSize = 0;
            if(foot!=null && foot.length>0){
            	footSize = 1;
            	for(int i=0;i<foot.length;i++){
        			 Label labelCell = new Label(i,bodySize+2+headSize,foot[i]);
        			 sheet.addCell(labelCell);
        			 int tempSize = foot[i].getBytes(SIZE_CODE).length;
        			 if(tempSize > cloSize[i]){
        				 cloSize[i] = tempSize;
        			 }
        		}
            }
            if(StringUtil.isNotEmpty(footer)){
            	WritableFont wf = new WritableFont(WritableFont.TIMES, 12,WritableFont.NO_BOLD, false);
                WritableCellFormat wcf = new WritableCellFormat(NumberFormats.TEXT);
                wcf.setFont(wf);
                wcf.setAlignment(Alignment.RIGHT);
                wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
                if(collen>0){
                	sheet.mergeCells(0, bodySize+2+headSize+footSize, collen, bodySize+2+headSize+footSize);
                }
	            Label cell = new Label(0, bodySize+2+headSize+footSize, footer,wcf);
	            sheet.addCell(cell);
            }
            int total = 0;
            for(int i=0;i<cloSize.length;i++){
            	int t1 = cloSize[i];
            	int t2 = 0;
            	if(t1<5){
            		t1 = 5;//最小10
            	}
            	if(t1<20){
            		t2 = (int)(t1*1.5)+1;
            	}else{
            		t2 = (int)(t1*1.2)+1;
            	}
            	total = total + t2;
            	cloSize[i] = t2;
            }
            if(total<max*2){//希望列宽总和 大于（标题|foot|norecord最大值）两倍 //不足两倍的话补足
            	int add = (int)((max*2-total)/cloSize.length)+1;
            	  for(int i=0;i<cloSize.length;i++){
                  	 cloSize[i] = cloSize[i] + add;
                  }
            }
            WritableFont wf = new WritableFont(WritableFont.TIMES, 12,WritableFont.NO_BOLD, false);
            WritableCellFormat wcfF = new WritableCellFormat(NumberFormats.TEXT); //定义一个单元格样式
    	    wcfF.setFont(wf); //设置字体
            for(int i=0;i<=collen;i++){
            	CellView cv = new CellView(); //定义一个列显示样式 
        	    cv.setFormat(wcfF);//把定义的单元格格式初始化进去
        	    cv.setSize(cloSize[i]*265);
            	sheet.setColumnView(i, cv);
            }
            wwb.write();
            wwb.close();
        } catch (Exception e) {
           logger.error(e.getMessage(), e);
        } finally {
            if (os!=null) {
                try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
        File tempFile = new File(tempName);
        tempFile.renameTo(reportFile);
		return reportFile;
	}
}
