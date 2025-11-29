package com.aviation.mro.modules.sales.controller;

import com.aviation.mro.modules.sales.domain.dto.SalesOrderRequest;
import com.aviation.mro.modules.sales.domain.dto.SalesOrderResponse;
import com.aviation.mro.modules.sales.domain.enums.SalesOrderStatus;
import com.aviation.mro.modules.sales.service.SalesOrderService;
import com.aviation.mro.shared.common.ApiResponse;
import com.aviation.mro.shared.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sales/orders")
@RequiredArgsConstructor
@Tag(name = "Sales Order Management", description = "APIs for managing sales orders")
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @PostMapping
    @Operation(summary = "Create a new sales order")
    public ResponseEntity<ApiResponse> createSalesOrder(
            @Valid @RequestBody SalesOrderRequest request,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        SalesOrderResponse response = salesOrderService.createSalesOrder(request, username);
        return ResponseEntity.ok(ApiResponse.success("Sales order created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all sales orders")
    public ResponseEntity<ApiResponse> getAllSalesOrders() {
        List<SalesOrderResponse> orders = salesOrderService.getAllSalesOrders();
        return ResponseEntity.ok(ApiResponse.success("Sales orders retrieved successfully", orders));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sales order by ID")
    public ResponseEntity<ApiResponse> getSalesOrderById(@PathVariable Long id) {
        SalesOrderResponse order = salesOrderService.getSalesOrderById(id);
        return ResponseEntity.ok(ApiResponse.success("Sales order retrieved successfully", order));
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get sales order by number")
    public ResponseEntity<ApiResponse> getSalesOrderByNumber(@PathVariable String orderNumber) {
        SalesOrderResponse order = salesOrderService.getSalesOrderByNumber(orderNumber);
        return ResponseEntity.ok(ApiResponse.success("Sales order retrieved successfully", order));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update sales order status")
    public ResponseEntity<ApiResponse> updateSalesOrderStatus(
            @PathVariable Long id,
            @RequestParam SalesOrderStatus status,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        SalesOrderResponse order = salesOrderService.updateSalesOrderStatus(id, status, username);
        return ResponseEntity.ok(ApiResponse.success("Sales order status updated successfully", order));
    }

    @PutMapping("/{id}/tracking")
    @Operation(summary = "Update tracking number")
    public ResponseEntity<ApiResponse> updateTrackingNumber(
            @PathVariable Long id,
            @RequestParam String trackingNumber,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        SalesOrderResponse order = salesOrderService.updateTrackingNumber(id, trackingNumber, username);
        return ResponseEntity.ok(ApiResponse.success("Tracking number updated successfully", order));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get sales orders by customer")
    public ResponseEntity<ApiResponse> getSalesOrdersByCustomer(@PathVariable Long customerId) {
        List<SalesOrderResponse> orders = salesOrderService.getSalesOrdersByCustomer(customerId);
        return ResponseEntity.ok(ApiResponse.success("Sales orders retrieved successfully", orders));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get sales orders by status")
    public ResponseEntity<ApiResponse> getSalesOrdersByStatus(@PathVariable SalesOrderStatus status) {
        List<SalesOrderResponse> orders = salesOrderService.getSalesOrdersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Sales orders retrieved successfully", orders));
    }
}