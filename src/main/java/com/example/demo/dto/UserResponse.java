package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//lớp này để trả data về client
public class UserResponse {
	private Long id;
    private String name;
    private String email;
}
