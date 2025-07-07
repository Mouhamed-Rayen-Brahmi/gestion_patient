package com.example.gestion.patient;

import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@SpringBootApplication
public class GestionPatientApplication {

    @PostConstruct
    public void initFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                System.out.println("Initializing Firebase...");
                InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-config.json");
                
                if (serviceAccount == null) {
                    throw new IllegalStateException("firebase-config.json not found in resources directory");
                }
                
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setProjectId("gestion-patient-e7a44")
                        .setDatabaseUrl("https://gestion-patient-e7a44.firebaseio.com")
                        .build();
                        
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase initialized successfully!");
            } else {
                System.out.println("Firebase already initialized");
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize Firebase: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(GestionPatientApplication.class, args);
    }
}

