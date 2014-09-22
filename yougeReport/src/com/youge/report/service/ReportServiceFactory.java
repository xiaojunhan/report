package com.youge.report.service;

import com.youge.report.myreport.Test;
import com.youge.report.myreport.coop.ReportCoopDay;
import com.youge.report.myreport.coop.ReportCoopMonth;
import com.youge.report.myreport.coop.ReportCoopQuDetail;
import com.youge.report.myreport.coop.ReportCoopTouDetail;
import com.youge.report.myreport.wuye.ReportWuYeDetail;
import com.youge.report.myreport.wuye.ReportWuYeTotal;

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
		case "report_coop_month":
			return new ReportCoopMonth();
		case "report_wuye_total":
			return new ReportWuYeTotal();
		case "report_wuye_detail":
			return new ReportWuYeDetail();
		}
		return null;
	}

}
