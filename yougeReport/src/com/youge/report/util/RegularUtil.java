package com.youge.report.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author <a href="mailto:hanxiaojun85@gmail.com">韩晓军</a>
 * @version 2010-7-31
 * @since 1.6
 */
public class RegularUtil {
	/**
	 * 根据正则表达式及内容得到匹配的结果
	 * 
	 * @param reg
	 *            正则表达式
	 * @param content
	 *            内容
	 * @return
	 */
	public static String getResult(String reg, String content) {
		return getResult(reg, content, 1);
	}

	public static String getResult(String reg, String content, int group) {
		if (reg == null || content == null) {
			return null;
		}
		Matcher matcher = getMatcher(reg, content);
		String part = null;
		if (matcher.find()) {
			part = matcher.group(group);
		}
		return part;
	}
	
	public static boolean isExsit(String reg, String content){
		Matcher matcher = getMatcher(reg, content);
		return matcher.find();
	}

	/**
	 * 去除所有span标签
	 * 
	 * @param data
	 * @return
	 */
	public static String removeSpan(String data) {
		String reg = "<span.*?>|</span>";
		Matcher m = getMatcher(reg, data);
		return m.replaceAll("");
	}

	/**
	 * 去除所有a标签
	 * 
	 * @param data
	 * @return
	 */
	public static String removea(String data) {
		String reg = "<a.*?>|</a>";
		Matcher m = getMatcher(reg, data);
		return m.replaceAll("");
	}

	public static Matcher getMatcher(String reg, String data) {
		Pattern p = Pattern.compile(reg, Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL);
		return p.matcher(data);
	}
	
	public static boolean isNumber(String data){
		String reg="^\\d+$";
		Matcher m = getMatcher(reg, data);
		return m.find();
	}
	
	public static boolean isNumber(String data,int length){
		String reg="^\\d{"+length+"}$";
		Matcher m = getMatcher(reg, data);
		return m.find();
	}
}
