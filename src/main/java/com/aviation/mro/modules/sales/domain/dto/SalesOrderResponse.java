package com.aviation.mro.modules.sales.domain.dto;

import com.aviation.mro.modules.sales.domain.enums.SalesOrderStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class SalesOrderResponse {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private String customerName;
    private String customerCode;
    private Long quotationId;
    private String quotationNumber;
    private SalesOrderStatus status;
    private LocalDateTime orderDate;
    private LocalDateTime expectedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private Double subtotal;
    private Double taxAmount;
    private Double shippingCost;
    private Double totalAmount;
    private String shippingAddress;
    private String shippingMethod;
    private String trackingNumber;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SalesOrderItemResponse> items = new ArrayList<>();

    @Data
    public static class SalesOrderItemResponse {
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
