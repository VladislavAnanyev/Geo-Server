package com.example.meetings.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@EnableConfigurationProperties(FirebaseProperties.class)
@RequiredArgsConstructor
public class FirebaseConfig {

    private final FirebaseProperties firebaseProperties;

    @Bean
    GoogleCredentials googleCredentials() throws IOException {
        if (firebaseProperties.getServiceAccount() != null) {
            try (InputStream is = firebaseProperties.getServiceAccount().getInputStream()) {
                return GoogleCredentials.fromStream(is);
            }
        }
        else {
            // Use standard credentials chain. Useful when running inside GKE
            return GoogleCredentials.getApplicationDefault();
        }
    }

    @Bean
    FirebaseApp firebaseApp(GoogleCredentials credentials) {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
