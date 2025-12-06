package com.aviation.mro.modules.quality.domain.dto;

import com.aviation.mro.modules.quality.domain.enums.InspectionType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class InspectionPlanResponse {
    private Long id;
    private String planNumber;
    private String title;
    private String description;
    private InspectionType inspectionType;
    private String applicableStandards;
    private Integer inspectionFrequencyDays;
    private Integer sampleSize;
    private Boolean isActive;
    private Long qualityStandardId;
    private String qualityStandardName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<QualityCheckResponse> checkpoints = new ArrayList<>();
    @Getter
    @Setter
    public static class QualityCheckResponse {
        private Long id;
        private String checkCode;
        private String description;
        private String acceptanceCriteria;
        private String measurementMethod;
        private String toolsRequired;
        private Boolean isCritical;
        private Integer sequenceNumber;
    }
}