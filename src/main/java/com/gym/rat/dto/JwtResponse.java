package com.gym.rat.dto;

import com.gym.rat.model.Usuario;
import java.util.Set;

public class JwtResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private String email;
    private String nombre;
    private Set<String> roles;

    public JwtResponse(String accessToken, String refreshToken, Usuario usuario, Set<String> roles) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.email = usuario.getEmail();
        this.nombre = usuario.getNombre();
        this.roles = roles;
    }

    // Getters y setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
