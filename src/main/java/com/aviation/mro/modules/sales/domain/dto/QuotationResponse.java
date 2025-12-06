package com.aviation.mro.modules.sales.domain.dto;

import com.aviation.mro.modules.sales.domain.enums.QuotationStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class QuotationResponse {
    private Long id;
    private String quotationNumber;
    private Long customerId;
    private String customerName;
    private String customerCode;
    private QuotationStatus status;
    private LocalDateTime quotationDate;
    private LocalDateTime expiryDate;
    private Double subtotal;
    private Double taxAmount;
    private Double discountAmount;
    private Double totalAmount;
    private String termsAndConditions;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<QuotationItemResponse> items = new ArrayList<>();

    @Data
    public static class QuotationItemResponse {
        private Long id;
        private Long partId;
        private String partNumber;
        private String partDescription;
        private Integer quantity;
        private Double unitPrice;
        private Double discountPercentage;
        private Double discountAmount;
        private Double lineTotal;
    }
}