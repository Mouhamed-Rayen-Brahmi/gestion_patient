package com.example.gestion.patient.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.gestion.patient.model.User;

@Service
public class UserService {

    @Autowired
    private FirebaseService firebaseService;
    
    @Autowired
    private CounterService counterService;

    public void registerUser(User user) throws ExecutionException, InterruptedException {
        User existingUser = firebaseService.getUserByEmail(user.getEmail());
        if (existingUser != null) {
            throw new RuntimeException("User with this email already exists");
        }
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        
        try {
            Long nextId = counterService.getNextSequence("users");
            if (nextId == null) {
                throw new RuntimeException("Failed to generate user ID - received null");
            }
            user.setId(nextId);
            System.out.println("Generated user ID: " + user.getId());
        } catch (Exception e) {
            System.err.println("Error generating user ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate user ID: " + e.getMessage(), e);
        }
        
        if (user.getId() == null) {
            throw new RuntimeException("User ID is null even after generation attempt");
        }
        
        firebaseService.saveUser(user);
        System.out.println("User saved successfully with ID: " + user.getId());
    }

    public User getUserByEmail(String email) throws ExecutionException, InterruptedException {
        System.out.println("Looking up user by email: " + email);
        return firebaseService.getUserByEmail(email);
    }
    
    public List<User> getAllUsers() throws ExecutionException, InterruptedException {
        return firebaseService.getUsers();
    }

    public User getUserById(Long userId) throws ExecutionException, InterruptedException {
        return firebaseService.getUserById(userId);
    }

    public void updateUser(User user) throws ExecutionException, InterruptedException {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        firebaseService.updateUser(user);
    }
}
