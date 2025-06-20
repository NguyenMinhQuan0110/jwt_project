package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
//lớp này để nhận data từ client
public class UserRequest {
	private Long id;	
	
	 @NotBlank(message = "Name is required")
	 @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
	 private String name;

	 @NotBlank(message = "Email is required")
	 @Email(message = "Email must be valid")
	 private String email;
	 
	 @NotBlank(message = "Password is required")
	 @Pattern(
		        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
		        message = "Password must be at least 8 characters long and include uppercase, lowercase, number and special character"
		    )
	 private String password;
}
