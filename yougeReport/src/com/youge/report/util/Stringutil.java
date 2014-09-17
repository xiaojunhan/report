package com.youge.report.util;

public class Stringutil {
	
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0 || str.toLowerCase().equals("null");
	}

	public static boolean isNotEmpty(String str) {
		return str != null && str.trim().length() > 0 && (!str.toLowerCase().equals("null"));
	}
}
