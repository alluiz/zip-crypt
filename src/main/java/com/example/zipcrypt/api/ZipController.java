package com.example.zipcrypt.api;

import com.example.zipcrypt.service.ArchiveResult;
import com.example.zipcrypt.service.PasswordCacheService;
import com.example.zipcrypt.service.ZipArchiveService;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/archives")
public class ZipController {

    private final ZipArchiveService zipArchiveService;
    private final PasswordCacheService passwordCacheService;

    public ZipController(ZipArchiveService zipArchiveService, PasswordCacheService passwordCacheService) {
        this.zipArchiveService = zipArchiveService;
        this.passwordCacheService = passwordCacheService;
    }

    @PostMapping(produces = "application/zip")
    public ResponseEntity<byte[]> createArchive(@Valid @RequestBody ZipRequest request) {
        ArchiveResult result = zipArchiveService.createEncryptedArchive(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/zip"));
        headers.setContentDisposition(ContentDisposition.attachment().filename(result.zipFileName()).build());
        headers.set("X-Archive-Id", result.archiveId());

        return new ResponseEntity<>(result.content(), headers, HttpStatus.CREATED);
    }

    @GetMapping("/{fileName}/password")
    public PasswordLookupResponse getPassword(@PathVariable String fileName) {
        String password = passwordCacheService.get(fileName)
                .orElseThrow(() -> new PasswordNotFoundException(fileName));
        return new PasswordLookupResponse(fileName, password);
    }
}
