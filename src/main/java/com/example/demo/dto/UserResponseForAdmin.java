package com.example.demo.dto;


import java.util.Date;

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
    private String createdBy;
    private Date createdTime;
	private String updateBy;
	private Date updateTime;
}
