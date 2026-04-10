package com.qsh.aicodehelper;

import com.qsh.aicodehelper.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
@RequiredArgsConstructor
@Slf4j
public class AuthTest {

    @Autowired(required = false)
    private AuthService authService;

    @Test
    public void testContextLoads() {
        log.info("AuthService bean loaded: {}", authService != null);
        if (authService != null) {
            log.info("Authentication service is available");
        }
    }
}