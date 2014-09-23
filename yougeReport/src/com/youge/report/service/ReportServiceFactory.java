package com.youge.report.service;

import com.youge.report.myreport.Test;

public class ReportServiceFactory {
	public static ReportService getInstance(String report) {
		switch (report) {
		case "test":
			return new Test();
		}
		return null;
	}

}
