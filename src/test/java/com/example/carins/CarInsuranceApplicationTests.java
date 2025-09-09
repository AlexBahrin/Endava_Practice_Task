package com.example.carins;

import com.example.carins.service.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CarInsuranceApplicationTests {

    @Autowired
    CarService service;

    @Autowired
    MockMvc mockMvc;

    @Test
    void insuranceValidityBasic() {
        assertTrue(service.isInsuranceValid(1L, LocalDate.parse("2024-06-01")));
        assertTrue(service.isInsuranceValid(1L, LocalDate.parse("2025-06-01")));
        assertFalse(service.isInsuranceValid(2L, LocalDate.parse("2025-02-01")));
    }

    @Test
    void testValidInsuranceCheck() throws Exception {
        mockMvc.perform(get("/api/cars/1/insurance-valid")
                .param("date", "2025-06-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carId").value(1))
                .andExpect(jsonPath("$.date").value("2025-06-01"))
                .andExpect(jsonPath("$.valid").isBoolean());
    }

    @Test
    void testInvalidCarId() throws Exception {
        mockMvc.perform(get("/api/cars/999/insurance-valid")
                .param("date", "2025-06-01"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Car not found"));
    }

    @Test
    void testInvalidDateFormat() throws Exception {
        mockMvc.perform(get("/api/cars/1/insurance-valid")
                .param("date", "invalid-date"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(""));
    }

    @Test
    void testDateOutOfRange() throws Exception {
        mockMvc.perform(get("/api/cars/1/insurance-valid")
                .param("date", "2022-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Date out of range"));

        mockMvc.perform(get("/api/cars/1/insurance-valid")
                .param("date", "2028-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Date out of range"));
    }
}
