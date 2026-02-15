package com.example.zipcrypt.notification;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Primary
public class ConsoleNotificationService implements NotificationService {

    @Override
    public void notifyPassword(String archiveId, char[] password) {
        System.out.printf("Archive %s password: %s%n", archiveId, new String(password));
        Arrays.fill(password, '\0');
    }
}
