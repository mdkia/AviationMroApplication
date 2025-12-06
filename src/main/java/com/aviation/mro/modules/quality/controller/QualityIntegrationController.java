package com.aviation.mro.modules.quality.controller;

import com.aviation.mro.modules.quality.service.QualityRepairReportService;
import com.aviation.mro.shared.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/quality/integration")
@RequiredArgsConstructor
@Tag(name = "Quality Integration", description = "APIs for quality module integration reports")
public class QualityIntegrationController {

    private final QualityRepairReportService qualityRepairReportService;

    @GetMapping("/repair-stats")
    @Operation(summary = "Get quality-repair integration statistics")
    public ResponseEntity<ApiResponse> getQualityRepairIntegrationStats() {
        Map<String, Object> report = qualityRepairReportService.getQualityRepairIntegrationReport();
        return ResponseEntity.ok(ApiResponse.success("Integration statistics retrieved successfully", report));
    }
}
