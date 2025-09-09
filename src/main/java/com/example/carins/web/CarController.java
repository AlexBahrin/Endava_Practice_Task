package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.model.Claim;
import com.example.carins.model.History;
import com.example.carins.service.CarService;
import com.example.carins.web.dto.CarDto;
import com.example.carins.web.dto.ClaimDto;
import com.example.carins.web.dto.ErrorResponse;
import com.example.carins.web.dto.HistoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Car Insurance", description = "Car insurance management operations")
public class CarController {

    private final CarService service;

    public CarController(CarService service) {
        this.service = service;
    }

    @Operation(summary = "Get all cars")
    @GetMapping("/cars")
    public List<CarDto> getCars() {
        return service.listCars().stream().map(this::toDto).toList();
    }

    @Operation(summary = "Check insurance validity")
    @GetMapping("/cars/{carId}/insurance-valid")
    public ResponseEntity<?> isInsuranceValid(
            @Parameter(description = "Car ID") @PathVariable Long carId, 
            @Parameter(description = "Date (YYYY-MM-DD)") @RequestParam String date) {
        // TODO: validate date format and handle errors consistently
        LocalDate d = LocalDate.parse(date);
        boolean valid = service.isInsuranceValid(carId, d);
        return ResponseEntity.ok(new InsuranceValidityResponse(carId, d.toString(), valid));
    }

    @Operation(summary = "Register insurance claim")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Claim registered successfully",
                    content = @Content(schema = @Schema(implementation = ClaimDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Car not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/cars/{carId}/claims")
    public ResponseEntity<?> registerClaim(
            @Parameter(description = "Car ID") @PathVariable Long carId, 
            @Valid @RequestBody ClaimRequest request) {
        try {
            Claim claim = service.registerClaim(carId, request.claimDate(), request.description(), request.amount());
            ClaimDto claimDto = toClaimDto(claim);
            
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(claim.getId())
                    .toUri();
            
            return ResponseEntity.created(location).body(claimDto);
        } catch (IllegalArgumentException e) {
            ErrorResponse error = new ErrorResponse("Car not found", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @Operation(summary = "Get car history")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Car history retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Car not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/cars/{carId}/history")
    public ResponseEntity<?> getCarHistory(@Parameter(description = "Car ID") @PathVariable Long carId) {
        try {
            List<History> history = service.getCarHistory(carId);
            List<HistoryDto> historyDtos = history.stream().map(this::toHistoryDto).toList();
            return ResponseEntity.ok(historyDtos);
        } catch (IllegalArgumentException e) {
            ErrorResponse error = new ErrorResponse("Car not found", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String field = fieldError != null ? fieldError.getField() : "unknown";
        String message = fieldError != null ? fieldError.getDefaultMessage() : "Validation failed";
        
        // Create specific error messages based on field
        String errorMessage = switch (field) {
            case "claimDate" -> "Invalid claim date";
            case "description" -> "Invalid description";
            case "amount" -> "Invalid amount";
            default -> "Invalid " + field;
        };
        
        ErrorResponse error = new ErrorResponse(errorMessage, field, 400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private CarDto toDto(Car c) {
        var o = c.getOwner();
        return new CarDto(c.getId(), c.getVin(), c.getMake(), c.getModel(), c.getYearOfManufacture(),
                o != null ? o.getId() : null,
                o != null ? o.getName() : null,
                o != null ? o.getEmail() : null);
    }

    private ClaimDto toClaimDto(Claim claim) {
        return new ClaimDto(
            claim.getId(),
            claim.getCar().getId(),
            claim.getClaimDate(),
            claim.getDescription(),
            claim.getAmount()
        );
    }

    private HistoryDto toHistoryDto(History history) {
        return new HistoryDto(
            history.getId(),
            history.getCar().getId(),
            history.getEvent(),
            history.getDate()
        );
    }

    public record InsuranceValidityResponse(Long carId, String date, boolean valid) {}

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
}
