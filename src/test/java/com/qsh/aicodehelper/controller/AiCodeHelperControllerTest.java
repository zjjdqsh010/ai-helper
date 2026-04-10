package com.qsh.aicodehelper.controller;

import com.qsh.aicodehelper.service.AiCodeHelperServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AiCodeHelperControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AiCodeHelperServiceImpl aiCodeHelperService;

    private AiCodeHelperController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new AiCodeHelperController(aiCodeHelperService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testChat() throws Exception {
        // Arrange
        when(aiCodeHelperService.chat(anyString(), anyString()))
                .thenReturn("This is a test response for chat");

        String requestBody = "{\"message\": \"Hello, how are you?\", \"sessionId\": \"test-session-123\"}";

        // Act & Assert
        mockMvc.perform(post("/api/ai-helper/chat")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("test-session-123"))
                .andExpect(jsonPath("$.response").value("This is a test response for chat"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testCodeReview() throws Exception {
        // Arrange
        when(aiCodeHelperService.codeReview(anyString(), anyString()))
                .thenReturn("Code review completed: Good code quality");

        String requestBody = "{\"code\": \"public class Test {}\", \"language\": \"java\"}";

        // Act & Assert
        mockMvc.perform(post("/api/ai-helper/code-review")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Code review completed: Good code quality"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testDebug() throws Exception {
        // Arrange
        when(aiCodeHelperService.debug(anyString(), anyString()))
                .thenReturn("Debug analysis: Found null pointer exception");

        String requestBody = "{\"code\": \"String s = null; s.length();\", \"error\": \"NullPointerException\"}";

        // Act & Assert
        mockMvc.perform(post("/api/ai-helper/debug")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Debug analysis: Found null pointer exception"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testOptimize() throws Exception {
        // Arrange
        when(aiCodeHelperService.optimize(anyString()))
                .thenReturn("Optimization suggestions: Use StringBuilder for string concatenation");

        String requestBody = "{\"code\": \"String result = \"\"; for(int i=0; i<100; i++) { result += i; }\"}";

        // Act & Assert
        mockMvc.perform(post("/api/ai-helper/optimize")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Optimization suggestions: Use StringBuilder for string concatenation"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testExplain() throws Exception {
        // Arrange
        when(aiCodeHelperService.explain(anyString()))
                .thenReturn("This code demonstrates a simple loop structure");

        String requestBody = "{\"code\": \"for(int i=0; i<10; i++) { System.out.println(i); }\"}";

        // Act & Assert
        mockMvc.perform(post("/api/ai-helper/explain")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("This code demonstrates a simple loop structure"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}