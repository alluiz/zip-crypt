package com.example.zipcrypt.api;

public class PasswordNotFoundException extends RuntimeException {

    public PasswordNotFoundException(String fileName) {
        super("Password not found or expired for file: " + fileName);
    }
}
