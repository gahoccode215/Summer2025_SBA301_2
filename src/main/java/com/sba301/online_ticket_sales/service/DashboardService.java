package com.sba301.online_ticket_sales.service;

import com.sba301.online_ticket_sales.dto.dashboard.DashboardResponse;

import java.time.LocalDate;

public interface DashboardService {
    DashboardResponse getDashboard(LocalDate startDate, LocalDate endDate);
    DashboardResponse getAdminDashboard(LocalDate startDate, LocalDate endDate);
    DashboardResponse getManagerDashboard(Long cinemaId, LocalDate startDate, LocalDate endDate);
}
