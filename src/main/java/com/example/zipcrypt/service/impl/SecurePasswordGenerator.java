package com.example.zipcrypt.service.impl;

import com.example.zipcrypt.service.PasswordGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class SecurePasswordGenerator implements PasswordGenerator {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@$%&*_-";
    private static final int PASSWORD_SIZE = 32;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public char[] generate() {
        char[] password = new char[PASSWORD_SIZE];
        for (int i = 0; i < PASSWORD_SIZE; i++) {
            password[i] = ALPHABET.charAt(secureRandom.nextInt(ALPHABET.length()));
        }
        return password;
    }
}
