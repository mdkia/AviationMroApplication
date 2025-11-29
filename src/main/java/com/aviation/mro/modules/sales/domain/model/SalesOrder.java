package com.aviation.mro.modules.sales.domain.model;

import com.aviation.mro.modules.sales.domain.enums.SalesOrderStatus;
import com.aviation.mro.modules.auth.model.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "sales_orders")
public class SalesOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderNumber; // Format: SO-YYYY-MM-001

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id")
    private Quotation quotation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SalesOrderStatus status = SalesOrderStatus.PENDING;

    // Dates
    private LocalDateTime orderDate;
    private LocalDateTime expectedDeliveryDate;
    private LocalDateTime actualDeliveryDate;

    // Financials
    private Double subtotal;
    private Double taxAmount;
    private Double shippingCost;
    private Double totalAmount;

    // Shipping
    private String shippingAddress;
    private String shippingMethod;
    private String trackingNumber;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalesOrderItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "salesOrder")
    private Invoice invoice;

    // Audit
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Helper methods
    public void addItem(SalesOrderItem item) {
        items.add(item);
        item.setSalesOrder(this);
    }

    public void calculateTotals() {
        this.subtotal = items.stream()
                .mapToDouble(SalesOrderItem::getLineTotal)
                .sum();
        this.totalAmount = subtotal + taxAmount + shippingCost;
    }
}
