package com.aviation.mro.modules.sales.service;

import com.aviation.mro.modules.sales.domain.dto.*;
import com.aviation.mro.modules.sales.domain.model.*;
import com.aviation.mro.modules.sales.domain.enums.QuotationStatus;
import com.aviation.mro.modules.sales.repository.*;
import com.aviation.mro.modules.auth.model.User;
import com.aviation.mro.modules.auth.repository.UserRepository;
import com.aviation.mro.modules.parts.domain.model.AircraftPart;
import com.aviation.mro.modules.parts.repository.AircraftPartRepository;
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
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final AircraftPartRepository aircraftPartRepository;
    private final QuotationItemRepository quotationItemRepository;

    @Transactional
    public QuotationResponse createQuotation(QuotationRequest request, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + request.getCustomerId()));

        // Generate quotation number
        String quotationNumber = generateQuotationNumber();

        Quotation quotation = new Quotation();
        quotation.setQuotationNumber(quotationNumber);
        quotation.setCustomer(customer);
        quotation.setQuotationDate(LocalDateTime.now());
        quotation.setExpiryDate(request.getExpiryDate());
        quotation.setTaxAmount(request.getTaxAmount());
        quotation.setDiscountAmount(request.getDiscountAmount());
        quotation.setTermsAndConditions(request.getTermsAndConditions());
        quotation.setNotes(request.getNotes());
        quotation.setCreatedBy(currentUser);

        // Add items
        for (QuotationItemRequest itemRequest : request.getItems()) {
            AircraftPart part = aircraftPartRepository.findById(itemRequest.getPartId())
                    .orElseThrow(() -> new NotFoundException("Part not found with id: " + itemRequest.getPartId()));

            QuotationItem item = new QuotationItem();
            item.setPart(part);
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setDiscountPercentage(itemRequest.getDiscountPercentage());

            quotation.addItem(item);
        }

        // Calculate totals
        quotation.calculateTotals();

        Quotation savedQuotation = quotationRepository.save(quotation);
        log.info("Quotation created: {} for customer: {} by user: {}",
                quotationNumber, customer.getCompanyName(), username);

        return mapToQuotationResponse(savedQuotation);
    }

    @Transactional(readOnly = true)
    public List<QuotationResponse> getAllQuotations() {
        return quotationRepository.findAll().stream()
                .map(this::mapToQuotationResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QuotationResponse getQuotationById(Long id) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Quotation not found with id: " + id));
        return mapToQuotationResponse(quotation);
    }

    @Transactional(readOnly = true)
    public QuotationResponse getQuotationByNumber(String quotationNumber) {
        Quotation quotation = quotationRepository.findByQuotationNumber(quotationNumber)
                .orElseThrow(() -> new NotFoundException("Quotation not found: " + quotationNumber));
        return mapToQuotationResponse(quotation);
    }

    @Transactional
    public QuotationResponse updateQuotationStatus(Long id, QuotationStatus status, String username) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Quotation not found with id: " + id));

        quotation.setStatus(status);
        Quotation updatedQuotation = quotationRepository.save(quotation);
        log.info("Quotation status updated: {} to {} by user: {}",
                quotation.getQuotationNumber(), status, username);

        return mapToQuotationResponse(updatedQuotation);
    }

    @Transactional(readOnly = true)
    public List<QuotationResponse> getQuotationsByCustomer(Long customerId) {
        return quotationRepository.findByCustomerId(customerId).stream()
                .map(this::mapToQuotationResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuotationResponse> getQuotationsByStatus(QuotationStatus status) {
        return quotationRepository.findByStatus(status).stream()
                .map(this::mapToQuotationResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public QuotationResponse convertToSalesOrder(Long quotationId, String username) {
        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new NotFoundException("Quotation not found with id: " + quotationId));

        if (quotation.getStatus() != QuotationStatus.ACCEPTED) {
            throw new IllegalStateException("Only accepted quotations can be converted to sales orders");
        }

        quotation.setStatus(QuotationStatus.CONVERTED_TO_ORDER);
        Quotation updatedQuotation = quotationRepository.save(quotation);
        log.info("Quotation converted to sales order: {} by user: {}",
                quotation.getQuotationNumber(), username);

        return mapToQuotationResponse(updatedQuotation);
    }

    // Helper method to generate quotation number
    private String generateQuotationNumber() {
        LocalDateTime now = LocalDateTime.now();
        String baseNumber = "QTN-" + now.getYear() + "-" + String.format("%02d", now.getMonthValue());

        long count = quotationRepository.count() + 1;
        return baseNumber + "-" + String.format("%03d", count);
    }

    // Mapping helper
    private QuotationResponse mapToQuotationResponse(Quotation quotation) {
        QuotationResponse response = new QuotationResponse();
        response.setId(quotation.getId());
        response.setQuotationNumber(quotation.getQuotationNumber());
        response.setCustomerId(quotation.getCustomer().getId());
        response.setCustomerName(quotation.getCustomer().getCompanyName());
        response.setCustomerCode(quotation.getCustomer().getCustomerCode());
        response.setStatus(quotation.getStatus());
        response.setQuotationDate(quotation.getQuotationDate());
        response.setExpiryDate(quotation.getExpiryDate());
        response.setSubtotal(quotation.getSubtotal());
        response.setTaxAmount(quotation.getTaxAmount());
        response.setDiscountAmount(quotation.getDiscountAmount());
        response.setTotalAmount(quotation.getTotalAmount());
        response.setTermsAndConditions(quotation.getTermsAndConditions());
        response.setNotes(quotation.getNotes());
        response.setCreatedAt(quotation.getCreatedAt());
        response.setUpdatedAt(quotation.getUpdatedAt());

        if (quotation.getCreatedBy() != null) {
            response.setCreatedBy(quotation.getCreatedBy().getUsername());
        }

        // Map items
        if (quotation.getItems() != null) {
            List<QuotationResponse.QuotationItemResponse> itemResponses = quotation.getItems().stream()
                    .map(item -> {
                        QuotationResponse.QuotationItemResponse itemResponse = new QuotationResponse.QuotationItemResponse();
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
