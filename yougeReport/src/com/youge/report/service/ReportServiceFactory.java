package com.youge.report.service;

import com.youge.report.myreport.Test;
import com.youge.report.myreport.coop.ReportCoopDay;
import com.youge.report.myreport.coop.ReportCoopQuDetail;
import com.youge.report.myreport.coop.ReportCoopTouDetail;

public class ReportServiceFactory {
	public static ReportService getInstance(String report) {
		switch (report) {
		case "test":
			return new Test();
		case "report_coop_day":
			return new ReportCoopDay();
		case "report_coop_tou":
			return new ReportCoopTouDetail();
		case "report_coop_qu":
			return new ReportCoopQuDetail();
		}
		return null;
	}

}
