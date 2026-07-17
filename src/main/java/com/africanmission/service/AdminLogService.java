package com.africanmission.service;

import com.africanmission.model.AdminLog;
import com.africanmission.repository.AdminLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminLogService {

    private final AdminLogRepository adminLogRepository;

    public void log(String username, String action, String details, String ipAddress) {
        AdminLog log = new AdminLog();
        log.setUsername(username);
        log.setAction(action);
        log.setDetails(details);
        log.setIpAddress(ipAddress);
        adminLogRepository.save(log);
    }

    public List<AdminLog> getRecentLogs() {
        return adminLogRepository.findTop50ByOrderByCreatedAtDesc();
    }

    public void clearLogs() {
        adminLogRepository.deleteAll();
    }
}