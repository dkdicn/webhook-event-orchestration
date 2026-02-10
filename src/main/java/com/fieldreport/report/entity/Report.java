package com.fieldreport.report.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    private String summary;

    private String category;

    private String priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    private String failReason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ReportStatus.PENDING;
        }
    }

    public void completeAnalysis(String summary, String category, String priority,
                                  boolean success, String failReason) {
        this.summary = summary;
        this.category = category;
        this.priority = priority;
        this.status = success ? ReportStatus.SUCCESS : ReportStatus.FAIL;
        this.failReason = failReason;
        this.processedAt = LocalDateTime.now();
    }
}
