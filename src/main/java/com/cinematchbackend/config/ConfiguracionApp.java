package com.cinematchbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class ConfiguracionApp {

    private static ConfiguracionApp instancia;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;


    private ConfiguracionApp() {}

    @PostConstruct
    private void inicializar() {
        instancia = this;
    }

    public static ConfiguracionApp getInstance() {
        return instancia;
    }

    public String getJwtSecret() { return jwtSecret; }
    public Long getJwtExpiration() { return jwtExpiration; }

}