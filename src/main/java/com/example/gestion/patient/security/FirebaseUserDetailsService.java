package com.example.gestion.patient.security;

import com.example.gestion.patient.model.User;
import com.example.gestion.patient.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Trying to load user: " + email);
        try {
            User user = userService.getUserByEmail(email);
            if (user == null) {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }
            return new CustomUserDetails(user);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new UsernameNotFoundException("Error fetching user from Firebase", e);
        } catch (Exception e) {
            throw new UsernameNotFoundException("Unexpected error occurred while fetching user", e);
        }
    }
}
