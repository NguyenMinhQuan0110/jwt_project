package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRequest {
	
	private Long id;
	
	@NotBlank(message = "Old Password is required")
	private String oldpassword;
	@NotBlank(message = "New Password is required")
	@Pattern(
	        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
	        message = "Password must be at least 8 characters long and include uppercase, lowercase, number and special character"
	    )
	private String newpassword;
}
