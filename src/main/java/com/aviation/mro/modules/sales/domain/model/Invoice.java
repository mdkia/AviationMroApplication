package com.aviation.mro.modules.sales.domain.model;

import com.aviation.mro.modules.sales.domain.enums.InvoiceStatus;
import com.aviation.mro.modules.sales.domain.enums.PaymentStatus;
import com.aviation.mro.modules.sales.domain.enums.PaymentMethod;
import com.aviation.mro.modules.auth.model.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @Column(unique = true, nullable = false)
    private String invoiceNumber; // Format: INV-YYYY-MM-001

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_id", nullable = false)
    private SalesOrder salesOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus invoiceStatus = InvoiceStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    // Dates
    private LocalDateTime invoiceDate;
    private LocalDateTime dueDate;
    private LocalDateTime paymentDate;

    // Financials
    private Double subtotal;
    private Double taxAmount;
    private Double totalAmount;
    private Double amountPaid;
    private Double balanceDue;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    // Audit
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Helper methods
    public void calculateBalance() {
        this.balanceDue = totalAmount - (amountPaid != null ? amountPaid : 0.0);

        if (balanceDue <= 0) {
            this.paymentStatus = PaymentStatus.PAID;
        } else if (amountPaid != null && amountPaid > 0) {
            this.paymentStatus = PaymentStatus.PARTIALLY_PAID;
        }
    }
}
