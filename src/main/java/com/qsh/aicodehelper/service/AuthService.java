package com.qsh.aicodehelper.service;

import com.qsh.aicodehelper.entity.User;
import com.qsh.aicodehelper.repository.UserRepository;
import com.qsh.aicodehelper.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public Map<String, Object> register(String username, String email, String password, String nickname) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname != null ? nickname : username);
        user.setStatus(1);

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        userMap.put("email", user.getEmail());
        userMap.put("nickname", user.getNickname() != null ? user.getNickname() : "");
        userMap.put("createdAt", user.getCreatedAt());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", userMap);

        return response;
    }

    @Transactional
    public Map<String, Object> login(String username, String password) {
        try {
            log.info("Attempting login for user: {}", username);
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            log.info("User found, checking password...");
            if (!passwordEncoder.matches(password, user.getPassword())) {
                log.warn("Password mismatch for user: {}", username);
                throw new RuntimeException("Invalid password");
            }

            log.info("Password verified, updating last login time...");
            userRepository.updateLastLoginTime(user.getId(), LocalDateTime.now());

            log.info("Generating JWT token...");
            String token = jwtUtil.generateToken(user.getUsername());

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("email", user.getEmail());
            userMap.put("nickname", user.getNickname() != null ? user.getNickname() : "");
            if (user.getLastLoginAt() != null) {
                userMap.put("lastLoginAt", user.getLastLoginAt());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", userMap);

            log.info("Login successful for user: {}", username);
            return response;
        } catch (RuntimeException e) {
            log.warn("Authentication failed for user: {}", username, e);
            throw new RuntimeException("Invalid username or password");
        }
    }

    public void logout(String token) {
        // Token会在客户端删除，这里可以添加token黑名单逻辑（可选）
    }

    public Map<String, Object> getCurrentUser(String token) {
        log.info("Validating token...");
        if (!jwtUtil.validateToken(token)) {
            log.warn("Token validation failed");
            throw new RuntimeException("Invalid token");
        }

        log.info("Extracting username from token...");
        String username = jwtUtil.getUsernameFromToken(token);
        log.info("Username from token: {}", username);

        log.info("Finding user by username...");
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("Building response for user: {}", user.getUsername());
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        userMap.put("email", user.getEmail());
        userMap.put("nickname", user.getNickname() != null ? user.getNickname() : "");
        userMap.put("lastLoginAt", user.getLastLoginAt());
        userMap.put("status", user.getStatus());

        Map<String, Object> response = new HashMap<>();
        response.put("user", userMap);

        return response;
    }
}