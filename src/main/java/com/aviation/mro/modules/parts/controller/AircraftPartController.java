package com.aviation.mro.modules.parts.controller;

import com.aviation.mro.modules.parts.domain.model.AircraftPart;
import com.aviation.mro.modules.parts.service.AircraftPartService;
import com.aviation.mro.shared.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parts")
public class AircraftPartController {

    private final AircraftPartService partService;

    public AircraftPartController(AircraftPartService partService) {
        this.partService = partService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN', 'INSPECTOR', 'WAREHOUSE_MANAGER', 'SALES_MANAGER', 'ACCOUNTANT', 'READ_ONLY')")
    public ResponseEntity<List<AircraftPart>> getAllParts() {
        return ResponseEntity.ok(partService.getAllParts());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<AircraftPart> createPart(@RequestBody AircraftPart part) {
        return ResponseEntity.ok(partService.createPart(part));
    }

//    @PutMapping("/{id}/status")
//    @PreAuthorize("hasAnyRole('ADMIN', 'INSPECTOR')")
//    public ResponseEntity<AircraftPart> updatePartStatus(
//            @PathVariable Long id,
//            @RequestBody PartStatusUpdate request) {
//        return ResponseEntity.ok(partService.updatePartStatus(id, request));
//    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSPECTOR')")
    public ResponseEntity<AircraftPart> updatePart(
            @PathVariable Long id,
            @RequestBody AircraftPart request) {
        return ResponseEntity.ok(partService.updatePart(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deletePart(@PathVariable Long id) {
        partService.deletePart(id);
        return ResponseEntity.ok(ApiResponse.success("Part deleted successfully"));
    }
}