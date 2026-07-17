package com.africanmission.service;

import com.africanmission.model.AdminUser;
import com.africanmission.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUser createUser(AdminUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return adminUserRepository.save(user);
    }

    public AdminUser updateUser(Long id, AdminUser userData) {
        AdminUser user = adminUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setFullName(userData.getFullName());
        user.setEmail(userData.getEmail());
        user.setRole(userData.getRole());
        user.setIsActive(userData.getIsActive());
        if (userData.getPassword() != null && !userData.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userData.getPassword()));
        }
        return adminUserRepository.save(user);
    }

    public void deleteUser(Long id) {
        adminUserRepository.deleteById(id);
    }

    public List<AdminUser> getAllUsers() {
        return adminUserRepository.findAll();
    }

    public AdminUser getUserById(Long id) {
        return adminUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public AdminUser getUserByUsername(String username) {
        return adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public void updateLastLogin(String username) {
        AdminUser user = getUserByUsername(username);
        user.setLastLogin(LocalDateTime.now());
        adminUserRepository.save(user);
    }

    public long countActiveUsers() {
        return adminUserRepository.findByIsActiveTrue().size();
    }
}