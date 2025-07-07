package com.example.gestion.patient.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.gestion.patient.model.Seance;
import com.example.gestion.patient.model.User;
import com.example.gestion.patient.security.CustomUserDetails;
import com.example.gestion.patient.service.SeanceService;
import com.example.gestion.patient.service.UserService;

@Controller
public class UserController {

    @Autowired
    private SeanceService seanceService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/dashboard")
    public String showDashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        try {
            User user = userDetails.getUser();
            System.out.println("Loading dashboard for user: " + user.getName() + ", ID: " + user.getId());
            
            if (user.getId() == null) {
                model.addAttribute("error", "User ID is null. Please log out and log in again.");
                model.addAttribute("userSeances", Collections.emptyList());
            } else {
                try {
                    List<Seance> seances = seanceService.getSeancesByPatient(user.getId());
                    model.addAttribute("userSeances", seances);
                    
                    long completedCount = seances.stream()
                        .filter(s -> "COMPLETED".equals(s.getStatus()))
                        .count();
                        
                    long plannedCount = seances.stream()
                        .filter(s -> "PLANNED".equals(s.getStatus()))
                        .count();
                        
                    long waitingApprovalCount = seances.stream()
                        .filter(s -> "WAITING_APPROVAL".equals(s.getStatus()))
                        .count();
                    
                    model.addAttribute("completedSeances", completedCount);
                    model.addAttribute("plannedSeances", plannedCount);
                    model.addAttribute("waitingApprovalSeances", waitingApprovalCount);
                    
                } catch (Exception e) {
                    System.err.println("Error loading seances: " + e.getMessage());
                    model.addAttribute("error", "Could not load your appointments. Please try again later.");
                    model.addAttribute("userSeances", Collections.emptyList());
                    model.addAttribute("completedSeances", 0L);
                    model.addAttribute("plannedSeances", 0L);
                    model.addAttribute("waitingApprovalSeances", 0L);
                }
            }
            
            model.addAttribute("user", user);
            return "dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/profile")
    public String showProfilePage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        try {
            User user = userDetails.getUser();
            model.addAttribute("user", user);
            return "profile";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading profile: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                               @RequestParam("name") String name,
                               RedirectAttributes redirectAttributes) {
        try {
            User user = userDetails.getUser();
            user.setName(name);
            userService.updateUser(user);
            
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating profile: " + e.getMessage());
            return "redirect:/profile";
        }
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                               @RequestParam("currentPassword") String currentPassword,
                               @RequestParam("newPassword") String newPassword,
                               @RequestParam("confirmPassword") String confirmPassword,
                               RedirectAttributes redirectAttributes) {
        try {
            if (!passwordEncoder.matches(currentPassword, userDetails.getPassword())) {
                redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
                return "redirect:/profile";
            }
            
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "New passwords do not match");
                return "redirect:/profile";
            }
            
            User user = userDetails.getUser();
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.updateUser(user);
            
            redirectAttributes.addFlashAttribute("success", "Password changed successfully");
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error changing password: " + e.getMessage());
            return "redirect:/profile";
        }
    }
}
