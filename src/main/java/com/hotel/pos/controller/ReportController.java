package com.hotel.pos.controller;

import com.hotel.pos.dto.DailyReportResponse;
import com.hotel.pos.service.AuthService;
import com.hotel.pos.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final AuthService authService;

    public ReportController(ReportService reportService, AuthService authService) {
        this.reportService = reportService;
        this.authService = authService;
    }

    @GetMapping("/daily")
    public DailyReportResponse daily(@RequestHeader("X-Auth-Token") String token,
                                      @RequestParam(required = false) String date) {
        authService.requireAuth(token);
        LocalDate d = date != null ? LocalDate.parse(date) : LocalDate.now();
        return reportService.getDailyReport(d);
    }
}
