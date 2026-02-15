package com.example.zipcrypt.service;

import com.example.zipcrypt.domain.TargetOs;
import com.example.zipcrypt.domain.TargetTool;
import com.example.zipcrypt.service.impl.DefaultEncryptionPolicyStrategy;
import com.example.zipcrypt.service.impl.NativeCompatibilityPolicyStrategy;
import com.example.zipcrypt.service.impl.StrategyBasedEncryptionPolicyResolver;
import com.example.zipcrypt.service.impl.StrongEncryptionPolicyStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static net.lingala.zip4j.model.enums.EncryptionMethod.AES;
import static net.lingala.zip4j.model.enums.EncryptionMethod.ZIP_STANDARD;
import static org.assertj.core.api.Assertions.assertThat;

class StrategyBasedEncryptionPolicyResolverTest {

    private final StrategyBasedEncryptionPolicyResolver resolver = new StrategyBasedEncryptionPolicyResolver(
            List.of(new NativeCompatibilityPolicyStrategy(), new StrongEncryptionPolicyStrategy(), new DefaultEncryptionPolicyStrategy())
    );

    @Test
    void shouldUseNativeProfileForNativeTools() {
        var policy = resolver.resolve(TargetOs.WINDOWS, TargetTool.NATIVE);

        assertThat(policy.method()).isEqualTo(ZIP_STANDARD);
    }

    @Test
    void shouldUseStrongProfileForSevenZip() {
        var policy = resolver.resolve(TargetOs.LINUX, TargetTool.SEVEN_ZIP);

        assertThat(policy.method()).isEqualTo(AES);
    }
}
