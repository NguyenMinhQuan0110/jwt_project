package com.example.demo.service;



import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.dto.UserRequest;
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
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	public List<UserResponse> getAllUser(){
		return userRepository.findAll().stream().map(u-> new UserResponse(u.getId(),u.getName(),u.getEmail())).collect(Collectors.toList());
	}
	
	@Transactional
	public UserResponse create (UserRequest userRequest) {
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
	
	public UserResponse getUserById(Long id) {
		User user = userRepository.findById(id).orElseThrow(()-> new NotFoundException("User not found"));
		return new UserResponse(user.getId(),user.getName(),user.getEmail());
	}
	@Transactional
	public UserResponse update(Long id, UserRequest userRequest) {
		User user = userRepository.findById(id).orElseThrow(()-> new NotFoundException("User not found"));
		user.setName(userRequest.getName());
		user.setEmail(userRequest.getEmail());
		user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		user = userRepository.save(user);
		return new UserResponse(user.getId(),user.getName(),user.getEmail());
	}
	public UserResponseForAdmin updateRole(Long id, UserRequestForAdmin userRequestForAdmin) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    boolean isAdmin = auth.getAuthorities().stream()
	        .anyMatch(authority -> authority.getAuthority().equals("ROLE_admin"));

	    if (!isAdmin) {
	        throw new BadRequestException("USER_NOT_PERMISSION");
	    }
		
		User user = userRepository.findById(id).orElseThrow(()-> new NotFoundException("User not found"));
		user.setName(userRequestForAdmin.getName());
		user.setEmail(userRequestForAdmin.getEmail());
		Role roleId = roleRepository.findByName(userRequestForAdmin.getRole()).orElseThrow(() -> new NotFoundException("Role 'user' not found"));
		user.setRole(roleId);
		user.setPassword(passwordEncoder.encode(userRequestForAdmin.getPassword()));
		
		user = userRepository.save(user);
		return new UserResponseForAdmin(user.getId(),user.getName(),user.getEmail(),user.getRole());
	}
	@Transactional
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}
}
