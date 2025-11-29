package com.aviation.mro.modules.sales.domain.model;

import com.aviation.mro.modules.parts.domain.model.AircraftPart;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "quotation_items")
public class QuotationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id", nullable = false)
    private Quotation quotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private AircraftPart part;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double unitPrice;

    private Double discountPercentage = 0.0;

    // Computed
    public Double getDiscountAmount() {
        return (unitPrice * quantity * discountPercentage) / 100;
    }

    public Double getLineTotal() {
        return (unitPrice * quantity) - getDiscountAmount();
    }
}