package com.youge.report.myreport.coop;

public class CoopUtil {
	public static String getDescBySize(String size) {
		if ("1".equals(size)) {
			return "小";
		}
		if ("2".equals(size)) {
			return "中";
		}
		if ("3".equals(size)) {
			return "大";
		}
		return "";
	}
}
