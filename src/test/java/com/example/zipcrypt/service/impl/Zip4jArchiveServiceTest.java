package com.example.zipcrypt.service.impl;

import com.example.zipcrypt.api.ZipRequest;
import com.example.zipcrypt.domain.TargetOs;
import com.example.zipcrypt.domain.TargetTool;
import com.example.zipcrypt.notification.NotificationService;
import com.example.zipcrypt.service.EncryptionPolicyResolver;
import com.example.zipcrypt.service.PasswordGenerator;
import net.lingala.zip4j.ZipFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static net.lingala.zip4j.model.enums.AesKeyStrength.KEY_STRENGTH_256;
import static net.lingala.zip4j.model.enums.EncryptionMethod.AES;
import static org.assertj.core.api.Assertions.assertThat;

class Zip4jArchiveServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldCreateEncryptedZipAndNotifyPassword() throws Exception {
        char[] password = "StrongPassword123!".toCharArray();
        PasswordGenerator passwordGenerator = () -> password;
        EncryptionPolicyResolver resolver = (os, tool) -> new EncryptionPolicyResolver.EncryptionPolicy(AES, KEY_STRENGTH_256);
        RecordingNotificationService notificationService = new RecordingNotificationService();
        Zip4jArchiveService service = new Zip4jArchiveService(passwordGenerator, resolver, notificationService, tempDir.toString());

        var response = service.createEncryptedArchive(new ZipRequest("hello world", "payload.txt", TargetOs.UNIVERSAL, TargetTool.GENERIC));

        Path zipPath = tempDir.resolve(response.zipFileName());
        assertThat(zipPath).exists();
        assertThat(response.content()).isEqualTo(Files.readAllBytes(zipPath));

        try (ZipFile zipFile = new ZipFile(zipPath.toFile(), "StrongPassword123!".toCharArray())) {
            assertThat(zipFile.isEncrypted()).isTrue();
            assertThat(zipFile.getFileHeaders()).hasSize(1);
        }

        assertThat(notificationService.notifiedArchiveId).isEqualTo(response.archiveId());
        assertThat(notificationService.notifiedPassword).isEqualTo("StrongPassword123!");
    }

    private static class RecordingNotificationService implements NotificationService {
        private String notifiedArchiveId;
        private String notifiedPassword;

        @Override
        public void notifyPassword(String archiveId, char[] password) {
            this.notifiedArchiveId = archiveId;
            this.notifiedPassword = new String(password);
        }
    }
}
