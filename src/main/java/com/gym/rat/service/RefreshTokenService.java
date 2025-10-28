package com.gym.rat.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RefreshTokenService {

    private final Map<String, String> refreshTokenStore = new HashMap<>();
    // email -> refreshToken

    public void saveToken(String email, String refreshToken) {
        refreshTokenStore.put(email, refreshToken);
    }

    public boolean validateToken(String email, String refreshToken) {
        return refreshToken.equals(refreshTokenStore.get(email));
    }

    public void deleteToken(String email) {
        refreshTokenStore.remove(email);
    }
}
