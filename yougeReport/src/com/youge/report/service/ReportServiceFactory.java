package com.youge.report.service;

import com.youge.report.myreport.Test;

public class ReportServiceFactory {
	/**
	 * 报表少的话 switch处理
	 * 多的话 在xml、properties文件配置 使用反射
	 * @param report
	 * @return
	 */
	public static ReportService getInstance(String report) {
		switch (report) {
		case "test":
			return new Test();
		}
		return null;
	}

}
