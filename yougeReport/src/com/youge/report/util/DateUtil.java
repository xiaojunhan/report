package com.youge.report.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



public class DateUtil {
	
	public static String format(Date date,String p){
		SimpleDateFormat sdf = new SimpleDateFormat(p);
		return sdf.format(date);
	}
	public static String format(Date date){
		return format(date,"yyyy-MM-dd HH:mm:ss");
	}
	public static String format(long time){
		return format(new Date(time));
	}
	public static String format(long time,String p){
		return format(new Date(time),p);
	}
	public static Date parse(String date,String p) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat(p);
		return sdf.parse(date);
	}
	public static Date parse(String date) throws ParseException{
		return parse(date,"yyyy-MM-dd HH:mm:ss");
	}
	public static long getTime(){
		return new Date().getTime();
	}
	/**
	 * 到秒
	 * @return
	 */
	public static long getTime1(){
		long l = new Date().getTime();
		int i = (int)(l/1000);
		return i;
	}
	
    public static String getNextDate(Date date,int i,String p){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR, i);
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(p);   
		return format.format(cal.getTime()); 
	}
    
    public static String getNextMinute(Date date,int i,String p){
  		Calendar cal = Calendar.getInstance();
  		cal.setTime(date);
  		cal.add(Calendar.MINUTE, i);
  		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(p);   
  		return format.format(cal.getTime()); 
  	}
	
    public static String getNextMonth(Date date,int i,String p){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, i);
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(p);   
		return format.format(cal.getTime()); 
	}
    
    public static long getTime(String date,String p) throws ParseException{
    	SimpleDateFormat sdf = new SimpleDateFormat(p);
    	return sdf.parse(date).getTime();
	}
    /**
     * 
     * @param startDate
     * @param endDate
     * @param p 输入日期的格式
     * @return 输出的格式为yyyyMMdd
     * @throws ParseException
     */
    public static List<String> getDateList(String startDate,String endDate,String p) throws ParseException{
    	List<String> list = new ArrayList<String>();
    	Date d1 = parse(startDate, p);
    	Date d2 = parse(endDate, p);
    	String s1 = format(d1,"yyyyMMdd");
    	String s2 = format(d2,"yyyyMMdd");
    	int r = s1.compareTo(s2);
    	if(r<0){
    		String str = null;
    		list.add(s1);
    		while(!((str=getNextDate(d1,1,"yyyyMMdd")).equals(s2))){
    			d1 = parse(str, "yyyyMMdd");
    			list.add(str);
    		}
    		list.add(s2);
    	}else if(r==0){
    		list.add(s1);
    	}else{
    		String str = null;
    		list.add(s2);
    		while(!((str=getNextDate(d2,1,"yyyyMMdd")).equals(s1))){
    			d2 = parse(str, "yyyyMMdd");
    			list.add(str);
    		}
    		list.add(s1);
    	}
    	return list;
    }
    /**
     * 获得间隔天数
     * @param date1
     * @param date2
     * @return
     * @throws ParseException 
     */
    public static int getIntervalDays(String startTime,Date nowTime) throws ParseException{
		long l1 = DateUtil.getTime(startTime, "yyyy-MM-dd HH:mm:ss");
		long l2 = nowTime.getTime();
		long diff = l2 - l1;
		int d = (int) (diff / 1000 / 60 / 60 / 24);
		return d;
    }
    /**
     * 获得间隔小时
     * @param date1
     * @param date2
     * @return
     * @throws ParseException 
     */
    public static int getIntervalHours(String startTime,Date nowTime) throws ParseException{
		long l1 = DateUtil.getTime(startTime, "yyyy-MM-dd HH:mm:ss");
		long l2 = nowTime.getTime();
		long diff = l2 - l1;
		int d = (int) (diff / 1000 / 60 / 60);
		return d;
    }
	public static void main(String[] args) throws ParseException{
//		System.out.println(getNextDate(new Date(),-30,"yyyyMMdd"));
//		System.out.println(getNextDate(new Date(),1,"yyyyMMdd"));
		System.out.println(getIntervalDays("2014-08-31 16:57:11",new Date()));
	}
}
