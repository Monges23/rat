package com.gym.rat.config;

import com.gym.rat.model.Rol;
import com.gym.rat.model.Usuario;
import com.gym.rat.repository.RolRepository;
import com.gym.rat.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(RolRepository rolRepo, UsuarioRepository usuarioRepo) {
        return args -> {

            Rol adminRol = rolRepo.findByNombre("ROLE_ADMIN")
                    .orElseGet(() -> rolRepo.save(new Rol("ROLE_ADMIN")));

            Rol userRol = rolRepo.findByNombre("ROLE_USER")
                    .orElseGet(() -> rolRepo.save(new Rol("ROLE_USER")));

            usuarioRepo.findByEmailIgnoreCase("rafa@ejemplo.com").orElseGet(() -> {
                Set<Rol> roles = new HashSet<>();
                roles.add(adminRol);

                Usuario admin = new Usuario("Administrador", "rafa@ejemplo.com", "adminrafa", roles);
                usuarioRepo.save(admin);

                System.out.println("Creando usuario admin:");
                System.out.println("Email: " + admin.getEmail());
                System.out.println("Password plain: adminrafa");
                System.out.println("Password hash: " + admin.getPassword());
                return admin;
            });

            usuarioRepo.findAll().forEach(u ->
                System.out.println("Usuario en DB: " + u.getEmail() + " / " + u.getPassword())
            );
        };
    }
}


