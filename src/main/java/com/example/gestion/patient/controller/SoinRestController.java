package com.example.gestion.patient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.gestion.patient.model.Soin;
import com.example.gestion.patient.service.SoinService;

@RestController
@RequestMapping("/api/soins")
public class SoinRestController {

    @Autowired
    private SoinService soinService;

    @GetMapping("/{id}")
    public ResponseEntity<Soin> getSoin(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(soinService.getSoinById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<Soin> createSoin(@RequestBody Soin soin) {
        try {
            soinService.createSoin(soin);
            return ResponseEntity.ok(soin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Soin> updateSoin(@PathVariable Long id, @RequestBody Soin soin) {
        try {
            soin.setId(id);
            soinService.createSoin(soin);
            return ResponseEntity.ok(soin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSoin(@PathVariable Long id) {
        try {
            soinService.deleteSoin(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}