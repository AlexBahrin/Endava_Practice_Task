package com.example.carins.service;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PolicyExpirationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PolicyExpirationScheduler.class);
    
    private final InsurancePolicyRepository policyRepository;
    
    private final Set<Long> loggedExpiredPolicies = new HashSet<>();

    public PolicyExpirationScheduler(InsurancePolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Scheduled(fixedRate = 600000) // 10 minutes in milliseconds
    public void checkExpiredPolicies() {
        LocalDate today = LocalDate.now();
        
        List<InsurancePolicy> recentlyExpiredPolicies = policyRepository.findByEndDate(today);
                
        for (InsurancePolicy policy : recentlyExpiredPolicies) {
            if (!loggedExpiredPolicies.contains(policy.getId())) {
                logger.info("Policy {} for car {} expired on {}", 
                    policy.getId(), 
                    policy.getCar().getId(), 
                    policy.getEndDate());
                
                loggedExpiredPolicies.add(policy.getId());
            }
        }
    }
}
