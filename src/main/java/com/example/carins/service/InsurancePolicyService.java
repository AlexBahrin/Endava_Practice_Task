package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class InsurancePolicyService {

    private final InsurancePolicyRepository policyRepository;
    private final CarRepository carRepository;

    public InsurancePolicyService(InsurancePolicyRepository policyRepository, CarRepository carRepository) {
        this.policyRepository = policyRepository;
        this.carRepository = carRepository;
    }

    public void createInsurancePolicy(Long carId, String provider, LocalDate startDate, LocalDate endDate) {
        validateEndDate(endDate);
        
        carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with id: " + carId));
    }

    public void updateInsurancePolicy(Long policyId, Long carId, String provider, LocalDate startDate, LocalDate endDate) {
        validateEndDate(endDate);
        
        InsurancePolicy existingPolicy = policyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("Insurance policy not found with id: " + policyId));
        
        if (carId != null && !carId.equals(existingPolicy.getCar().getId())) {
            carRepository.findById(carId)
                    .orElseThrow(() -> new IllegalArgumentException("Car not found with id: " + carId));
        }
    }

    private void validateEndDate(LocalDate endDate) {
        if (endDate == null) {
            throw new IllegalArgumentException("Insurance policy must have an end date. Open-ended policies are not allowed.");
        }
    }
}
