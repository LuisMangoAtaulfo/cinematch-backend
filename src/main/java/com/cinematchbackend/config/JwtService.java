package com.cinematchbackend.config;

import com.cinematchbackend.entities.TokenInvalidadoEntidad;
import com.cinematchbackend.repositories.TokenInvalidadoRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final TokenInvalidadoRepository tokenInvalidadoRepository;

    public String generarToken(Long id, String correo, String rol) {
        return Jwts.builder()
                .subject(correo)
                .claim("rol", rol)
                .claim("id", id)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ConfiguracionApp.getInstance().getJwtExpiration()))
                .signWith(getKey())
                .compact();
    }

    public Long extraerId(String token) {
        return extraerClaims(token).get("id", Long.class);
    }

    public String extraerCorreo(String token) {
        return extraerClaims(token).getSubject();
    }

    public String extraerRol(String token) {
        return extraerClaims(token).get("rol", String.class);
    }

    public boolean esValido(String token) {
        if (tokenInvalidadoRepository.existsByToken(token)) return false;
        try {
            extraerClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void invalidarToken(String token) {
        if (!tokenInvalidadoRepository.existsByToken(token)) {
            TokenInvalidadoEntidad entidad = new TokenInvalidadoEntidad();
            entidad.setToken(token);
            tokenInvalidadoRepository.save(entidad);
        }
    }

    private Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(
                ConfiguracionApp.getInstance().getJwtSecret()
                        .getBytes(StandardCharsets.UTF_8)
        );
    }
}