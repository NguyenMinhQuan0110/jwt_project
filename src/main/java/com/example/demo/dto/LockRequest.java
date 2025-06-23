package com.example.demo.dto;

import lombok.Data;

@Data
public class LockRequest {
	private Long id;
	private String reason;
}
