package com.tms.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.dto.LoginRequest;
import com.tms.dto.RegisterRequest;
import com.tms.model.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String jwtToken;
    private HttpHeaders headers;

    @BeforeEach
    public void setupAuthAndHeaders() throws Exception {
        // 1. Register ADMIN user
        RegisterRequest registerRequest = new RegisterRequest("admin2", "admin2@email.com", "admin123", "ROLE_ADMIN");
        restTemplate.postForEntity("/auth/registerNewUser", registerRequest, String.class);

        // 2. Login with correct endpoint
        LoginRequest loginRequest = new LoginRequest("admin2", "admin123");
        ResponseEntity<String> loginResponse = restTemplate.postForEntity("/auth/login", loginRequest, String.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 3. Parse the token safely from JSON response
        Map<String, Object> body = new ObjectMapper().readValue(loginResponse.getBody(), Map.class);
        jwtToken = (String) body.get("token");
        assertThat(jwtToken).isNotNull();

        // 4. Setup Authorization header
        headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
    }


    @Test
    public void testAddAndGetTask() {
        Task task = Task.builder()
                .title("Task via integration")
                .description("integration test")
                .dueDate(LocalDate.now().plusDays(3))
                .priority(Priority.HIGH)
                .status(Status.PENDING)
                .build();

        HttpEntity<Task> request = new HttpEntity<>(task, headers);

        // Create task
        ResponseEntity<Task> createResponse = restTemplate.postForEntity("/tasks/addTask", request, Task.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long taskId = createResponse.getBody().getTaskId();

        // Get by ID
        ResponseEntity<Task> getResponse = restTemplate.exchange("/tasks/getById/" + taskId,
                HttpMethod.GET, new HttpEntity<>(headers), Task.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getTitle()).isEqualTo("Task via integration");
    }

    @Test
    public void testUpdateTaskStatus() {
        // Create task first
        Task task = Task.builder()
                .title("To Complete")
                .dueDate(LocalDate.now().plusDays(1))
                .priority(Priority.MEDIUM)
                .status(Status.PENDING)
                .build();

        ResponseEntity<Task> created = restTemplate.postForEntity("/tasks/addTask", new HttpEntity<>(task, headers), Task.class);
        Long taskId = created.getBody().getTaskId();

        // Now update status
        HttpEntity<?> patchRequest = new HttpEntity<>(headers);
        ResponseEntity<String> patchResponse = restTemplate.exchange(
                "/tasks/updateTask/" + taskId + "/status?status=COMPLETED",
                HttpMethod.PATCH,
                patchRequest,
                String.class
        );

        assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testDeleteTask() {
        // Create task first
        Task task = Task.builder()
                .title("Delete Me")
                .dueDate(LocalDate.now().plusDays(1))
                .priority(Priority.LOW)
                .status(Status.PENDING)
                .build();

        ResponseEntity<Task> created = restTemplate.postForEntity("/tasks/addTask", new HttpEntity<>(task, headers), Task.class);
        Long taskId = created.getBody().getTaskId();

        // Now delete
        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                "/tasks/deleteTask/" + taskId,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                String.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
