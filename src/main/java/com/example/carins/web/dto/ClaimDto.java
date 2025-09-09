package com.example.carins.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Insurance claim data")
public record ClaimDto(
    @Schema(description = "Claim ID", example = "1")
    Long id,
    @Schema(description = "Car ID", example = "1")
    Long carId,
    @Schema(description = "Claim date", example = "2025-09-09")
    LocalDate claimDate,
    @Schema(description = "Claim description", example = "Minor collision damage")
    String description,
    @Schema(description = "Claim amount", example = "1500")
    int amount
) {}
