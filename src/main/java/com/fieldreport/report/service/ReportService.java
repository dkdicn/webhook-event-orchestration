package com.fieldreport.report.service;

import com.fieldreport.report.dto.CallbackRequest;
import com.fieldreport.report.dto.DashboardResponse;
import com.fieldreport.report.dto.ReportRequest;
import com.fieldreport.report.dto.ReportResponse;
import com.fieldreport.report.entity.Report;
import com.fieldreport.report.entity.ReportStatus;
import com.fieldreport.report.repository.ReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class ReportService {

    private final RestTemplate restTemplate;
    private final String webhookUrl;
    private final ReportRepository reportRepository;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public ReportService(
            @Value("${n8n.webhook-url}") String webhookUrl,
            ReportRepository reportRepository
    ) {
        this.restTemplate = new RestTemplate();
        this.webhookUrl = webhookUrl;
        this.reportRepository = reportRepository;
    }

    public ReportResponse submit(ReportRequest request) {
        Report report = Report.builder()
                .message(request.getMessage())
                .status(ReportStatus.PENDING)
                .build();
        report = reportRepository.save(report);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = Map.of(
                    "reportId", report.getId(),
                    "message", request.getMessage()
            );
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("n8n webhook 호출 성공, reportId={}", report.getId());
                return ReportResponse.success(report.getId());
            }

            log.warn("n8n webhook 응답 오류: {}", response.getStatusCode());
            return new ReportResponse(false, "일시적 오류, 관리자에게 전달됨", report.getId());
        } catch (Exception e) {
            log.error("n8n webhook 호출 실패", e);
            return new ReportResponse(false, "일시적 오류, 관리자에게 전달됨", report.getId());
        }
    }

    public void processCallback(CallbackRequest callback) {
        Report report = reportRepository.findById(callback.getReportId())
                .orElseThrow(() -> new IllegalArgumentException("Report not found: " + callback.getReportId()));

        report.completeAnalysis(
                callback.getSummary(),
                callback.getCategory(),
                callback.getPriority(),
                callback.isSuccess(),
                callback.getFailReason()
        );
        reportRepository.save(report);

        log.info("콜백 처리 완료 reportId={}, success={}", callback.getReportId(), callback.isSuccess());

        if (!callback.isSuccess()) {
            sendAlertToAdmins(report);
        }
    }

    public List<Report> searchReports(String category, String priority, String keyword) {
        return reportRepository.searchReports(category, priority, keyword);
    }

    public DashboardResponse getDashboard() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long success = reportRepository.countByStatusAndCreatedAtAfter(ReportStatus.SUCCESS, todayStart);
        long fail = reportRepository.countByStatusAndCreatedAtAfter(ReportStatus.FAIL, todayStart);
        long pending = reportRepository.countByStatusAndCreatedAtAfter(ReportStatus.PENDING, todayStart);
        return new DashboardResponse(success + fail + pending, success, fail, pending);
    }

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        return emitter;
    }

    private void sendAlertToAdmins(Report report) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("alert")
                        .data(Map.of(
                                "reportId", report.getId(),
                                "message", report.getMessage(),
                                "failReason", report.getFailReason() != null ? report.getFailReason() : "unknown",
                                "processedAt", report.getProcessedAt().toString()
                        )));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);
    }
}
