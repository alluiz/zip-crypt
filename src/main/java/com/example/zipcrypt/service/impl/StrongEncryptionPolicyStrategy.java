package com.example.zipcrypt.service.impl;

import com.example.zipcrypt.domain.TargetOs;
import com.example.zipcrypt.domain.TargetTool;
import com.example.zipcrypt.service.EncryptionPolicyResolver;
import com.example.zipcrypt.service.EncryptionPolicyStrategy;
import org.springframework.stereotype.Component;

import static net.lingala.zip4j.model.enums.AesKeyStrength.KEY_STRENGTH_256;
import static net.lingala.zip4j.model.enums.EncryptionMethod.AES;

@Component
@org.springframework.core.annotation.Order(2)
public class StrongEncryptionPolicyStrategy implements EncryptionPolicyStrategy {

    @Override
    public boolean supports(TargetOs targetOs, TargetTool targetTool) {
        return targetTool == TargetTool.SEVEN_ZIP || targetTool == TargetTool.WINZIP;
    }

    @Override
    public EncryptionPolicyResolver.EncryptionPolicy policy() {
        return new EncryptionPolicyResolver.EncryptionPolicy(AES, KEY_STRENGTH_256);
    }
}
