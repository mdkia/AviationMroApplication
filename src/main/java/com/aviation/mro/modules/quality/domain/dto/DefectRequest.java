// modules/quality/domain/dto/DefectRequest.java
package com.aviation.mro.modules.quality.domain.dto;

import com.aviation.mro.modules.quality.domain.enums.ComplianceStatus;
import com.aviation.mro.modules.quality.domain.enums.DefectSeverity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DefectRequest {

    @NotNull(message = "Quality check ID is required")
    private Long qualityCheckId;

    @NotNull(message = "Compliance status is required")
    private ComplianceStatus complianceStatus;

    private DefectSeverity severity;
    private String actualValue;
    private String deviation;
    private String notes;
    private String evidencePhotos;
}