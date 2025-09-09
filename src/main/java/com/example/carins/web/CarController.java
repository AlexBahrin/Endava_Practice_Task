package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.model.Claim;
import com.example.carins.service.CarService;
import com.example.carins.web.dto.CarDto;
import com.example.carins.web.dto.ClaimDto;
import com.example.carins.web.dto.ErrorResponse;
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
public class CarController {

    private final CarService service;

    public CarController(CarService service) {
        this.service = service;
    }

    @GetMapping("/cars")
    public List<CarDto> getCars() {
        return service.listCars().stream().map(this::toDto).toList();
    }

    @GetMapping("/cars/{carId}/insurance-valid")
    public ResponseEntity<?> isInsuranceValid(@PathVariable Long carId, @RequestParam String date) {
        // TODO: validate date format and handle errors consistently
        LocalDate d = LocalDate.parse(date);
        boolean valid = service.isInsuranceValid(carId, d);
        return ResponseEntity.ok(new InsuranceValidityResponse(carId, d.toString(), valid));
    }

    @PostMapping("/cars/{carId}/claims")
    public ResponseEntity<?> registerClaim(@PathVariable Long carId, @Valid @RequestBody ClaimRequest request) {
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

    public record InsuranceValidityResponse(Long carId, String date, boolean valid) {}

    public record ClaimRequest(
        @NotNull(message = "Claim date is required")
        LocalDate claimDate,
        
        @NotBlank(message = "Description is required")
        @Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
        String description,
        
        @NotNull(message = "Amount is required")
        @Min(value = 1, message = "Amount must be greater than 0")
        Integer amount
    ) {}
}
