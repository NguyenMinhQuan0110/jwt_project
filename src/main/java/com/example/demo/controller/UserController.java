package com.example.demo.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<UserResponseForAdmin>> getAllUser() {
        List<UserResponseForAdmin> users = userService.getAllUser();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponse> userUpdate(@Valid @RequestBody UserRequest userRequest) {
        UserResponse updatedUser = userService.userUpdate(userRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/updaterole")
    public ResponseEntity<UserResponseForAdmin> adminUpdate(@Valid @RequestBody UserRequestForAdmin request) {
        UserResponseForAdmin updatedUser = userService.adminUpdate(request);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/setpassword")
    public ResponseEntity<Map<String, Object>> setPassword(@Valid @RequestBody PasswordRequest passwordRequest) {
        Map<String, Object> result = userService.setPasswordForUser(passwordRequest);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
    }
}
