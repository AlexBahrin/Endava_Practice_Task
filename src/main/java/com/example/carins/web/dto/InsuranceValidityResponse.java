package com.example.carins.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response for insurance validity check")
public record InsuranceValidityResponse(
    @Schema(description = "Car ID", example = "1")
    Long carId,
    
    @Schema(description = "Date checked", example = "2025-09-10") 
    String date,
    
    @Schema(description = "Whether insurance is valid", example = "true")
    boolean valid
) {}
