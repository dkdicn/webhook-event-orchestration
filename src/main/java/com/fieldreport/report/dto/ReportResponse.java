package com.fieldreport.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportResponse {

    private boolean success;
    private String message;
    private Long reportId;

    public static ReportResponse success(Long reportId) {
        return new ReportResponse(true, "접수되었습니다", reportId);
    }

    public static ReportResponse fail() {
        return new ReportResponse(false, "일시적 오류, 관리자에게 전달됨", null);
    }
}
