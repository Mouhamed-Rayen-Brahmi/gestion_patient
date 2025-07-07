package com.example.gestion.patient.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender emailSender;
    
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage(); 
        message.setFrom("noreply@gestionpatient.com");
        message.setTo(to); 
        message.setSubject(subject); 
        message.setText(text);
        emailSender.send(message);
    }
    
    public void sendSeanceRequestApproval(String to, String patientName, Long seanceId, String dateSoin, String soinName, String adminMessage) {
        String subject = "Seance Request Approved";
        String text = "Dear " + patientName + ",\n\n" +
                "Your seance request (ID: " + seanceId + ") for " + soinName + " on " + dateSoin + " has been APPROVED.\n\n" +
                "Message from admin: " + adminMessage + "\n\n" +
                "Thank you for using our services.\n" +
                "Patient Management System";
        
        sendSimpleMessage(to, subject, text);
    }
    
    public void sendSeanceRequestRejection(String to, String patientName, Long seanceId, String dateSoin, String soinName, String adminMessage) {
        String subject = "Seance Request Declined";
        String text = "Dear " + patientName + ",\n\n" +
                "We regret to inform you that your seance request (ID: " + seanceId + ") for " + soinName + " on " + dateSoin + " has been DECLINED.\n\n" +
                "Reason: " + adminMessage + "\n\n" +
                "Please contact us if you have any questions.\n" +
                "Patient Management System";
        
        sendSimpleMessage(to, subject, text);
    }
}