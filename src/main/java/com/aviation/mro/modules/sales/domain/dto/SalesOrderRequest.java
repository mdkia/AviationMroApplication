package com.aviation.mro.modules.sales.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SalesOrderRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private Long quotationId; // Optional - can create order from quotation

    private LocalDateTime expectedDeliveryDate;

    private Double taxAmount = 0.0;
    private Double shippingCost = 0.0;

    private String shippingAddress;
    private String shippingMethod;

    @NotNull(message = "At least one item is required")
    private List<SalesOrderItemRequest> items = new ArrayList<>();
}