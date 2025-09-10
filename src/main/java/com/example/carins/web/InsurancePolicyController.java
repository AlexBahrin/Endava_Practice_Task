package com.example.carins.web;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.service.InsurancePolicyService;
import com.example.carins.web.dto.ErrorResponse;
import com.example.carins.web.dto.InsurancePolicyDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class InsurancePolicyController {

    private final InsurancePolicyService insurancePolicyService;

    public InsurancePolicyController(InsurancePolicyService insurancePolicyService) {
        this.insurancePolicyService = insurancePolicyService;
    }

    @PostMapping("/insurance-policies")
    public ResponseEntity<?> createInsurancePolicy(@RequestBody InsurancePolicyDto request) {
        try {
            insurancePolicyService.createInsurancePolicy(
                request.getCarId(),
                request.getProvider(),
                request.getStartDate(),
                request.getEndDate()
            );

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            String field = message.contains("end date") ? "endDate" : null;
            int status = message.contains("Car not found") ? 404 : 400;
            
            ErrorResponse error = new ErrorResponse(message, field, status);
            return ResponseEntity.status(HttpStatus.valueOf(status)).body(error);
        }
    }

    @PutMapping("/insurance-policies/{policyId}")
    public ResponseEntity<?> updateInsurancePolicy(@PathVariable Long policyId, @RequestBody InsurancePolicyDto request) {
        try {
            insurancePolicyService.updateInsurancePolicy(
                policyId,
                request.getCarId(),
                request.getProvider(),
                request.getStartDate(),
                request.getEndDate()
            );

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            String field = message.contains("end date") ? "endDate" : null;
            int status = message.contains("not found") ? 404 : 400;
            
            ErrorResponse error = new ErrorResponse(message, field, status);
            return ResponseEntity.status(HttpStatus.valueOf(status)).body(error);
        }
    }
}
