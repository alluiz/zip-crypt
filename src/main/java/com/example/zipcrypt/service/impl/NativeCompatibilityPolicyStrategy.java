package com.example.zipcrypt.service.impl;

import com.example.zipcrypt.domain.TargetOs;
import com.example.zipcrypt.domain.TargetTool;
import com.example.zipcrypt.service.EncryptionPolicyResolver;
import com.example.zipcrypt.service.EncryptionPolicyStrategy;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

import static net.lingala.zip4j.model.enums.EncryptionMethod.ZIP_STANDARD;

@Component
@org.springframework.core.annotation.Order(1)
public class NativeCompatibilityPolicyStrategy implements EncryptionPolicyStrategy {

    private static final Set<TargetOs> SUPPORTED_OS = EnumSet.of(TargetOs.WINDOWS, TargetOs.MACOS, TargetOs.LINUX, TargetOs.UNIVERSAL);

    @Override
    public boolean supports(TargetOs targetOs, TargetTool targetTool) {
        return targetTool == TargetTool.NATIVE && SUPPORTED_OS.contains(targetOs);
    }

    @Override
    public EncryptionPolicyResolver.EncryptionPolicy policy() {
        return new EncryptionPolicyResolver.EncryptionPolicy(ZIP_STANDARD, null);
    }
}
