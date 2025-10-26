package com.gym.rat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // desactiva CSRF (innecesario para APIs REST)
            .authorizeHttpRequests(auth -> auth
                // permite acceso a HTML, CSS, JS y login/registro
                .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/auth/**", "/error").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())   // desactiva el formulario HTML de Spring
            .httpBasic(basic -> basic.disable()); // desactiva login básico por cabecera

        return http.build();
    }
}

