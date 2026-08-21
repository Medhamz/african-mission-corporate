package com.africanmission.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // ============================================
    // 1. API MOBILE (Stateless, JWT)
    // ============================================
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/mobile/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/mobile/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ============================================
    // 2. SITE WEB & ADMIN (avec sessions)
    // ============================================
    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(userDetailsService)
                // Désactivation ciblée de la protection CSRF sur les endpoints de formulaire POST back-office et APIs
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/admin/**",
                        "/newsletter/**",
                        "/chat/**",
                        "/contact/**",
                        "/api/**"
                ))
                .authorizeHttpRequests(authz -> authz
                        // Autoriser les ressources statiques par défaut de Spring Boot
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                        // Autoriser toutes les API publiques
                        .requestMatchers("/api/market/**", "/api/projects/**", "/api/diagnostic/**", "/api/world/**", "/api/eco/**").permitAll()

                        // Pages publiques et routes du Diagnostiqueur
                        .requestMatchers(
                                "/", "/about", "/activities", "/contact", "/devis",
                                "/services", "/projects", "/team", "/faq",
                                "/blog", "/legal", "/sitemap", "/careers",
                                "/testimonials", "/gallery",

                                // Diagnostic
                                "/diagnostiqueur", "/diagnostiqueur/**", "/diagnostic", "/diagnostic.html",

                                // Autres fonctionnalités publiques
                                "/chiffres-cles", "/key-figures", "/key-figures/**",
                                "/monde", "/monde/**", "/carte-monde", "/world-map",
                                "/eco", "/eco/**", "/eco-dashboard",

                                // Ressources statiques explicites & dossier des fichiers envoyés
                                "/css/**", "/js/**", "/images/**", "/webjars/**", "/uploads/**", "/manifest.json", "/favicon.ico",
                                "/newsletter/**", "/search", "/chat/**", "/contact/**", "/maintenance", "/kiosk"
                        ).permitAll()

                        // Administration
                        .requestMatchers("/admin/**").hasRole("SUPER_ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .failureUrl("/admin/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}