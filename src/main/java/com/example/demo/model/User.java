package com.example.demo.model;


import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;

	 private String name;

	 private String email;
	 
	 private String password;
	 
	 @ManyToOne
	 @JoinColumn(name="role_id")
	 private Role role;
	 
	 private String refreshToken;
	 
	 private Date refreshTokenExpiry;
	 
	 private Boolean block;
	 
	 private String reason;
	 
	 private Integer loginfail;
	 
}
