package com.africanmission.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(userDetailsService)
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/admin/**", "/newsletter/**", "/chat/**", "/contact/**"))
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

        // ====== CONFIGURATION DE L'API MOBILE (JWT) ======
        http
                .securityMatcher("/api/mobile/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/mobile/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // ====== CONFIGURATION DU SITE WEB ======
        http
                .securityMatcher("/**")
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/", "/about", "/activities", "/contact",
                                "/services", "/projects", "/team", "/faq",
                                "/blog", "/legal", "/sitemap", "/careers",
                                "/testimonials", "/gallery", "/key-figures",
                                "/css/**", "/js/**", "/images/**", "/webjars/**",
                                "/uploads/**",
                                "/newsletter/**", "/search", "/chat/**", "/contact/**",
                                "/maintenance",
                                "/api/market/**",
                                "/api/projects/**",
                                "/diagnostiqueur",
                                "/monde",
                                "/carte-monde",
                                "/api/world/**",
                                "/eco",
                                "/api/eco/**",
                                "/kiosk"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("SUPER_ADMIN")
                        .anyRequest().authenticated()
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