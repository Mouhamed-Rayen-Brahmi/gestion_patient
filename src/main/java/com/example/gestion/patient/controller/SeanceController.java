package com.example.gestion.patient.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.gestion.patient.model.Seance;
import com.example.gestion.patient.model.User;
import com.example.gestion.patient.service.SeanceService;
import com.example.gestion.patient.service.SoinService;
import com.example.gestion.patient.service.UserService;

@Controller
@RequestMapping("/seances")
public class SeanceController {

    @Autowired
    private SeanceService seanceService;
    
    @Autowired
    private SoinService soinService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("")
    public String listSeances(Model model) {
        try {
            model.addAttribute("seances", seanceService.getAllSeances());
            model.addAttribute("soins", soinService.getAllSoins());
            return "seances/list";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading seances: " + e.getMessage());
            return "error";
        }
    }
    
    @GetMapping("/{id}")
    public String viewSeance(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("seance", seanceService.getSeanceById(id));
            return "seances/view";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading seance: " + e.getMessage());
            return "error";
        }
    }
    
    @GetMapping("/request")
    public String showRequestForm(Model model) {
        try {
            model.addAttribute("soins", soinService.getAllSoins());
            return "seances/request";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading request form: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/request")
    public String submitRequest(@ModelAttribute Seance seance, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.getUserByEmail(principal.getName());
            seance.setCreatedBy(currentUser.getId());
            seance.setCodeP(String.valueOf(currentUser.getId()));
            
            seanceService.requestSeance(seance);
            redirectAttributes.addFlashAttribute("success", "Seance request submitted successfully! Waiting for admin approval.");
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error submitting request: " + e.getMessage());
            return "redirect:/seances/request";
        }
    }
}