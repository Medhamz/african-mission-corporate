package com.africanmission.service;

import com.africanmission.model.AdminRole;
import com.africanmission.repository.AdminRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminRoleService {

    private final AdminRoleRepository adminRoleRepository;

    public List<AdminRole> getAllRoles() {
        return adminRoleRepository.findAll();
    }

    public AdminRole getRoleById(Long id) {
        return adminRoleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));
    }

    public AdminRole getRoleByName(String name) {
        return adminRoleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));
    }

    public AdminRole save(AdminRole role) {
        return adminRoleRepository.save(role);
    }

    public void delete(Long id) {
        adminRoleRepository.deleteById(id);
    }

    public boolean hasPermission(String roleName, String permission) {
        AdminRole role = getRoleByName(roleName);
        return role.getPermissions() != null && role.getPermissions().contains(permission);
    }
}