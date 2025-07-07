package com.example.gestion.patient.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class Seance {
    private Long id;
    private String codeP;
    private String codeSoin;
    private String dateSoin; 
    private List<Long> participants;
    private String status;
    private String notes;
    private Long createdBy;
    private String createdAt; 
    private String updatedAt;
    private String adminResponse;

    public Seance() {
        this.participants = new ArrayList<>();
        this.status = "WAITING_APPROVAL";
        this.createdAt = LocalDate.now().toString(); 
        this.updatedAt = LocalDate.now().toString(); 
    }
    
    @JsonIgnore
    public LocalDate getDateSoinAsLocalDate() {
        return dateSoin != null ? LocalDate.parse(dateSoin) : null;
    }
    
    @JsonIgnore
    public void setDateSoinFromLocalDate(LocalDate date) {
        this.dateSoin = date != null ? date.toString() : null;
    }
}
