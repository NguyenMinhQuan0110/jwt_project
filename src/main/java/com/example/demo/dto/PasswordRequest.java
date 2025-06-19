package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRequest {
	
	private Long id;
	
	@NotBlank(message = "Password is required")
	private String oldpassword;
	@NotBlank(message = "Password is required")
	private String newpassword;
}
