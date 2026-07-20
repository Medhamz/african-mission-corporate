package com.africanmission.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(userDetailsService)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/about", "/activities", "/contact",
                                "/services", "/projects", "/team", "/faq",
                                "/blog", "/legal", "/sitemap", "/careers",
                                "/testimonials", "/gallery", "/key-figures",
                                "/css/**", "/js/**", "/images/**", "/webjars/**",
                                "/newsletter/**", "/search", "/chat/**", "/contact/**",
                                "/maintenance",
                                "/api/market/**",
                                "/api/projects/**",
                                "/diagnostiqueur" // ⬅️ AJOUT : rendre la page Diagnostiqueur publique
                        ).permitAll()
                        // Admin - accès par rôle
                        .requestMatchers("/admin/dashboard", "/admin/activities", "/admin/partners",
                                "/admin/projects-admin", "/admin/testimonials", "/admin/team-members",
                                "/admin/faqs", "/admin/messages", "/admin/chat-sessions",
                                "/admin/newsletter").hasAnyRole("ADMIN", "SUPER_ADMIN", "EDITOR")
                        .requestMatchers("/admin/users", "/admin/settings", "/admin/logs",
                                "/admin/roles", "/admin/notifications",
                                "/admin/export/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
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
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/admin/**", "/newsletter/**", "/chat/**", "/contact/**")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}