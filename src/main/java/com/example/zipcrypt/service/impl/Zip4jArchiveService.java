package com.example.zipcrypt.service.impl;

import com.example.zipcrypt.api.ZipRequest;
import com.example.zipcrypt.notification.NotificationService;
import com.example.zipcrypt.service.ArchiveResult;
import com.example.zipcrypt.service.EncryptionPolicyResolver;
import com.example.zipcrypt.service.PasswordCacheService;
import com.example.zipcrypt.service.PasswordGenerator;
import com.example.zipcrypt.service.ZipArchiveService;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;

@Service
public class Zip4jArchiveService implements ZipArchiveService {

    private final PasswordGenerator passwordGenerator;
    private final EncryptionPolicyResolver encryptionPolicyResolver;
    private final NotificationService notificationService;
    private final PasswordCacheService passwordCacheService;
    private final Path outputDirectory;

    public Zip4jArchiveService(
            PasswordGenerator passwordGenerator,
            EncryptionPolicyResolver encryptionPolicyResolver,
            NotificationService notificationService,
            PasswordCacheService passwordCacheService,
            @Value("${zip.output-directory:./build/out}") String outputDirectory
    ) {
        this.passwordGenerator = passwordGenerator;
        this.encryptionPolicyResolver = encryptionPolicyResolver;
        this.notificationService = notificationService;
        this.passwordCacheService = passwordCacheService;
        this.outputDirectory = Path.of(outputDirectory).toAbsolutePath();
    }

    @Override
    public ArchiveResult createEncryptedArchive(ZipRequest request) {
        String archiveId = UUID.randomUUID().toString();
        char[] password = passwordGenerator.generate();
        String zipFileName = archiveId + ".zip";
        Path sourcePath = null;

        try {
            Files.createDirectories(outputDirectory);
            sourcePath = createInputFile(request, archiveId);
            Path zipPath = outputDirectory.resolve(zipFileName);
            createEncryptedZip(sourcePath, zipPath, password, request);

            byte[] zipBytes = Files.readAllBytes(zipPath);
            char[] passwordCopy = Arrays.copyOf(password, password.length);
            passwordCacheService.put(zipFileName, passwordCopy);
            notificationService.notifyPassword(archiveId, passwordCopy);

            return new ArchiveResult(archiveId, zipFileName, zipBytes);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to create encrypted archive", ex);
        } finally {
            deletePlaintextSourceFile(sourcePath);
            Arrays.fill(password, '\0');
        }
    }

    private void deletePlaintextSourceFile(Path sourcePath) {
        if (sourcePath == null) {
            return;
        }

        try {
            Files.deleteIfExists(sourcePath);
        } catch (IOException ignored) {
            // noop: cleanup best effort without affecting API response contract
        }
    }

    private Path createInputFile(ZipRequest request, String archiveId) throws IOException {
        String safeFileName = request.sourceFileName().replaceAll("[^a-zA-Z0-9._-]", "_");
        Path sourcePath = outputDirectory.resolve(archiveId + "-" + safeFileName);
        Files.writeString(sourcePath, request.plainText(), StandardCharsets.UTF_8);
        return sourcePath;
    }

    private void createEncryptedZip(Path sourcePath, Path zipPath, char[] password, ZipRequest request) throws IOException {
        var policy = encryptionPolicyResolver.resolve(request.targetOs(), request.targetTool());
        var parameters = new ZipParameters();
        parameters.setEncryptFiles(true);
        parameters.setEncryptionMethod(policy.method());
        if (policy.keyStrength() != null) {
            parameters.setAesKeyStrength(policy.keyStrength());
        }

        try (ZipFile zipFile = new ZipFile(zipPath.toFile(), password)) {
            zipFile.addFile(sourcePath.toFile(), parameters);
        }
    }
}
