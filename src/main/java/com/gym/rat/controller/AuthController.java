package com.gym.rat.controller;

import com.gym.rat.model.Usuario;
import com.gym.rat.model.Rol;
import com.gym.rat.repository.UsuarioRepository;
import com.gym.rat.repository.RolRepository;
import com.gym.rat.dto.LoginRequest;
import com.gym.rat.dto.RegisterRequest;
import com.gym.rat.dto.RefreshRequest;
import com.gym.rat.dto.JwtResponse;
import com.gym.rat.config.JwtTokenUtil;
import com.gym.rat.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // --- Registro ---
    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegisterRequest request) {
        if (usuarioRepository.findByEmailIgnoreCase(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("❌ El email ya está registrado.");
        }

        Rol userRol = rolRepository.findByNombre("ROLE_USER")
                .orElseGet(() -> rolRepository.save(new Rol("ROLE_USER")));

        Set<Rol> roles = new HashSet<>();
        roles.add(userRol);

        Usuario nuevo = new Usuario(
                request.getNombre(),
                request.getEmail(),
                request.getPassword(),
                roles
        );

        usuarioRepository.save(nuevo);

        return ResponseEntity.ok("✅ Usuario registrado correctamente: " + nuevo.getEmail());
    }

    // --- Login ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<Usuario> encontrado = usuarioRepository.findByEmailIgnoreCase(request.getEmail());
        if (encontrado.isEmpty()) return ResponseEntity.status(401).body("❌ Usuario no encontrado.");

        Usuario user = encontrado.get();
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("❌ Contraseña incorrecta.");
        }

        Set<String> roles = user.getRoles().stream().map(Rol::getNombre).collect(Collectors.toSet());
        String accessToken = jwtTokenUtil.generarAccessToken(user.getEmail(), roles);
        String refreshToken = jwtTokenUtil.generarRefreshToken(user.getEmail());

        // Guardar refresh token en memoria (o BD)
        refreshTokenService.saveToken(user.getEmail(), refreshToken);

        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken, user, roles));
    }

    // --- Renovar access token con refresh token ---
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        if (refreshToken == null || !jwtTokenUtil.validarToken(refreshToken)) {
            return ResponseEntity.status(401).body("❌ Refresh token inválido.");
        }

        String email = jwtTokenUtil.obtenerEmail(refreshToken);
        Usuario user = usuarioRepository.findByEmailIgnoreCase(email).orElse(null);
        if (user == null) return ResponseEntity.status(401).body("❌ Usuario no encontrado.");

        // Validar refresh token guardado
        if (!refreshTokenService.validateToken(email, refreshToken)) {
            return ResponseEntity.status(401).body("❌ Refresh token no coincide.");
        }

        Set<String> roles = user.getRoles().stream().map(Rol::getNombre).collect(Collectors.toSet());
        String newAccessToken = jwtTokenUtil.generarAccessToken(email, roles);

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);
        return ResponseEntity.ok(response);
    }
}
