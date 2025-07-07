package com.example.gestion.patient.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.gestion.patient.model.Seance;
import com.example.gestion.patient.service.SeanceService;

@RestController
@RequestMapping("/api/seances")
public class SeanceRestController {

    @Autowired
    private SeanceService seanceService;

    @GetMapping("/{id}")
    public ResponseEntity<Seance> getSeance(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(seanceService.getSeanceById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createSeance(@RequestBody Seance seance) {
        try {
            System.out.println("Received seance creation request: " + seance);
            
            if (seance.getDateSoin() == null) {
                seance.setDateSoin(LocalDate.now().toString());
            }
            
            seanceService.createSeance(seance);
            return ResponseEntity.ok(seance);
        } catch (Exception e) {
            System.err.println("Error creating seance: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Seance> updateSeance(@PathVariable Long id, @RequestBody Seance seance) {
        try {
            seance.setId(id);
            seanceService.updateSeance(seance);
            return ResponseEntity.ok(seance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Void> joinSeance(@PathVariable Long id, @RequestParam Long userId) {
        try {
            seanceService.joinSeance(id, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<Void> leaveSeance(@PathVariable Long id, @RequestParam Long userId) {
        try {
            seanceService.leaveSeance(id, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Seance>> getPendingSeances() {
        try {
            return ResponseEntity.ok(seanceService.getPendingSeances());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Seance> approveSeance(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String message = payload.getOrDefault("message", "Your seance request has been approved.");
            seanceService.approveSeanceRequest(id, message);
            return ResponseEntity.ok(seanceService.getSeanceById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/decline")
    public ResponseEntity<Seance> declineSeance(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String message = payload.getOrDefault("message", "Your seance request has been declined.");
            seanceService.declineSeanceRequest(id, message);
            return ResponseEntity.ok(seanceService.getSeanceById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}