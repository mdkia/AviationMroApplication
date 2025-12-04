package com.aviation.mro.modules.sales.domain.model;

import com.aviation.mro.modules.sales.domain.enums.QuotationStatus;
import com.aviation.mro.modules.auth.model.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "quotations")
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @Column(unique = true, nullable = false)
    private String quotationNumber; // Format: QTN-YYYY-MM-001

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuotationStatus status = QuotationStatus.DRAFT;

    // Dates
    private LocalDateTime quotationDate;
    private LocalDateTime expiryDate;

    // Financials
    private Double subtotal;
    private Double taxAmount;
    private Double discountAmount;
    private Double totalAmount;

    @Nationalized
    private String termsAndConditions;

    @Nationalized
    private String notes;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuotationItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "quotation")
    private SalesOrder salesOrder;

    // Audit
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Helper methods
    public void addItem(QuotationItem item) {
        items.add(item);
        item.setQuotation(this);
    }

    public void removeItem(QuotationItem item) {
        items.remove(item);
        item.setQuotation(null);
    }

    public void calculateTotals() {
        this.subtotal = items.stream()
                .mapToDouble(QuotationItem::getLineTotal)
                .sum();
        this.totalAmount = subtotal + taxAmount - discountAmount;
    }
}
