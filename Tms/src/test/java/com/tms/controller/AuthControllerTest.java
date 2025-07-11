package com.tms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.config.JwtAuthFilter;
import com.tms.dto.*;
import com.tms.model.User;
import com.tms.repo.UserRepository;
import com.tms.util.JwtUtil;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@WebMvcTest(
	    controllers = AuthController.class,
	    excludeFilters = {
	        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class)
	    }
	)
	@AutoConfigureMockMvc(addFilters = false)
	public class AuthControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testRegister_Success() throws Exception {
        RegisterRequest request = new RegisterRequest("testuser", "test@email.com", "password", "ROLE_USER");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/registerNewUser") 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegister_DuplicateUser() throws Exception {
        RegisterRequest request = new RegisterRequest("testuser", "test@email.com", "password", "ROLE_USER");

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(User.builder().username("testuser").build()));

        mockMvc.perform(post("/auth/registerNewUser") // ✅ fixed endpoint
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLogin_Success() throws Exception {
        LoginRequest request = new LoginRequest("john", "123456");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(User.builder()
                        .username("john")
                        .password("encodedPassword")
                        .role("ROLE_USER")
                        .email("john@email.com")
                        .build()));

        when(jwtUtil.generateToken("john")).thenReturn("mock-jwt");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt"))
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    public void testLogin_InvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("wronguser", "wrongpass");

        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(authenticationManager)
                .authenticate(any());

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
