package com.example.carins.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
public class ValidationService {
    public LocalDate validateAndParseDate(String dateString) {
        LocalDate date = LocalDate.parse(dateString);
        
        LocalDate now = LocalDate.now();
        LocalDate minDate = now.minusYears(2);
        LocalDate maxDate = now.plusYears(2);
        
        if (date.isBefore(minDate) || date.isAfter(maxDate)) {
            throw new IllegalArgumentException("Date out of range");
        }
        
        return date;
    }
}
