package com.qsh.aicodehelper.filter;

import com.qsh.aicodehelper.service.CustomUserDetailsService;
import com.qsh.aicodehelper.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                    String username = jwtUtil.getUsernameFromToken(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Authenticated user: {}", username);
                }
            }
        } catch (Exception e) {
            log.warn("JWT authentication failed: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}