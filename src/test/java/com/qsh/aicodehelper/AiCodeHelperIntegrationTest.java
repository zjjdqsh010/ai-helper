package com.qsh.aicodehelper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AiCodeHelperIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testChatEndpoint() throws Exception {
        String requestBody = "{\"message\": \"What is Java?\", \"sessionId\": \"integration-test-session\"}";

        mockMvc.perform(post("/api/ai-helper/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("integration-test-session"))
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testCodeReviewEndpoint() throws Exception {
        String requestBody = "{\"code\": \"public class HelloWorld { public static void main(String[] args) { System.out.println(\\\"Hello World\\\"); } }\", \"language\": \"java\"}";

        mockMvc.perform(post("/api/ai-helper/code-review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testDebugEndpoint() throws Exception {
        String requestBody = "{\"code\": \"String name = null; int length = name.length();\", \"error\": \"NullPointerException at line 1\"}";

        mockMvc.perform(post("/api/ai-helper/debug")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testOptimizeEndpoint() throws Exception {
        String requestBody = "{\"code\": \"String result = \\\"\\\"; for(int i = 0; i < 1000; i++) { result += i; }\"}";

        mockMvc.perform(post("/api/ai-helper/optimize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testExplainEndpoint() throws Exception {
        String requestBody = "{\"code\": \"for(int i = 0; i < 10; i++) { System.out.println(\\\"Number: \\\" + i); }\"}";

        mockMvc.perform(post("/api/ai-helper/explain")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testChatWithoutSessionId() throws Exception {
        String requestBody = "{\"message\": \"Generate a Java method to calculate factorial\"}";

        mockMvc.perform(post("/api/ai-helper/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}