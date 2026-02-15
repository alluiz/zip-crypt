package com.example.zipcrypt.service;

import com.example.zipcrypt.domain.TargetOs;
import com.example.zipcrypt.domain.TargetTool;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;

public interface EncryptionPolicyResolver {

    EncryptionPolicy resolve(TargetOs targetOs, TargetTool targetTool);

    record EncryptionPolicy(EncryptionMethod method, AesKeyStrength keyStrength) {}
}
