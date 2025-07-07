package com.example.gestion.patient.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.gestion.patient.model.Seance;
import com.example.gestion.patient.model.Soin;
import com.example.gestion.patient.model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class SeanceService {

    private static final String COLLECTION_NAME = "seances";

    @Autowired
    private CounterService counterService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SoinService soinService;

    public void createSeance(Seance seance) throws Exception {
        try {
            System.out.println("Creating seance in service layer...");
            Firestore db = FirestoreClient.getFirestore();
            
            if (seance.getId() == null) {
                Long nextId = counterService.getNextSequence(COLLECTION_NAME);
                seance.setId(nextId);
                System.out.println("Generated ID: " + nextId);
            }
            
            if (seance.getCreatedAt() == null) {
                seance.setCreatedAt(java.time.LocalDate.now().toString());
            } 

            if (seance.getUpdatedAt() == null) {
                seance.setUpdatedAt(java.time.LocalDate.now().toString());
            }
            
            if (seance.getDateSoin() == null) {
                seance.setDateSoin(java.time.LocalDate.now().toString());
            }
            
            if (seance.getParticipants() == null) {
                seance.setParticipants(new ArrayList<>());
            }
            
            Map<String, Object> seanceMap = new HashMap<>();
            seanceMap.put("id", seance.getId());
            seanceMap.put("codeP", seance.getCodeP());
            seanceMap.put("codeSoin", seance.getCodeSoin());
            seanceMap.put("dateSoin", seance.getDateSoin());
            seanceMap.put("participants", seance.getParticipants());
            seanceMap.put("status", seance.getStatus());
            seanceMap.put("notes", seance.getNotes());
            seanceMap.put("createdBy", seance.getCreatedBy());
            seanceMap.put("createdAt", seance.getCreatedAt());
            seanceMap.put("updatedAt", seance.getUpdatedAt());
            
            System.out.println("Saving seance with ID: " + seance.getId());
            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(String.valueOf(seance.getId()))
                    .set(seanceMap); 
                    
            future.get(); 
            System.out.println("Seance saved with ID: " + seance.getId());
        } catch (Exception e) {
            System.err.println("Error in createSeance: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void deleteSeance(Long id) throws Exception {
        if (id == null) {
            throw new IllegalArgumentException("Seance ID cannot be null");
        }
        Firestore db = FirestoreClient.getFirestore();
        db.collection(COLLECTION_NAME).document(String.valueOf(id)).delete().get();
    }

    public List<Seance> getAllSeances() throws ExecutionException, InterruptedException {
        System.out.println("Getting all seances...");

        try {
            Firestore db = FirestoreClient.getFirestore();

            if (db == null) {
                System.err.println("Firestore is null! Firebase may not be initialized properly.");
                return Collections.emptyList();
            }

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
            List<Seance> seances = new ArrayList<>();

            QuerySnapshot snapshot = future.get();
            if (snapshot.isEmpty()) {
                System.out.println("No seances found");
                return seances;
            }

            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                Seance seance = doc.toObject(Seance.class);
                if (seance != null) {
                    seances.add(seance);
                }
            }

            System.out.println("Found " + seances.size() + " seances");
            return seances;
        } catch (Exception e) {
            System.err.println("Error getting all seances: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Seance getSeanceById(Long id) throws Exception {
        if (id == null) {
            throw new IllegalArgumentException("Seance ID cannot be null");
        }
        
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(String.valueOf(id));
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        
        if (document.exists()) {
            System.out.println("Seance found");
            return document.toObject(Seance.class);
        } else {
            System.out.println("Seance not found");
            return null;
        }
    }

    public void updateSeance(Seance seance) throws Exception {
        if (seance == null || seance.getId() == null) {
            throw new IllegalArgumentException("Seance and its ID cannot be null");
        }
        
        seance.setUpdatedAt(java.time.LocalDate.now().toString());
        
        Map<String, Object> seanceMap = new HashMap<>();
        seanceMap.put("id", seance.getId());
        seanceMap.put("codeP", seance.getCodeP());
        seanceMap.put("codeSoin", seance.getCodeSoin());
        seanceMap.put("dateSoin", seance.getDateSoin());
        seanceMap.put("participants", seance.getParticipants());
        seanceMap.put("status", seance.getStatus());
        seanceMap.put("notes", seance.getNotes());
        seanceMap.put("createdBy", seance.getCreatedBy());
        seanceMap.put("createdAt", seance.getCreatedAt());
        seanceMap.put("updatedAt", seance.getUpdatedAt());
        
        Firestore db = FirestoreClient.getFirestore();
        db.collection(COLLECTION_NAME).document(String.valueOf(seance.getId())).set(seanceMap).get();
    }

    public void joinSeance(Long seanceId, Long userId) throws Exception {
        if (seanceId == null || userId == null) {
            throw new IllegalArgumentException("Seance ID and User ID cannot be null");
        }

        Firestore db = FirestoreClient.getFirestore();
        Seance seance = getSeanceById(seanceId);
        User user = userService.getUserById(userId);

        if (seance == null) {
            throw new IllegalArgumentException("Seance not found");
        }
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (!user.getRole().equals("PATIENT")) {
            throw new IllegalArgumentException("Only patients can join seances");
        }

        if (seance.getParticipants() == null) {
            seance.setParticipants(new ArrayList<>());
        }

        if (!seance.getParticipants().contains(userId)) {
            seance.getParticipants().add(userId);
            db.collection(COLLECTION_NAME).document(String.valueOf(seanceId)).set(seance).get();
        }
    }

    public void leaveSeance(Long seanceId, Long userId) throws Exception {
        if (seanceId == null || userId == null) {
            throw new IllegalArgumentException("Seance ID and User ID cannot be null");
        }

        Firestore db = FirestoreClient.getFirestore();
        Seance seance = getSeanceById(seanceId);

        if (seance == null) {
            throw new IllegalArgumentException("Seance not found");
        }

        if (seance.getParticipants() != null) {
            seance.getParticipants().remove(userId);
            db.collection(COLLECTION_NAME).document(String.valueOf(seanceId)).set(seance).get();
        }
    }

    public List<Seance> getSeancesByPatient(Long patientId) throws ExecutionException, InterruptedException {
        System.out.println("Getting seances for patient: " + patientId);

        try {
            Firestore db = FirestoreClient.getFirestore();

            if (db == null) {
                System.err.println("Firestore is null! Firebase may not be initialized properly.");
                return Collections.emptyList();
            }

            String patientIdStr = String.valueOf(patientId);
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("codeP", patientIdStr)
                    .get();

            List<Seance> seances = new ArrayList<>();

            QuerySnapshot snapshot = future.get();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                Seance seance = doc.toObject(Seance.class);
                if (seance != null) {
                    seances.add(seance);
                }
            }

            System.out.println("Found " + seances.size() + " seances for patient " + patientId);
            return seances;
        } catch (Exception e) {
            System.err.println("Error getting patient seances: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void requestSeance(Seance seance) throws Exception {
        try {
            System.out.println("Creating seance request...");
            Firestore db = FirestoreClient.getFirestore();
            
            if (seance.getId() == null) {
                Long nextId = counterService.getNextSequence(COLLECTION_NAME);
                seance.setId(nextId);
            }
            
            if (seance.getCreatedAt() == null) {
                seance.setCreatedAt(java.time.LocalDate.now().toString());
            } 

            if (seance.getUpdatedAt() == null) {
                seance.setUpdatedAt(java.time.LocalDate.now().toString());
            }
            
            if (seance.getDateSoin() == null) {
                seance.setDateSoin(java.time.LocalDate.now().toString());
            }
            
            if (seance.getParticipants() == null) {
                seance.setParticipants(new ArrayList<>());
            }
            
            seance.setStatus("WAITING_APPROVAL");
            
            Map<String, Object> seanceMap = new HashMap<>();
            seanceMap.put("id", seance.getId());
            seanceMap.put("codeP", seance.getCodeP());
            seanceMap.put("codeSoin", seance.getCodeSoin());
            seanceMap.put("dateSoin", seance.getDateSoin());
            seanceMap.put("participants", seance.getParticipants());
            seanceMap.put("status", seance.getStatus());
            seanceMap.put("notes", seance.getNotes());
            seanceMap.put("createdBy", seance.getCreatedBy());
            seanceMap.put("createdAt", seance.getCreatedAt());
            seanceMap.put("updatedAt", seance.getUpdatedAt());
            
            System.out.println("Saving seance request with ID: " + seance.getId());
            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(String.valueOf(seance.getId()))
                    .set(seanceMap);
                    
            future.get(); 
            System.out.println("Seance request saved with ID: " + seance.getId());
        } catch (Exception e) {
            System.err.println("Error in requestSeance: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    
    public List<Seance> getPendingSeances() throws ExecutionException, InterruptedException {
        System.out.println("Getting pending seances...");

        try {
            Firestore db = FirestoreClient.getFirestore();
            
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                                               .whereEqualTo("status", "WAITING_APPROVAL")
                                               .get();
            List<Seance> seances = new ArrayList<>();

            QuerySnapshot snapshot = future.get();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                Seance seance = doc.toObject(Seance.class);
                if (seance != null) {
                    seances.add(seance);
                }
            }

            System.out.println("Found " + seances.size() + " pending seances");
            return seances;
        } catch (Exception e) {
            System.err.println("Error getting pending seances: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void approveSeanceRequest(Long seanceId, String adminMessage) throws Exception {
        Seance seance = getSeanceById(seanceId);
        if (seance == null) {
            throw new IllegalArgumentException("Seance not found with ID: " + seanceId);
        }
        
        if (!seance.getStatus().equals("WAITING_APPROVAL")) {
            throw new IllegalStateException("Seance is not in WAITING_APPROVAL status");
        }
        
        seance.setStatus("PLANNED");
        seance.setAdminResponse(adminMessage);
        seance.setUpdatedAt(java.time.LocalDate.now().toString());
        
        updateSeance(seance);
        
        try {
            User patient = userService.getUserById(Long.parseLong(seance.getCodeP()));
            if (patient != null && patient.getEmail() != null) {
                String soinName = "Unknown Treatment";
                try {
                    Soin soin = soinService.getSoinById(Long.parseLong(seance.getCodeSoin()));
                    if (soin != null) {
                        soinName = soin.getDesignation();
                    }
                } catch (Exception e) {
                    System.err.println("Error retrieving treatment details: " + e.getMessage());
                }
                
                emailService.sendSeanceRequestApproval(
                    patient.getEmail(), 
                    patient.getName(), 
                    seance.getId(), 
                    seance.getDateSoin(), 
                    soinName,
                    adminMessage
                );
            }
        } catch (Exception e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
        }
    }

    
    public void declineSeanceRequest(Long seanceId, String adminMessage) throws Exception {
        Seance seance = getSeanceById(seanceId);
        if (seance == null) {
            throw new IllegalArgumentException("Seance not found with ID: " + seanceId);
        }
        
        if (!seance.getStatus().equals("WAITING_APPROVAL")) {
            throw new IllegalStateException("Seance is not in WAITING_APPROVAL status");
        }
        
        seance.setStatus("CANCELLED");
        seance.setAdminResponse(adminMessage);
        seance.setUpdatedAt(java.time.LocalDate.now().toString());
        
        updateSeance(seance);
        
        try {
            User patient = userService.getUserById(Long.parseLong(seance.getCodeP()));
            if (patient != null && patient.getEmail() != null) {
                String soinName = "Unknown Treatment";
                try {
                    Soin soin = soinService.getSoinById(Long.parseLong(seance.getCodeSoin()));
                    if (soin != null) {
                        soinName = soin.getDesignation();
                    }
                } catch (Exception e) {
                    System.err.println("Error retrieving treatment details: " + e.getMessage());
                }
                
                emailService.sendSeanceRequestRejection(
                    patient.getEmail(), 
                    patient.getName(), 
                    seance.getId(), 
                    seance.getDateSoin(), 
                    soinName,
                    adminMessage
                );
            }
        } catch (Exception e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
        }
    }
}