package com.example.demo.dto;

import com.example.demo.model.Role;

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
    private Role role;
}
