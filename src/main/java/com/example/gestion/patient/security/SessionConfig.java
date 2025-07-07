package com.example.gestion.patient.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class SessionConfig {
    
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();

        serializer.setCookieName("PATIENT-SESSION");
        serializer.setCookieMaxAge(30 * 24 * 60 * 60); 
        serializer.setUseSecureCookie(false); 
        return serializer;
    }
}