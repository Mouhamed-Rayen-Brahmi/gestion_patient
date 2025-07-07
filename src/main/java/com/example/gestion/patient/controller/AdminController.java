package com.example.gestion.patient.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.gestion.patient.model.Seance;
import com.example.gestion.patient.service.SeanceService;
import com.example.gestion.patient.service.SoinService;
import com.example.gestion.patient.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private SeanceService seanceService;

    @Autowired
    private SoinService soinService;

    @Autowired
    private UserService userService;

    @GetMapping("")
    public String showAdminDashboard(Model model) {
        try {
            model.addAttribute("totalUsers", userService.getAllUsers().size());
            model.addAttribute("totalSoins", soinService.getAllSoins().size());
            
            List<Seance> allSeances = seanceService.getAllSeances();
            
            long pendingRequests = allSeances.stream()
                .filter(s -> s.getStatus().equals("WAITING_APPROVAL"))
                .count();
            model.addAttribute("pendingRequests", pendingRequests);
            
            model.addAttribute("activeSeances", 
                allSeances.stream()
                    .filter(s -> s.getStatus().equals("PLANNED") || s.getStatus().equals("IN_PROGRESS"))
                    .count());
            
            model.addAttribute("todaySeances", 
                allSeances.stream()
                    .filter(s -> s.getDateSoin() != null && s.getDateSoin().equals(LocalDate.now().toString()))
                    .count());

            model.addAttribute("recentSeances", allSeances);
            
            model.addAttribute("soins", soinService.getAllSoins());

            return "admin";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading admin dashboard: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/users")
    public String showUsers(Model model) {
        try {
            model.addAttribute("users", userService.getAllUsers());
            return "admin/users";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading users: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/soins")
    public String showSoins(Model model) {
        try {
            model.addAttribute("soins", soinService.getAllSoins());
            return "admin/soins";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading soins: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/seances")
    public String showSeances(Model model) {
        try {
            model.addAttribute("seances", seanceService.getAllSeances());
            model.addAttribute("soins", soinService.getAllSoins());
            return "admin/seances";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading seances: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/pending-seances")
    public String showPendingSeances(Model model) {
        try {
            model.addAttribute("pendingSeances", seanceService.getPendingSeances());
            return "admin/pending-seances";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading pending seances: " + e.getMessage());
            return "error";
        }
    }
}
