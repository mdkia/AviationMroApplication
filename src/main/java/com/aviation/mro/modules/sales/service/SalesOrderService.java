package com.aviation.mro.modules.sales.service;

import com.aviation.mro.modules.sales.domain.dto.*;
import com.aviation.mro.modules.sales.domain.model.*;
import com.aviation.mro.modules.sales.domain.enums.SalesOrderStatus;
import com.aviation.mro.modules.sales.repository.*;
import com.aviation.mro.modules.auth.model.User;
import com.aviation.mro.modules.auth.repository.UserRepository;
import com.aviation.mro.modules.parts.domain.model.AircraftPart;
import com.aviation.mro.modules.parts.repository.AircraftPartRepository;
import com.aviation.mro.modules.warehouse.domain.model.InventoryItem;
import com.aviation.mro.modules.warehouse.repository.InventoryItemRepository;
import com.aviation.mro.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final CustomerRepository customerRepository;
    private final QuotationRepository quotationRepository;
    private final UserRepository userRepository;
    private final AircraftPartRepository aircraftPartRepository;
    private final InventoryItemRepository inventoryItemRepository;

    @Transactional
    public SalesOrderResponse createSalesOrder(SalesOrderRequest request, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + request.getCustomerId()));

        // Generate order number
        String orderNumber = generateOrderNumber();

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setOrderNumber(orderNumber);
        salesOrder.setCustomer(customer);
        salesOrder.setOrderDate(LocalDateTime.now());
        salesOrder.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        salesOrder.setTaxAmount(request.getTaxAmount());
        salesOrder.setShippingCost(request.getShippingCost());
        salesOrder.setShippingAddress(request.getShippingAddress());
        salesOrder.setShippingMethod(request.getShippingMethod());
        salesOrder.setCreatedBy(currentUser);

        // Link to quotation if provided
        if (request.getQuotationId() != null) {
            Quotation quotation = quotationRepository.findById(request.getQuotationId())
                    .orElseThrow(() -> new NotFoundException("Quotation not found with id: " + request.getQuotationId()));
            salesOrder.setQuotation(quotation);
        }

        // Add items and check inventory
        for (SalesOrderItemRequest itemRequest : request.getItems()) {
            AircraftPart part = aircraftPartRepository.findById(itemRequest.getPartId())
                    .orElseThrow(() -> new NotFoundException("Part not found with id: " + itemRequest.getPartId()));

            // Check inventory availability
            checkInventoryAvailability(part, itemRequest.getQuantity());

            SalesOrderItem item = new SalesOrderItem();
            item.setPart(part);
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setDiscountPercentage(itemRequest.getDiscountPercentage());

            salesOrder.addItem(item);
        }

        // Calculate totals
        salesOrder.calculateTotals();

        SalesOrder savedOrder = salesOrderRepository.save(salesOrder);
        log.info("Sales order created: {} for customer: {} by user: {}",
                orderNumber, customer.getCompanyName(), username);

        return mapToSalesOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<SalesOrderResponse> getAllSalesOrders() {
        return salesOrderRepository.findAll().stream()
                .map(this::mapToSalesOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SalesOrderResponse getSalesOrderById(Long id) {
        SalesOrder salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sales order not found with id: " + id));
        return mapToSalesOrderResponse(salesOrder);
    }

    @Transactional(readOnly = true)
    public SalesOrderResponse getSalesOrderByNumber(String orderNumber) {
        SalesOrder salesOrder = salesOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new NotFoundException("Sales order not found: " + orderNumber));
        return mapToSalesOrderResponse(salesOrder);
    }

    @Transactional
    public SalesOrderResponse updateSalesOrderStatus(Long id, SalesOrderStatus status, String username) {
        SalesOrder salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sales order not found with id: " + id));

        salesOrder.setStatus(status);

        // Set actual delivery date if status is DELIVERED
        if (status == SalesOrderStatus.DELIVERED && salesOrder.getActualDeliveryDate() == null) {
            salesOrder.setActualDeliveryDate(LocalDateTime.now());
        }

        SalesOrder updatedOrder = salesOrderRepository.save(salesOrder);
        log.info("Sales order status updated: {} to {} by user: {}",
                salesOrder.getOrderNumber(), status, username);

        return mapToSalesOrderResponse(updatedOrder);
    }

    @Transactional
    public SalesOrderResponse updateTrackingNumber(Long id, String trackingNumber, String username) {
        SalesOrder salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sales order not found with id: " + id));

        salesOrder.setTrackingNumber(trackingNumber);
        salesOrder.setStatus(SalesOrderStatus.SHIPPED);

        SalesOrder updatedOrder = salesOrderRepository.save(salesOrder);
        log.info("Tracking number updated for sales order: {} by user: {}",
                salesOrder.getOrderNumber(), username);

        return mapToSalesOrderResponse(updatedOrder);
    }

    @Transactional(readOnly = true)
    public List<SalesOrderResponse> getSalesOrdersByCustomer(Long customerId) {
        return salesOrderRepository.findByCustomerId(customerId).stream()
                .map(this::mapToSalesOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SalesOrderResponse> getSalesOrdersByStatus(SalesOrderStatus status) {
        return salesOrderRepository.findByStatus(status).stream()
                .map(this::mapToSalesOrderResponse)
                .collect(Collectors.toList());
    }

    // Helper method to check inventory availability
    private void checkInventoryAvailability(AircraftPart part, Integer requestedQuantity) {
        List<InventoryItem> inventoryItems = inventoryItemRepository.findByPartId(part.getId());

        int availableQuantity = inventoryItems.stream()
                .filter(item -> item.getQuantityAvailable() > 0) // فقط موارد با موجودی مثبت
                .mapToInt(InventoryItem::getQuantityAvailable) // استفاده از متد موجود
                .sum();

        if (availableQuantity < requestedQuantity) {
            throw new IllegalStateException(
                    String.format("Insufficient inventory for part %s. Requested: %d, Available: %d",
                            part.getPartNumber(), requestedQuantity, availableQuantity));
        }
    }

    // Helper method to generate order number
    private String generateOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        String baseNumber = "SO-" + now.getYear() + "-" + String.format("%02d", now.getMonthValue());

        long count = salesOrderRepository.count() + 1;
        return baseNumber + "-" + String.format("%03d", count);
    }

    // Mapping helper
    private SalesOrderResponse mapToSalesOrderResponse(SalesOrder salesOrder) {
        SalesOrderResponse response = new SalesOrderResponse();
        response.setId(salesOrder.getId());
        response.setOrderNumber(salesOrder.getOrderNumber());
        response.setCustomerId(salesOrder.getCustomer().getId());
        response.setCustomerName(salesOrder.getCustomer().getCompanyName());
        response.setCustomerCode(salesOrder.getCustomer().getCustomerCode());
        response.setStatus(salesOrder.getStatus());
        response.setOrderDate(salesOrder.getOrderDate());
        response.setExpectedDeliveryDate(salesOrder.getExpectedDeliveryDate());
        response.setActualDeliveryDate(salesOrder.getActualDeliveryDate());
        response.setSubtotal(salesOrder.getSubtotal());
        response.setTaxAmount(salesOrder.getTaxAmount());
        response.setShippingCost(salesOrder.getShippingCost());
        response.setTotalAmount(salesOrder.getTotalAmount());
        response.setShippingAddress(salesOrder.getShippingAddress());
        response.setShippingMethod(salesOrder.getShippingMethod());
        response.setTrackingNumber(salesOrder.getTrackingNumber());
        response.setCreatedAt(salesOrder.getCreatedAt());
        response.setUpdatedAt(salesOrder.getUpdatedAt());

        if (salesOrder.getCreatedBy() != null) {
            response.setCreatedBy(salesOrder.getCreatedBy().getUsername());
        }

        if (salesOrder.getQuotation() != null) {
            response.setQuotationId(salesOrder.getQuotation().getId());
            response.setQuotationNumber(salesOrder.getQuotation().getQuotationNumber());
        }

        // Map items
        if (salesOrder.getItems() != null) {
            List<SalesOrderResponse.SalesOrderItemResponse> itemResponses = salesOrder.getItems().stream()
                    .map(item -> {
                        SalesOrderResponse.SalesOrderItemResponse itemResponse = new SalesOrderResponse.SalesOrderItemResponse();
                        itemResponse.setId(item.getId());
                        itemResponse.setPartId(item.getPart().getId());
                        itemResponse.setPartNumber(item.getPart().getPartNumber());
                        itemResponse.setPartDescription(item.getPart().getDescription());
                        itemResponse.setQuantity(item.getQuantity());
                        itemResponse.setUnitPrice(item.getUnitPrice());
                        itemResponse.setDiscountPercentage(item.getDiscountPercentage());
                        itemResponse.setDiscountAmount(item.getDiscountAmount());
                        itemResponse.setLineTotal(item.getLineTotal());
                        return itemResponse;
                    })
                    .collect(Collectors.toList());
            response.setItems(itemResponses);
        }

        return response;
    }
}