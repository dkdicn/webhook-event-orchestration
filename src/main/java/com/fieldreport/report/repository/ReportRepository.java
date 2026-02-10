package com.fieldreport.report.repository;

import com.fieldreport.report.entity.Report;
import com.fieldreport.report.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT r FROM Report r WHERE " +
           "(:category IS NULL OR r.category = :category) AND " +
           "(:priority IS NULL OR r.priority = :priority) AND " +
           "(:keyword IS NULL OR LOWER(r.message) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY r.createdAt DESC")
    List<Report> searchReports(@Param("category") String category,
                                @Param("priority") String priority,
                                @Param("keyword") String keyword);

    long countByStatusAndCreatedAtAfter(ReportStatus status, LocalDateTime after);

    List<Report> findAllByOrderByCreatedAtDesc();
}
