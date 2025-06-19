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

import com.example.demo.dto.PasswordRequest;
import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserRequestForAdmin;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserResponseForAdmin;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping
	public List<UserResponseForAdmin> getAllUser(){
		return userService.getAllUser();
	}
	
	@GetMapping("/{id}")
	public UserResponse getUserById(@PathVariable Long id) {
		return userService.getUserById(id);
	}
	
	@PutMapping("/update")
	public UserResponse adminUpdate(@Valid @RequestBody UserRequest userRequest) {
		return userService.userUpdate(userRequest);
	}
	
	@PutMapping("/updaterole")
	public UserResponseForAdmin adminUpdate(@Valid @RequestBody UserRequestForAdmin request) {
		return userService.adminUpdate(request);
	}
	@PutMapping("/setpassword")
	public String setPassword(@RequestBody PasswordRequest passwordRequest) {
		return userService.setPasswordForUser(passwordRequest);
	}
	
	@DeleteMapping("/delete/{id}")
	public String deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return "User deleted successfully";
	}
}
