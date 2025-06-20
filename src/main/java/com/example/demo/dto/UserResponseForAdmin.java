package com.example.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseForAdmin {
	private Long id;
    private String name;
    private String email;
    private String role;
    private Boolean block;
    private String reason;
}
