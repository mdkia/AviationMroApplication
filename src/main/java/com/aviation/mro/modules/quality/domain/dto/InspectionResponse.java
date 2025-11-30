package com.aviation.mro.modules.quality.domain.dto;

import com.aviation.mro.modules.quality.domain.enums.DefectSeverity;
import com.aviation.mro.modules.quality.domain.enums.InspectionStatus;
import com.aviation.mro.modules.quality.domain.enums.ComplianceStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class InspectionResponse {
    private Long id;
    private String inspectionNumber;
    private Long inspectionPlanId;
    private String inspectionPlanTitle;
    private InspectionStatus status;
    private ComplianceStatus complianceStatus;
    private Long partId;
    private String partNumber;
    private Long workOrderId;
    private String workOrderNumber;
    private Long inspectorId;
    private String inspectorName;
    private LocalDateTime scheduledDate;
    private LocalDateTime actualStartDate;
    private LocalDateTime actualEndDate;
    private Integer totalChecks;
    private Integer passedChecks;
    private Integer failedChecks;
    private Double complianceRate;
    private String findings;
    private String recommendations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<DefectResponse> defects = new ArrayList<>();
    private NCRSummary ncrSummary;

    @Data
    public static class DefectResponse {
        private Long id;
        private Long qualityCheckId;
        private String qualityCheckCode;
        private String qualityCheckDescription;
        private ComplianceStatus complianceStatus;
        private DefectSeverity severity;
        private String actualValue;
        private String deviation;
        private String notes;
        private String evidencePhotos;
    }

    @Data
    public static class NCRSummary {
        private Long ncrId;
        private String ncrNumber;
        private String status;
    }
}
