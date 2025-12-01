// modules/quality/domain/dto/NonConformanceResponse.java
package com.aviation.mro.modules.quality.domain.dto;

import com.aviation.mro.modules.quality.domain.enums.NCRStatus;
import com.aviation.mro.modules.quality.domain.enums.CorrectiveActionStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NonConformanceResponse {
    private Long id;
    private String ncrNumber;
    private Long inspectionId;
    private String inspectionNumber;
    private Long partId;
    private String partNumber;
    private NCRStatus status;
    private CorrectiveActionStatus correctiveActionStatus;
    private String problemDescription;
    private String rootCause;
    private String immediateAction;
    private String correctiveAction;
    private String preventiveAction;
    private String raisedBy;
    private String assignedTo;
    private String verifiedBy;
    private LocalDateTime targetCompletionDate;
    private LocalDateTime actualCompletionDate;
    private LocalDateTime verificationDate;
    private Boolean isEffective;
    private String verificationNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
