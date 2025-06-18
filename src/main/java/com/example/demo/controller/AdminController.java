package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserRequestForAdmin;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserResponseForAdmin;

import com.example.demo.service.UserServiceForAdmin;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
	
	@Autowired
	private UserServiceForAdmin userServiceForAdmin;
	
	@GetMapping
	public ResponseEntity<?> helloAdmin() {
		
	    return ResponseEntity.ok(Map.of("message","Heloo admin"));
	}
	
	@GetMapping("/listuser")
	public List<UserResponseForAdmin> getAllUser(){
		return userServiceForAdmin.getAllUser();
	}
	
	@PostMapping("/createuser")
    public UserResponseForAdmin creteUser(@Valid @RequestBody UserRequestForAdmin request) {
    	return userServiceForAdmin.createUser(request);
    } 
	@GetMapping("/{id}")
	public UserResponseForAdmin getUserById(@PathVariable Long id) {
		return userServiceForAdmin.getUserById(id);
	}
	@PutMapping("/updateuser/{id}")
	public UserResponseForAdmin updateUser(@PathVariable Long id,@Valid @RequestBody UserRequestForAdmin request) {
		return userServiceForAdmin.updateUser(id,request);
	}
	@DeleteMapping("/delete/{id}")
	public String deleteUser(@PathVariable Long id) {
		userServiceForAdmin.deleteUser(id);
		return "User deleted successfully";
	}
}
