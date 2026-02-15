package com.example.zipcrypt.service;

import com.example.zipcrypt.domain.TargetOs;
import com.example.zipcrypt.domain.TargetTool;

public interface EncryptionPolicyStrategy {

    boolean supports(TargetOs targetOs, TargetTool targetTool);

    EncryptionPolicyResolver.EncryptionPolicy policy();
}
