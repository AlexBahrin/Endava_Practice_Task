package com.example.carins.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Car history event data")
public record HistoryDto(
    @Schema(description = "History entry ID", example = "1")
    Long id,
    @Schema(description = "Car ID", example = "1")
    Long carId,
    @Schema(description = "Event description", example = "Minor collision damage")
    String event,
    @Schema(description = "Event date", example = "2025-09-09")
    LocalDate date
) {}
