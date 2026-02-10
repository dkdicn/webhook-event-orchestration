package com.fieldreport.report.controller;

import com.fieldreport.report.dto.CallbackRequest;
import com.fieldreport.report.dto.ReportRequest;
import com.fieldreport.report.dto.ReportResponse;
import com.fieldreport.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponse> submit(@RequestBody ReportRequest request) {
        ReportResponse response = reportService.submit(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/callback")
    public ResponseEntity<String> callback(@RequestBody CallbackRequest request) {
        reportService.processCallback(request);
        return ResponseEntity.ok("OK");
    }

    @GetMapping(value = "/alerts/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        return reportService.subscribe();
    }
}
