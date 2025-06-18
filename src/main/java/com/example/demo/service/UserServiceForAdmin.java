package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.example.demo.dto.UserRequestForAdmin;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserResponseForAdmin;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserServiceForAdmin {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	public List<UserResponseForAdmin> getAllUser(){
		return userRepository.findAll().stream().map(u-> new UserResponseForAdmin(u.getId(),u.getName(),u.getEmail(),u.getRole())).collect(Collectors.toList());
	}
	
	@Transactional
	public UserResponseForAdmin createUser (UserRequestForAdmin userRequestForAdmin) {
		if (userRepository.existsByEmail(userRequestForAdmin.getEmail())) {
		        throw new BadRequestException("Email đã tồn tại");
		    }
		User user = new User();
		user.setName(userRequestForAdmin.getName());
		user.setEmail(userRequestForAdmin.getEmail());
		user.setPassword(passwordEncoder.encode(userRequestForAdmin.getPassword()));
		Role roleId = roleRepository.findByName(userRequestForAdmin.getRole()).orElseThrow(() -> new NotFoundException("Role 'user' not found"));
		user.setRole(roleId);
		user= userRepository.save(user);
		return new UserResponseForAdmin(user.getId(),user.getName(),user.getEmail(),user.getRole()); 
	}
	
	public UserResponseForAdmin getUserById(Long id) {
		User user = userRepository.findById(id).orElseThrow(()-> new NotFoundException("User not found"));
		return new UserResponseForAdmin(user.getId(),user.getName(),user.getEmail(),user.getRole());
	}
	
	@Transactional
	public UserResponseForAdmin updateUser(Long id, UserRequestForAdmin userRequestForAdmin) {
		User user = userRepository.findById(id).orElseThrow(()-> new NotFoundException("User not found"));
		user.setName(userRequestForAdmin.getName());
		user.setEmail(userRequestForAdmin.getEmail());
		user.setPassword(passwordEncoder.encode(userRequestForAdmin.getPassword()));
		Role roleId = roleRepository.findByName(userRequestForAdmin.getRole()).orElseThrow(() -> new NotFoundException("Role 'user' not found"));
		user.setRole(roleId);
		user = userRepository.save(user);
		return new UserResponseForAdmin(user.getId(),user.getName(),user.getEmail(),user.getRole());
	}
	@Transactional
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}
}
