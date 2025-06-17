package com.example.demo.exception;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
	private Integer status;
	private String message;
	private List<String> errors;
}
