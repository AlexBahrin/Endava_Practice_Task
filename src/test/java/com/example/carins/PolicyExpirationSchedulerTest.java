package com.example.carins;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.model.Owner;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.service.PolicyExpirationScheduler;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyExpirationSchedulerTest {

    @Mock
    private InsurancePolicyRepository policyRepository;

    private PolicyExpirationScheduler scheduler;
    private ListAppender<ILoggingEvent> logAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        scheduler = new PolicyExpirationScheduler(policyRepository);
        
        logger = (Logger) LoggerFactory.getLogger(PolicyExpirationScheduler.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);
        logger.setLevel(Level.INFO);
    }

    private void setId(Object entity, Long id) throws Exception {
        Field idField = entity.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
    }

    @Test
    void checkExpiredPolicies_ShouldHandleNoPolicies() {
        LocalDate today = LocalDate.now();
        
        when(policyRepository.findByEndDate(today))
            .thenReturn(Collections.emptyList());
        
        scheduler.checkExpiredPolicies();
        
        verify(policyRepository).findByEndDate(today);
        
        List<ILoggingEvent> logEvents = logAppender.list;
        assertTrue(logEvents.isEmpty(), "No log messages should be written when no policies expire");
    }

    @Test
    void checkExpiredPolicies_WithExpiredPolicies_ShouldLogCorrectMessages() throws Exception {
        LocalDate today = LocalDate.now();
        
        Owner owner1 = new Owner("John Doe", "john@example.com");
        setId(owner1, 1L);
        Car car1 = new Car("VIN12345678901234567", "Toyota", "Camry", 2020, owner1);
        setId(car1, 1L);
        InsurancePolicy policy1 = new InsurancePolicy(car1, "Groupama", today.minusDays(30), today);
        setId(policy1, 1L);
        
        Owner owner2 = new Owner("Jane Smith", "jane@example.com");
        setId(owner2, 2L);
        Car car2 = new Car("VIN98765432109876543", "Honda", "Civic", 2021, owner2);
        setId(car2, 2L);
        InsurancePolicy policy2 = new InsurancePolicy(car2, "Grawe", today.minusDays(60), today);
        setId(policy2, 2L);
        
        when(policyRepository.findByEndDate(today))
            .thenReturn(Arrays.asList(policy1, policy2));
        
        scheduler.checkExpiredPolicies();
        
        verify(policyRepository).findByEndDate(today);
        
        List<ILoggingEvent> logEvents = logAppender.list;
        assertEquals(2, logEvents.size(), "Should log exactly 2 messages for 2 expired policies");
        
        ILoggingEvent firstLog = logEvents.get(0);
        assertEquals(Level.INFO, firstLog.getLevel());
        assertTrue(firstLog.getFormattedMessage().contains("Policy 1"));
        assertTrue(firstLog.getFormattedMessage().contains("car 1"));
        assertTrue(firstLog.getFormattedMessage().contains("expired on " + today));
        
        ILoggingEvent secondLog = logEvents.get(1);
        assertEquals(Level.INFO, secondLog.getLevel());
        assertTrue(secondLog.getFormattedMessage().contains("Policy 2"));
        assertTrue(secondLog.getFormattedMessage().contains("car 2"));
        assertTrue(secondLog.getFormattedMessage().contains("expired on " + today));
    }

    @Test
    void checkExpiredPolicies_AntiSpamLogic_ShouldLogOnlyOnce() throws Exception {
        LocalDate today = LocalDate.now();
        
        Owner owner = new Owner("John Doe", "john@example.com");
        setId(owner, 1L);
        Car car = new Car("VIN12345678901234567", "Toyota", "Camry", 2020, owner);
        setId(car, 1L);
        InsurancePolicy policy = new InsurancePolicy(car, "Groupama", today.minusDays(30), today);
        setId(policy, 1L);
        
        when(policyRepository.findByEndDate(today))
            .thenReturn(Arrays.asList(policy));
        
        scheduler.checkExpiredPolicies();
        scheduler.checkExpiredPolicies();
        
        verify(policyRepository, times(2)).findByEndDate(today);
        
        List<ILoggingEvent> logEvents = logAppender.list;
        assertEquals(1, logEvents.size(), "Should log only once due to anti-spam logic");
        
        ILoggingEvent logEvent = logEvents.get(0);
        assertEquals(Level.INFO, logEvent.getLevel());
        assertTrue(logEvent.getFormattedMessage().contains("Policy 1"));
        assertTrue(logEvent.getFormattedMessage().contains("car 1"));
        assertTrue(logEvent.getFormattedMessage().contains("expired on " + today));
    }

    @Test
    void checkExpiredPolicies_MixedNewAndOldPolicies_ShouldLogOnlyNewOnes() throws Exception {
        LocalDate today = LocalDate.now();
        
        Owner owner1 = new Owner("John Doe", "john@example.com");
        setId(owner1, 1L);
        Car car1 = new Car("VIN12345678901234567", "Toyota", "Camry", 2020, owner1);
        setId(car1, 1L);
        InsurancePolicy policy1 = new InsurancePolicy(car1, "Groupama", today.minusDays(30), today);
        setId(policy1, 1L);
        
        Owner owner2 = new Owner("Jane Smith", "jane@example.com");
        setId(owner2, 2L);
        Car car2 = new Car("VIN98765432109876543", "Honda", "Civic", 2021, owner2);
        setId(car2, 2L);
        InsurancePolicy policy2 = new InsurancePolicy(car2, "Grawe", today.minusDays(60), today);
        setId(policy2, 2L);
        
        when(policyRepository.findByEndDate(today))
            .thenReturn(Arrays.asList(policy1, policy2));
        
        scheduler.checkExpiredPolicies();
        
        when(policyRepository.findByEndDate(today))
            .thenReturn(Arrays.asList(policy1, policy2));
        
        scheduler.checkExpiredPolicies();
        
        List<ILoggingEvent> logEvents = logAppender.list;
        assertEquals(2, logEvents.size(), "Should log only 2 messages total (from first call only)");
        
        assertTrue(logEvents.get(0).getFormattedMessage().contains("Policy 1"));
        assertTrue(logEvents.get(1).getFormattedMessage().contains("Policy 2"));
    }
}
