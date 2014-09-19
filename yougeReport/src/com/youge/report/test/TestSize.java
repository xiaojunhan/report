package com.youge.report.test;

import java.io.UnsupportedEncodingException;

public class TestSize {

	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		String title = "网点2014";
		int titleSize = title.getBytes("UTF-8").length;
		System.out.println(titleSize);//10
		titleSize = title.getBytes("GBK").length;
		System.out.println(titleSize);//8
	}

}
