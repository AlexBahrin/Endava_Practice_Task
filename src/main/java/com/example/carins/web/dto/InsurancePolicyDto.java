package com.example.carins.web.dto;

import java.time.LocalDate;

public class InsurancePolicyDto {
    private Long id;
    private Long carId;
    private String provider;
    private LocalDate startDate;
    private LocalDate endDate;

    public InsurancePolicyDto() {}

    public InsurancePolicyDto(Long id, Long carId, String provider, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.carId = carId;
        this.provider = provider;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
