package com.example.carins.web;

import com.example.carins.web.dto.ErrorResponse;
import com.example.carins.web.dto.InsurancePolicyDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class InsurancePolicyController {

    @PostMapping("/insurance-policies")
    public ResponseEntity<?> createInsurancePolicy(@RequestBody InsurancePolicyDto request) {
        if (request.getEndDate() == null) {
            ErrorResponse error = new ErrorResponse(
                "Insurance policy must have an end date. Open-ended policies are not allowed.",
                "endDate",
                400
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        InsurancePolicyDto response = new InsurancePolicyDto(
            999L,
            request.getCarId(),
            request.getProvider(),
            request.getStartDate(),
            request.getEndDate()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/insurance-policies/{policyId}")
    public ResponseEntity<?> updateInsurancePolicy(@PathVariable Long policyId, @RequestBody InsurancePolicyDto request) {
        if (request.getEndDate() == null) {
            ErrorResponse error = new ErrorResponse(
                "Insurance policy must have an end date. Open-ended policies are not allowed.",
                "endDate",
                400
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        InsurancePolicyDto response = new InsurancePolicyDto(
            policyId,
            request.getCarId(),
            request.getProvider(),
            request.getStartDate(),
            request.getEndDate()
        );

        return ResponseEntity.ok(response);
    }
}
