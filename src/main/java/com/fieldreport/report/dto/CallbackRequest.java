package com.fieldreport.report.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CallbackRequest {

    private Long reportId;
    private boolean success;
    private String summary;
    private String category;
    private String priority;
    private String failReason;
}
