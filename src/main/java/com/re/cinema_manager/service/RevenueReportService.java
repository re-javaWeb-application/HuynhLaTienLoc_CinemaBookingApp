package com.re.cinema_manager.service;

import com.re.cinema_manager.dto.admin.RevenueReportDto;

import java.time.LocalDate;

public interface RevenueReportService {

    RevenueReportDto buildReport(LocalDate fromDate, LocalDate toDate);
}
