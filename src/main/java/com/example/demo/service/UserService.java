package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

import jakarta.transaction.Transactional;


@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Transactional
	public UserResponse createUser (UserRequest userRequest) {
		Role roleId = roleRepository.findByName("user").orElseThrow(() -> new NotFoundException("Role 'user' not found"));

		    if (userRepository.existsByEmail(userRequest.getEmail())) {
		        throw new BadRequestException("Email đã tồn tại");
		    }
		User user = new User();
		user.setName(userRequest.getName());
		user.setEmail(userRequest.getEmail());
		user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		user.setRole(roleId);
		user= userRepository.save(user);
		return new UserResponse(user.getId(),user.getName(),user.getEmail()); 
	}
	
	public List<UserResponse> getAllUser(){
		return userRepository.findAll().stream().map(u-> new UserResponse(u.getId(),u.getName(),u.getEmail())).collect(Collectors.toList());
	}
	
	public UserResponse getUserById(Long id) {
		User user = userRepository.findById(id).orElseThrow(()-> new NotFoundException("User not found"));
		return new UserResponse(user.getId(),user.getName(),user.getEmail());
	}
	@Transactional
	public UserResponse updateUser(Long id, UserRequest userRequest) {
		User user = userRepository.findById(id).orElseThrow(()-> new NotFoundException("User not found"));
		user.setName(userRequest.getName());
		user.setEmail(userRequest.getEmail());
		user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		user = userRepository.save(user);
		return new UserResponse(user.getId(),user.getName(),user.getEmail());
	}
	@Transactional
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}
}
