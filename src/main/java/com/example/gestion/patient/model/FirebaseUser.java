package com.example.gestion.patient.model;

import lombok.Data;

@Data
public class FirebaseUser {
    private Long id;
    private String email;
    private String password;
    private String name;
    private String role;
    private String profileImage;
    
    public FirebaseUser(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.name = user.getName();
        this.role = user.getRole();
        this.profileImage = user.getProfileImage();
    }
    
    public FirebaseUser() {}
}