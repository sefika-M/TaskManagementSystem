package com.tms.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(nullable = false)
    private String username;
    
    @Column(nullable = false, unique = true)
	private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;
}
