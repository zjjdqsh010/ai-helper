package com.qsh.aicodehelper.controller;

import com.qsh.aicodehelper.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String email = request.get("email");
            String password = request.get("password");
            String nickname = request.get("nickname");

            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", "Username is required"));
            }

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", "Email is required"));
            }

            if (password == null || password.length() < 6) {
                return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", "Password must be at least 6 characters"));
            }

            Map<String, Object> response = authService.register(username, email, password, nickname);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", e.getMessage() != null ? e.getMessage() : "Operation failed"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");

            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", "Username is required"));
            }

            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", "Password is required"));
            }

            Map<String, Object> response = authService.login(username, password);
            if (response == null) {
                return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", "Login service returned null"));
            }
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("Login failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", e.getMessage() != null ? e.getMessage() : "Login failed"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            authService.logout(token);
            return ResponseEntity.ok(java.util.Collections.singletonMap("message", "Logged out successfully"));
        } catch (Exception e) {
            log.warn("Logout failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", "Logout failed"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Map<String, Object> response = authService.getCurrentUser(token);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warn("Get current user failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", e.getMessage() != null ? e.getMessage() : "Operation failed"));
        }
    }
}