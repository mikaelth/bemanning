package se.uu.ebc.bemanning.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Test controller for security configuration.
 * Only active when security.test.enabled=true
 */
@RestController
@RequestMapping("/security-test")
@ConditionalOnProperty(name = "security.test.enabled", havingValue = "true")
@Slf4j
public class SecurityTestController {

    @GetMapping("/public")
    public Map<String, Object> publicEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a public endpoint");
        response.put("timestamp", System.currentTimeMillis());
        response.put("authenticated", false);
        
        log.info("Public endpoint accessed");
        return response;
    }

    @GetMapping("/authenticated")
    public Map<String, Object> authenticatedEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is an authenticated endpoint");
        response.put("timestamp", System.currentTimeMillis());
        response.put("authenticated", auth != null && auth.isAuthenticated());
        
        if (auth != null) {
            response.put("username", auth.getName());
            response.put("authorities", auth.getAuthorities());
            response.put("principal", auth.getPrincipal().getClass().getSimpleName());
        }
        
        log.info("Authenticated endpoint accessed by: {}", auth != null ? auth.getName() : "anonymous");
        return response;
    }

    @GetMapping("/admin")
    public Map<String, Object> adminEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is an admin endpoint");
        response.put("timestamp", System.currentTimeMillis());
        response.put("authenticated", auth != null && auth.isAuthenticated());
        
        if (auth != null) {
            response.put("username", auth.getName());
            response.put("authorities", auth.getAuthorities());
            response.put("hasAdminRole", auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        }
        
        log.info("Admin endpoint accessed by: {}", auth != null ? auth.getName() : "anonymous");
        return response;
    }
}