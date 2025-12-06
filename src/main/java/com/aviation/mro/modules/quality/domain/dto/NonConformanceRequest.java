package com.aviation.mro.modules.quality.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NonConformanceRequest {

    @NotNull(message = "Inspection ID is required")
    private Long inspectionId;

    @NotBlank(message = "Problem description is required")
    private String problemDescription;

    private String rootCause;
    private String immediateAction;
    private String assignedTo;
    private LocalDateTime targetCompletionDate;
}
