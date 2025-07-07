package com.example.gestion.patient.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.example.gestion.patient.model.FirebaseUser;
import com.example.gestion.patient.model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class FirebaseService {

    private static final String COLLECTION_NAME = "users";

    public void saveUser(User user) throws ExecutionException, InterruptedException {
        try {
            System.out.println("Attempting to save user: " + user.getId() + ", " + user.getEmail());
            
            FirebaseUser firebaseUser = new FirebaseUser(user);
            
            Firestore db = FirestoreClient.getFirestore();
            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                .document(String.valueOf(user.getId()))
                .set(firebaseUser);
                
            WriteResult result = future.get();
            System.out.println("User saved successfully at: " + result.getUpdateTime());
        } catch (Exception e) {
            System.err.println("Error saving user to Firestore: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public User getUserByEmail(String email) throws ExecutionException, InterruptedException {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        
        System.out.println("Searching for user with email: " + email);
        Firestore db = FirestoreClient.getFirestore();
        
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                .whereEqualTo("email", email)
                .limit(1)
                .get();
                
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        if (!documents.isEmpty()) {
            User user = documents.get(0).toObject(User.class);
            System.out.println("Found user with email " + email + ", ID: " + user.getId());
            return user;
        } else {
            System.out.println("No user found with email: " + email);
            return null;
        }
    }

    public User getUserById(Long id) throws ExecutionException, InterruptedException {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        System.out.println("Looking up user with ID: " + id);
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(String.valueOf(id));
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        
        if (document.exists()) {
            FirebaseUser firebaseUser = document.toObject(FirebaseUser.class);
            User user = convertToUser(firebaseUser);
            System.out.println("Found user with ID " + id + ": " + user.getName());
            return user;
        } else {
            System.out.println("No user found with ID: " + id);
            return null;
        }
    }

    public void updateUser(User user) throws ExecutionException, InterruptedException {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference documentReference = db.collection(COLLECTION_NAME).document(user.getId().toString());
            
            FirebaseUser firebaseUser = new FirebaseUser(user);
            ApiFuture<WriteResult> future = documentReference.set(firebaseUser);
            
            future.get();
        } catch (Exception e) {
            System.err.println("Error updating user in Firestore: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private User convertToUser(FirebaseUser firebaseUser) {
        if (firebaseUser == null) return null;
        
        User user = new User();
        user.setId(firebaseUser.getId());
        user.setEmail(firebaseUser.getEmail());
        user.setPassword(firebaseUser.getPassword());
        user.setName(firebaseUser.getName());
        user.setRole(firebaseUser.getRole());
        user.setProfileImage(firebaseUser.getProfileImage());
        return user;
    }

    public List<User> getUsers() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
        List<User> users = new ArrayList<>();
        
        for (DocumentSnapshot document : future.get().getDocuments()) {
            User user = document.toObject(User.class);
            if (user != null) {
                users.add(user);
            }
        }
        
        return users;
    }
}
