package com.example.gestion.patient.model;

import lombok.Data;

@Data
public class Soin {
    private Long id;
    private String codeSoin;
    private String designation;
    private Double price;
}
