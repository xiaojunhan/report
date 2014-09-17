package com.youge.report.service;

import com.youge.report.myreport.CoopDayReport;

public class ReportServiceFactory {
	public static ReportService getInstance(String report) {
		if ("test".equals(report)) {
			return new CoopDayReport();
		}
		return null;
	}

}
