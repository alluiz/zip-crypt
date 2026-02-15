package com.example.zipcrypt.service;

public record ArchiveResult(String archiveId, String zipFileName, byte[] content) {
}
