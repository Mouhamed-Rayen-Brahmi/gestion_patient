package com.example.gestion.patient.service;

import java.util.*;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.gestion.patient.model.Soin;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class SoinService {

    private static final String COLLECTION_NAME = "soins";
    
    @Autowired
    private CounterService counterService;

    public List<Soin> getAllSoins() throws ExecutionException, InterruptedException {
        try {
            System.out.println("Getting all soins...");
            Firestore db = FirestoreClient.getFirestore();
            
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
            List<Soin> soins = new ArrayList<>();
            
            QuerySnapshot snapshot = future.get();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                Soin soin = doc.toObject(Soin.class);
                if (soin != null) {
                    soins.add(soin);
                }
            }
            
            System.out.println("Found " + soins.size() + " soins");
            return soins;
        } catch (Exception e) {
            System.err.println("Error getting soins: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    public Soin getSoinById(Long id) throws ExecutionException, InterruptedException {
        System.out.println("Getting soin with ID: " + id);
        Firestore db = FirestoreClient.getFirestore();
        
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(String.valueOf(id));
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        
        if (document.exists()) {
            return document.toObject(Soin.class);
        } else {
            return null;
        }
    }
    
    public void createSoin(Soin soin) throws Exception {
        if (soin == null) {
            throw new IllegalArgumentException("Soin cannot be null");
        }
        
        System.out.println("Creating soin...");
        Firestore db = FirestoreClient.getFirestore();
        
        Long nextId = counterService.getNextSequence("soins");
        soin.setId(nextId);
        
        ApiFuture<WriteResult> result = db.collection(COLLECTION_NAME)
                .document(String.valueOf(soin.getId()))
                .set(soin);
                
        result.get();
        System.out.println("Soin created with ID: " + soin.getId());
    }

    public void deleteSoin(Long id) throws ExecutionException, InterruptedException {
        System.out.println("Deleting soin with ID: " + id);
        Firestore db = FirestoreClient.getFirestore();
        
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(String.valueOf(id));
        ApiFuture<WriteResult> writeResult = docRef.delete();
        
        writeResult.get();
        System.out.println("Soin with ID " + id + " deleted successfully");
    }
}