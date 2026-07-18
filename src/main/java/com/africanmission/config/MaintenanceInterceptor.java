package com.africanmission.config;

import com.africanmission.service.MaintenanceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class MaintenanceInterceptor implements HandlerInterceptor {

    private final MaintenanceService maintenanceService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // Exclure les chemins qui ne doivent pas être bloqués
        if (uri.startsWith("/admin") || uri.startsWith("/css") || uri.startsWith("/js") ||
                uri.startsWith("/images") || uri.startsWith("/webjars") || uri.startsWith("/error") ||
                uri.startsWith("/maintenance") || uri.startsWith("/uploads")) {  // ⬅️ AJOUT de /maintenance
            return true;
        }

        if (maintenanceService.isMaintenanceMode()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth != null && auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") ||
                            a.getAuthority().equals("ROLE_SUPER_ADMIN"));
            if (!isAdmin) {
                response.sendRedirect("/maintenance");
                return false;
            }
        }
        return true;
    }
}