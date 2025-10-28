package com.gym.rat.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
public class JwtTokenUtil {

    private static final String SECRET_KEY = "EstaEsUnaClaveSuperSecretaGymRat1234567890";
    private static final long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 15; // 15 min
    private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7; // 7 días

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // --- Generar access token ---
    public String generarAccessToken(String email, Set<String> roles) {
        return generarToken(email, roles, ACCESS_TOKEN_EXPIRATION);
    }

    // --- Generar refresh token ---
    public String generarRefreshToken(String email) {
        return generarToken(email, Collections.emptySet(), REFRESH_TOKEN_EXPIRATION);
    }

    private String generarToken(String email, Set<String> roles, long expirationTime) {
        Map<String, Object> claims = new HashMap<>();
        if (!roles.isEmpty()) claims.put("roles", roles);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String obtenerEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validarToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
