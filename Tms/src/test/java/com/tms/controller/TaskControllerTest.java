package com.tms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tms.config.JwtAuthFilter;
import com.tms.model.*;
import com.tms.repo.TaskRepository;
import com.tms.util.JwtUtil;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@WebMvcTest(
	    controllers = TaskController.class,
	    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class)
	)
	@AutoConfigureMockMvc(addFilters = false)
	public class TaskControllerTest {


    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private TaskRepository taskRepo;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    public void testCreateTask_Success() throws Exception {
        Task task = Task.builder()
                .title("Task 1")
                .description("Test description")
                .dueDate(LocalDate.now().plusDays(2))
                .priority(Priority.HIGH)
                .status(Status.PENDING)
                .build();

        when(taskRepo.save(any(Task.class))).thenReturn(task);

        mockMvc.perform(post("/tasks/addTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(task)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateTask_Success() throws Exception {
        Task oldTask = Task.builder().taskId(1L).title("Old").status(Status.PENDING).build();
        Task updatedTask = Task.builder().title("Updated").description("Updated desc")
                .dueDate(LocalDate.now().plusDays(2)).priority(Priority.MEDIUM).status(Status.IN_PROGRESS).build();

        when(taskRepo.findById(1L)).thenReturn(Optional.of(oldTask));
        when(taskRepo.save(any(Task.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/tasks/updateTask/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateTaskStatus_Success() throws Exception {
        Task task = Task.builder().taskId(1L).status(Status.PENDING).build();

        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepo.save(any(Task.class))).thenReturn(task);

        mockMvc.perform(patch("/tasks/updateTask/1/status")
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllTasks() throws Exception {
        List<Task> tasks = List.of(
                Task.builder().taskId(1L).title("T1").build(),
                Task.builder().taskId(2L).title("T2").build()
        );

        when(taskRepo.findAll()).thenReturn(tasks);

        mockMvc.perform(get("/tasks/showAll"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetTaskById_Success() throws Exception {
        Task task = Task.builder().taskId(1L).title("Task 1").build();

        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));

        mockMvc.perform(get("/tasks/getById/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteTask_Success() throws Exception {
        when(taskRepo.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepo).deleteById(1L);

        mockMvc.perform(delete("/tasks/deleteTask/1"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testGetTaskById_NotFound() throws Exception {
        when(taskRepo.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/tasks/getById/99"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testUpdateTaskStatus_InvalidStatus() throws Exception {
        Task task = Task.builder().taskId(1L).status(Status.PENDING).build();
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));

        mockMvc.perform(patch("/tasks/updateTask/1/status")
                        .param("status", "INVALID"))
                .andExpect(status().isBadRequest());
    }


}
