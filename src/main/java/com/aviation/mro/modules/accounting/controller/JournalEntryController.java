package com.aviation.mro.modules.accounting.controller;

import com.aviation.mro.modules.accounting.domain.dto.JournalEntryRequest;
import com.aviation.mro.modules.accounting.domain.dto.JournalEntryResponse;
import com.aviation.mro.modules.accounting.service.JournalEntryService;
import com.aviation.mro.shared.common.ApiResponse;
import com.aviation.mro.shared.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/accounting/journal-entries")
@RequiredArgsConstructor
@Tag(name = "Journal Entry Management", description = "APIs for managing journal entries")
public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    @PostMapping
    @Operation(summary = "Create a new journal entry")
    public ResponseEntity<ApiResponse> createJournalEntry(
            @Valid @RequestBody JournalEntryRequest request,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        JournalEntryResponse response = journalEntryService.createJournalEntry(request, username);
        return ResponseEntity.ok(ApiResponse.success("Journal entry created successfully", response));
    }

    @PostMapping("/{entryId}/post")
    @Operation(summary = "Post journal entry")
    public ResponseEntity<ApiResponse> postJournalEntry(
            @PathVariable Long entryId,
            Authentication authentication) {
        String username = SecurityUtils.getCurrentUsername(authentication);
        JournalEntryResponse response = journalEntryService.postJournalEntry(entryId, username);
        return ResponseEntity.ok(ApiResponse.success("Journal entry posted successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all journal entries")
    public ResponseEntity<ApiResponse> getAllJournalEntries() {
        List<JournalEntryResponse> entries = journalEntryService.getAllJournalEntries();
        return ResponseEntity.ok(ApiResponse.success("Journal entries retrieved successfully", entries));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get journal entry by ID")
    public ResponseEntity<ApiResponse> getJournalEntryById(@PathVariable Long id) {
        JournalEntryResponse entry = journalEntryService.getJournalEntryById(id);
        return ResponseEntity.ok(ApiResponse.success("Journal entry retrieved successfully", entry));
    }

    @GetMapping("/number/{entryNumber}")
    @Operation(summary = "Get journal entry by number")
    public ResponseEntity<ApiResponse> getJournalEntryByNumber(@PathVariable String entryNumber) {
        JournalEntryResponse entry = journalEntryService.getJournalEntryByNumber(entryNumber);
        return ResponseEntity.ok(ApiResponse.success("Journal entry retrieved successfully", entry));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get journal entries by date range")
    public ResponseEntity<ApiResponse> getJournalEntriesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<JournalEntryResponse> entries = journalEntryService.getJournalEntriesByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Journal entries retrieved successfully", entries));
    }

    @GetMapping("/posted")
    @Operation(summary = "Get posted journal entries")
    public ResponseEntity<ApiResponse> getPostedJournalEntries() {
        List<JournalEntryResponse> entries = journalEntryService.getPostedJournalEntries();
        return ResponseEntity.ok(ApiResponse.success("Posted journal entries retrieved successfully", entries));
    }
}
