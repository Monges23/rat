package com.gym.rat.controller;

import com.gym.rat.model.Usuario;
import com.gym.rat.model.Rol;
import com.gym.rat.repository.UsuarioRepository;
import com.gym.rat.repository.RolRepository;
import com.gym.rat.dto.LoginRequest;
import com.gym.rat.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

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

        if (encontrado.isEmpty()) {
            return ResponseEntity.status(401).body("❌ Usuario no encontrado.");
        }

        Usuario user = encontrado.get();

        System.out.println("Intento login:");
        System.out.println("Email recibido: " + request.getEmail());
        System.out.println("Password recibido (texto plano): " + request.getPassword());
        System.out.println("Password hash en DB: " + user.getPassword());

        boolean matches = encoder.matches(request.getPassword(), user.getPassword());
        System.out.println("Resultado matches: " + matches);

        if (!matches) {
            return ResponseEntity.status(401).body("❌ Contraseña incorrecta.");
        }

        return ResponseEntity.ok("✅ Login exitoso. Bienvenido, " + user.getNombre() + "!");
    }
}
