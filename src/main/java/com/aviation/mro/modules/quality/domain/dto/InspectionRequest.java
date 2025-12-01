// modules/quality/domain/dto/InspectionRequest.java
package com.aviation.mro.modules.quality.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class InspectionRequest {

    @NotNull(message = "Inspection plan ID is required")
    private Long inspectionPlanId;

    private Long partId;
    private Long workOrderId;
    private Long inspectorId;
    private LocalDateTime scheduledDate;

    @NotNull(message = "At least one defect record is required")
    private List<DefectRequest> defects = new ArrayList<>();
}
