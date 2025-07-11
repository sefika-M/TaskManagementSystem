package com.tms.controller;

import com.tms.model.Task;
import com.tms.repo.TaskRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")

@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepo;

    // Get all tasks (USER + ADMIN)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/showAll")
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskRepo.findAll());
    }

    // Get task by ID (USER + ADMIN)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getTask(@PathVariable Long id) {
        Optional<Task> task = taskRepo.findById(id);
        return task.isPresent() ?
                ResponseEntity.ok(task.get()) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
    }

    // Create new task (ADMIN only)
    @PostMapping("/addTask")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createTask(@Valid @RequestBody Task task) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskRepo.save(task));
    }

    // Update task (ADMIN only)
    @PutMapping("/updateTask/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @Valid @RequestBody Task updatedTask) {
        Optional<Task> optionalTask = taskRepo.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setDueDate(updatedTask.getDueDate());
            task.setPriority(updatedTask.getPriority());
            task.setStatus(updatedTask.getStatus());
            return ResponseEntity.ok(taskRepo.save(task));
        }else{
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }
    }
    
    @PatchMapping("updateTask/{id}/status")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long id, @RequestParam String status) {
        return taskRepo.findById(id).map(task -> {
            try {
                task.setStatus(Enum.valueOf(com.tms.model.Status.class, status.toUpperCase()));
            	System.out.println("Updating Task ID: " + id + " to status: " + status);
            	return ResponseEntity.ok(taskRepo.save(task));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid status value. Use: PENDING, IN_PROGRESS, COMPLETED");
            }
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found"));
    }


    // Delete task (ADMIN only)
    @DeleteMapping("deleteTask/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        if (taskRepo.existsById(id)) {
            taskRepo.deleteById(id);
            return ResponseEntity.noContent().build(); 
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }
    }
}
