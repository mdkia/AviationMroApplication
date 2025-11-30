package com.aviation.mro.modules.accounting.service;

import com.aviation.mro.modules.accounting.domain.dto.JournalEntryRequest;
import com.aviation.mro.modules.accounting.domain.dto.JournalEntryResponse;
import com.aviation.mro.modules.accounting.domain.model.*;
import com.aviation.mro.modules.accounting.repository.*;
import com.aviation.mro.modules.auth.model.User;
import com.aviation.mro.modules.auth.repository.UserRepository;
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
public class JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;
    private final AccountRepository accountRepository;
    private final JournalItemRepository journalItemRepository;
    private final UserRepository userRepository;

    @Transactional
    public JournalEntryResponse createJournalEntry(JournalEntryRequest request, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        // Generate entry number
        String entryNumber = generateEntryNumber();

        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setEntryNumber(entryNumber);
        journalEntry.setEntryDate(request.getEntryDate());
        journalEntry.setReferenceNumber(request.getReferenceNumber());
        journalEntry.setDescription(request.getDescription());
        journalEntry.setNotes(request.getNotes());
        journalEntry.setSourceModule(request.getSourceModule());
        journalEntry.setSourceId(request.getSourceId());
        journalEntry.setCreatedBy(currentUser);

        // Add items
        for (var itemRequest : request.getItems()) {
            Account account = accountRepository.findById(itemRequest.getAccountId())
                    .orElseThrow(() -> new NotFoundException("Account not found with id: " + itemRequest.getAccountId()));

            JournalItem item = new JournalItem();
            item.setAccount(account);
            item.setDebitAmount(itemRequest.getDebitAmount());
            item.setCreditAmount(itemRequest.getCreditAmount());
            item.setDescription(itemRequest.getDescription());
            item.setReference(itemRequest.getReference());
            item.setLineNumber(itemRequest.getLineNumber());

            journalEntry.addItem(item);
        }

        // Validate that the entry is balanced
        if (!journalEntry.isBalanced()) {
            throw new IllegalStateException(
                    String.format("Journal entry is not balanced. Debit: %.2f, Credit: %.2f",
                            journalEntry.getTotalDebit(), journalEntry.getTotalCredit()));
        }

        JournalEntry savedEntry = journalEntryRepository.save(journalEntry);
        log.info("Journal entry created: {} by user: {}", entryNumber, username);

        return mapToJournalEntryResponse(savedEntry);
    }

    @Transactional
    public JournalEntryResponse postJournalEntry(Long entryId, String username) {
        JournalEntry journalEntry = journalEntryRepository.findById(entryId)
                .orElseThrow(() -> new NotFoundException("Journal entry not found with id: " + entryId));

        if (journalEntry.getIsPosted()) {
            throw new IllegalStateException("Journal entry is already posted: " + journalEntry.getEntryNumber());
        }

        if (!journalEntry.isBalanced()) {
            throw new IllegalStateException("Cannot post unbalanced journal entry: " + journalEntry.getEntryNumber());
        }

        // Update account balances
        for (JournalItem item : journalEntry.getItems()) {
            Account account = item.getAccount();
            account.updateBalance(item.getDebitAmount(), item.getCreditAmount());
            accountRepository.save(account);
        }

        journalEntry.setIsPosted(true);
        journalEntry.setPostedDate(LocalDateTime.now());

        JournalEntry postedEntry = journalEntryRepository.save(journalEntry);
        log.info("Journal entry posted: {} by user: {}", journalEntry.getEntryNumber(), username);

        return mapToJournalEntryResponse(postedEntry);
    }

    @Transactional(readOnly = true)
    public List<JournalEntryResponse> getAllJournalEntries() {
        return journalEntryRepository.findAll().stream()
                .map(this::mapToJournalEntryResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public JournalEntryResponse getJournalEntryById(Long id) {
        JournalEntry journalEntry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Journal entry not found with id: " + id));
        return mapToJournalEntryResponse(journalEntry);
    }

    @Transactional(readOnly = true)
    public JournalEntryResponse getJournalEntryByNumber(String entryNumber) {
        JournalEntry journalEntry = journalEntryRepository.findByEntryNumber(entryNumber)
                .orElseThrow(() -> new NotFoundException("Journal entry not found: " + entryNumber));
        return mapToJournalEntryResponse(journalEntry);
    }

    @Transactional(readOnly = true)
    public List<JournalEntryResponse> getJournalEntriesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return journalEntryRepository.findByEntryDateBetween(startDate, endDate).stream()
                .map(this::mapToJournalEntryResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<JournalEntryResponse> getPostedJournalEntries() {
        return journalEntryRepository.findByIsPosted(true).stream()
                .map(this::mapToJournalEntryResponse)
                .collect(Collectors.toList());
    }

    // Helper method to generate entry number
    private String generateEntryNumber() {
        LocalDateTime now = LocalDateTime.now();
        String baseNumber = "JE-" + now.getYear() + "-" + String.format("%02d", now.getMonthValue());

        long count = journalEntryRepository.count() + 1;
        return baseNumber + "-" + String.format("%03d", count);
    }

    // Mapping helper
    private JournalEntryResponse mapToJournalEntryResponse(JournalEntry journalEntry) {
        JournalEntryResponse response = new JournalEntryResponse();
        response.setId(journalEntry.getId());
        response.setEntryNumber(journalEntry.getEntryNumber());
        response.setEntryDate(journalEntry.getEntryDate());
        response.setReferenceNumber(journalEntry.getReferenceNumber());
        response.setDescription(journalEntry.getDescription());
        response.setNotes(journalEntry.getNotes());
        response.setTotalDebit(journalEntry.getTotalDebit());
        response.setTotalCredit(journalEntry.getTotalCredit());
        response.setIsPosted(journalEntry.getIsPosted());
        response.setPostedDate(journalEntry.getPostedDate());
        response.setSourceModule(journalEntry.getSourceModule());
        response.setSourceId(journalEntry.getSourceId());
        response.setCreatedAt(journalEntry.getCreatedAt());
        response.setUpdatedAt(journalEntry.getUpdatedAt());

        if (journalEntry.getCreatedBy() != null) {
            response.setCreatedBy(journalEntry.getCreatedBy().getUsername());
        }

        // Map items
        if (journalEntry.getItems() != null) {
            List<JournalEntryResponse.JournalItemResponse> itemResponses = journalEntry.getItems().stream()
                    .map(item -> {
                        JournalEntryResponse.JournalItemResponse itemResponse = new JournalEntryResponse.JournalItemResponse();
                        itemResponse.setId(item.getId());
                        itemResponse.setAccountId(item.getAccount().getId());
                        itemResponse.setAccountCode(item.getAccount().getAccountCode());
                        itemResponse.setAccountName(item.getAccount().getAccountName());
                        itemResponse.setDebitAmount(item.getDebitAmount());
                        itemResponse.setCreditAmount(item.getCreditAmount());
                        itemResponse.setDescription(item.getDescription());
                        itemResponse.setReference(item.getReference());
                        itemResponse.setLineNumber(item.getLineNumber());
                        return itemResponse;
                    })
                    .collect(Collectors.toList());
            response.setItems(itemResponses);
        }

        return response;
    }
}
