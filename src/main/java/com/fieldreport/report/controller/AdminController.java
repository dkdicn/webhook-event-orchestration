package com.fieldreport.report.controller;

import com.fieldreport.report.dto.DashboardResponse;
import com.fieldreport.report.entity.Report;
import com.fieldreport.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ReportService reportService;

    @GetMapping("/reports")
    public ResponseEntity<List<Report>> searchReports(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String keyword) {
        List<Report> reports = reportService.searchReports(category, priority, keyword);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> dashboard() {
        return ResponseEntity.ok(reportService.getDashboard());
    }
}
