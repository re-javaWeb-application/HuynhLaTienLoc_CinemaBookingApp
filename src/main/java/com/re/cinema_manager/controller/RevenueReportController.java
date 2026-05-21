package com.re.cinema_manager.controller;

import com.re.cinema_manager.dto.admin.RevenueReportDto;
import com.re.cinema_manager.service.RevenueReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

/**
 * Dashboard Admin — Hướng 4: doanh thu theo tháng, Top 5 phim (SQL GROUP BY / JOIN).
 */
@Controller
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class RevenueReportController {

    private final RevenueReportService revenueReportService;

    @GetMapping
    public String report(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model) {

        LocalDate toDate = to != null ? to : LocalDate.now();
        // Mặc định 12 tháng gần nhất — phù hợp biểu đồ doanh thu theo tháng
        LocalDate fromDate = from != null ? from : toDate.minusMonths(11).withDayOfMonth(1);

        RevenueReportDto report = revenueReportService.buildReport(fromDate, toDate);
        model.addAttribute("report", report);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        return "admin/revenue-report";
    }
}
