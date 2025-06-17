package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.service.UserService;


@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
//	@PostMapping
//	public UserResponse createUser(@Valid @RequestBody UserRequest userRequest) {
//		return userService.createUser(userRequest);
//	}
//	
	@GetMapping
	public List<UserResponse> getAllUser(){
		return userService.getAllUser();
	}
	
	@GetMapping("/{id}")
	public UserResponse getUserById(@PathVariable Long id) {
		return userService.getUserById(id);
	}
	
	@PutMapping("/{id}")
	public UserResponse updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
		return userService.updateUser(id, userRequest);
	}
	
	@DeleteMapping("/{id}")
	public String deletUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return "User deleted successfully";
	}
}
