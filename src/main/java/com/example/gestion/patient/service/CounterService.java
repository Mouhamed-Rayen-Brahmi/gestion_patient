package com.example.gestion.patient.service;

import java.util.Collections;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class CounterService {
    public Long getNextSequence(String collectionName) throws Exception {
        try {
            System.out.println("Getting next sequence for: " + collectionName);
            
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference docRef = db.collection("counters").document(collectionName);
            
            ApiFuture<Long> futureTransaction = db.runTransaction(transaction -> {
                DocumentSnapshot snapshot = transaction.get(docRef).get();
                Long nextId = 1L;
                
                if (snapshot.exists()) {
                    nextId = snapshot.getLong("value") + 1;
                }
                
                transaction.set(docRef, Collections.singletonMap("value", nextId));
                return nextId;
            });
            
            Long result = futureTransaction.get();
            System.out.println("Generated ID: " + result + " for " + collectionName);
            return result;
        } catch (Exception e) {
            System.err.println("Error generating sequence: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}