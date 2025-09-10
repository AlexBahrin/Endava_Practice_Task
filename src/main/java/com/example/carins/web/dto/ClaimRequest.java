package com.example.carins.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Request body for registering a claim")
public record ClaimRequest(
    @Schema(description = "Date of the claim", example = "2025-09-09")
    @NotNull(message = "Claim date is required")
    LocalDate claimDate,
    
    @Schema(description = "Description of the claim", example = "Minor collision damage")
    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
    String description,
    
    @Schema(description = "Claim amount", example = "1500")
    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be greater than 0")
    Integer amount
) {}
