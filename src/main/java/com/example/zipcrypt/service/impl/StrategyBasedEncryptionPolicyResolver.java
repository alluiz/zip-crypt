package com.example.zipcrypt.service.impl;

import com.example.zipcrypt.domain.TargetOs;
import com.example.zipcrypt.domain.TargetTool;
import com.example.zipcrypt.service.EncryptionPolicyResolver;
import com.example.zipcrypt.service.EncryptionPolicyStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StrategyBasedEncryptionPolicyResolver implements EncryptionPolicyResolver {

    private final List<EncryptionPolicyStrategy> strategies;

    public StrategyBasedEncryptionPolicyResolver(List<EncryptionPolicyStrategy> strategies) {
        this.strategies = strategies;
    }

    @Override
    public EncryptionPolicy resolve(TargetOs targetOs, TargetTool targetTool) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(targetOs, targetTool))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No encryption strategy found"))
                .policy();
    }
}
