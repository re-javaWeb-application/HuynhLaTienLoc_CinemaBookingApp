package com.re.cinema_manager.controller;

import com.re.cinema_manager.service.StaffDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffDashboardController {

    private final StaffDashboardService staffDashboardService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("dashboard", staffDashboardService.buildDashboard());
        return "staff/dashboard";
    }

    @GetMapping("/dashboard/rooms/{roomId}")
    public String roomDetail(@PathVariable Long roomId,
                             @RequestParam(required = false) Long showtimeId,
                             Model model) {
        model.addAttribute("roomDetail", staffDashboardService.getRoomDetail(roomId, showtimeId));
        return "staff/room-detail";
    }
}
