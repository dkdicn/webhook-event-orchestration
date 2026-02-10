package com.fieldreport.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardResponse {

    private long todayTotal;
    private long todaySuccess;
    private long todayFail;
    private long todayPending;
}
