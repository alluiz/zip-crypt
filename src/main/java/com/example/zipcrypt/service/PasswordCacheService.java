package com.example.zipcrypt.service;

import java.util.Optional;

public interface PasswordCacheService {

    void put(String fileName, char[] password);

    Optional<String> get(String fileName);
}
