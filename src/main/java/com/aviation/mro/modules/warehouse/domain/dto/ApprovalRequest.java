package com.aviation.mro.modules.warehouse.domain.dto;

public class ApprovalRequest {
    private String notes;
    private String rejectionReason;

    // Getters and Setters
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
