package com.aviation.mro.modules.accounting.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Nationalized;

@Data
@Entity
@Table(name = "journal_items")
public class JournalItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_entry_id", nullable = false)
    private JournalEntry journalEntry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    private Double debitAmount = 0.0;
    private Double creditAmount = 0.0;

    @Nationalized
    private String description;
    @Nationalized
    private String reference;

    // برای ردیابی دقیق‌تر
    private Long sourceItemId; // ID آیتم در ماژول منبع

    // ترتیب نمایش
    private Integer lineNumber;
}