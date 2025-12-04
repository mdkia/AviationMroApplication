package com.aviation.mro.modules.accounting.domain.model;

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
@Table(name = "journal_entries")
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @Column(unique = true, nullable = false)
    private String entryNumber; // فرمت: JE-YYYY-MM-001

    @Column(nullable = false)
    private LocalDateTime entryDate;

    @Nationalized
    private String referenceNumber; // شماره مرجع (شماره فاکتور، etc.)

    @Nationalized
    private String description;

    @Nationalized
    private String notes;

    // مبالغ کل
    private Double totalDebit = 0.0;
    private Double totalCredit = 0.0;

    // وضعیت
    private Boolean isPosted = false; // آیا سند ثبت شده؟
    private LocalDateTime postedDate;

    @Nationalized
    // منبع سند (اتوماتیک/دستی)
    private String sourceModule; // SALES, REPAIR, PURCHASE, MANUAL

    // ارتباط با ماژول‌های دیگر
    private Long sourceId; // ID سند در ماژول منبع

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JournalItem> items = new ArrayList<>();

    // Audit
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Helper methods
    public void addItem(JournalItem item) {
        items.add(item);
        item.setJournalEntry(this);
        calculateTotals();
    }

    public void calculateTotals() {
        this.totalDebit = items.stream()
                .filter(item -> item.getDebitAmount() != null)
                .mapToDouble(JournalItem::getDebitAmount)
                .sum();

        this.totalCredit = items.stream()
                .filter(item -> item.getCreditAmount() != null)
                .mapToDouble(JournalItem::getCreditAmount)
                .sum();
    }

    public boolean isBalanced() {
        return totalDebit.equals(totalCredit);
    }
}
