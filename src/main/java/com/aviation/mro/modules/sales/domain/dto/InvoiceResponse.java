package com.aviation.mro.modules.sales.domain.dto;

import com.aviation.mro.modules.sales.domain.enums.InvoiceStatus;
import com.aviation.mro.modules.sales.domain.enums.PaymentStatus;
import com.aviation.mro.modules.sales.domain.enums.PaymentMethod;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private Long salesOrderId;
    private String salesOrderNumber;
    private Long customerId;
    private String customerName;
    private InvoiceStatus invoiceStatus;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private LocalDateTime invoiceDate;
    private LocalDateTime dueDate;
    private LocalDateTime paymentDate;
    private Double subtotal;
    private Double taxAmount;
    private Double totalAmount;
    private Double amountPaid;
    private Double balanceDue;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}