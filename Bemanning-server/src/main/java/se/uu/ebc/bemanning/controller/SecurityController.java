package se.uu.ebc.bemanning.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller for handling security-related endpoints and user information.
 */
@Controller
@Slf4j
public class SecurityController {

    /**
     * Login page - redirects to CAS if not authenticated
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * User profile page showing current user information
     */
    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("authorities", auth.getAuthorities());
            log.debug("User profile accessed by: {}", auth.getName());
        }
        return "profile";
    }

    /**
     * REST endpoint to get current user information
     */
    @GetMapping("/rest/user/current")
    @ResponseBody
    public Object getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return new UserInfo(auth.getName(), auth.getAuthorities().toString());
        }
        return null;
    }

    /**
     * Admin page - requires ADMIN role
     */
    @GetMapping("/admin")
    public String admin(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        log.info("Admin page accessed by: {}", auth.getName());
        return "admin";
    }

    /**
     * Simple DTO for user information
     */
    public static class UserInfo {
        private String username;
        private String authorities;

        public UserInfo(String username, String authorities) {
            this.username = username;
            this.authorities = authorities;
        }

        public String getUsername() {
            return username;
        }

        public String getAuthorities() {
            return authorities;
        }
    }
}