package com.example.zipcrypt.service;

import com.example.zipcrypt.api.ZipRequest;

public interface ZipArchiveService {

    ArchiveResult createEncryptedArchive(ZipRequest request);
}
