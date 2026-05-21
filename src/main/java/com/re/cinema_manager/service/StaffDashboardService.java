package com.re.cinema_manager.service;

import com.re.cinema_manager.dto.staff.StaffDashboardDto;
import com.re.cinema_manager.dto.staff.StaffRoomDetailDto;

public interface StaffDashboardService {

    StaffDashboardDto buildDashboard();

    StaffRoomDetailDto getRoomDetail(Long roomId, Long showtimeId);
}
