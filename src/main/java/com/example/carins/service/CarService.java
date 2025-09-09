package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.model.Claim;
import com.example.carins.model.History;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.ClaimRepository;
import com.example.carins.repo.HistoryRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyRepository policyRepository;
    private final ClaimRepository claimRepository;
    private final HistoryRepository historyRepository;

    public CarService(CarRepository carRepository, InsurancePolicyRepository policyRepository, 
                     ClaimRepository claimRepository, HistoryRepository historyRepository) {
        this.carRepository = carRepository;
        this.policyRepository = policyRepository;
        this.claimRepository = claimRepository;
        this.historyRepository = historyRepository;
    }

    public List<Car> listCars() {
        return carRepository.findAll();
    }

    public boolean isInsuranceValid(Long carId, LocalDate date) {
        if (carId == null || date == null) return false;
        
        carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with id: " + carId));
        
        return policyRepository.existsActiveOnDate(carId, date);
    }

    @Transactional
    public Claim registerClaim(Long carId, LocalDate claimDate, String description, int amount) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with id: " + carId));
        
        Claim claim = new Claim(car, claimDate, description, amount);
        Claim savedClaim = claimRepository.save(claim);
        
        // Add history entry for the claim
        History history = new History(car, description, claimDate);
        historyRepository.save(history);
        
        return savedClaim;
    }

    public List<History> getCarHistory(Long carId) {
        // Verify car exists
        carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with id: " + carId));
        
        return historyRepository.findByCarIdOrderByDateAsc(carId);
    }
}
