package com.tms.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.dto.LoginRequest;
import com.tms.dto.RegisterRequest;
import com.tms.model.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final ObjectMapper mapper = new ObjectMapper();

    private String jwtToken;

    @BeforeEach
    public void registerAndLogin() {
        // 1. Register ADMIN user
        RegisterRequest registerRequest = new RegisterRequest("testadmin", "admin@email.com", "admin123", "ROLE_ADMIN");
        restTemplate.postForEntity("/auth/registerNewUser", registerRequest, String.class);

        // 2. Login and get token
        LoginRequest loginRequest = new LoginRequest("testadmin", "admin123");
        ResponseEntity<String> loginResponse = restTemplate.postForEntity("/auth/login", loginRequest, String.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        try {
            Map<String, Object> body = mapper.readValue(loginResponse.getBody(), Map.class);
            jwtToken = (String) body.get("token"); 
            assertThat(jwtToken).isNotNull();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("Failed to parse login response");
        }
    }

    @Test
    public void testCreateTaskWithJwtToken() {
        // 3. Use token to create task
        Task task = Task.builder()
                .title("Integration Task")
                .description("Done by test")
                .dueDate(LocalDate.now().plusDays(2))
                .priority(Priority.MEDIUM)
                .status(Status.PENDING)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken); 
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Task> request = new HttpEntity<>(task, headers);

        ResponseEntity<Task> response = restTemplate.postForEntity("/tasks/addTask", request, Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Integration Task");
    }
}
