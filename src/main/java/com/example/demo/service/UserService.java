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
import com.example.demo.exception.AppException;
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
	
	public List<UserResponseForAdmin> getAllUser(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    boolean isAdmin = auth.getAuthorities().stream()
	        .anyMatch(authority -> authority.getAuthority().equals("ROLE_admin"));

	    if (!isAdmin) {
	        throw new AppException(403,"USER_NOT_PERMISSION");
	    }
		return userRepository.findAll().stream().map(u-> new UserResponseForAdmin(u.getId(),u.getName(),u.getEmail(),u.getRole().getName())).collect(Collectors.toList());
	}
	
	@Transactional
	public UserResponse create (UserRequest userRequest) {
		Role roleId = roleRepository.findByName("user").orElseThrow(() -> new AppException(404,"Role 'user' not found"));

		    if (userRepository.existsByEmail(userRequest.getEmail())) {
		        throw new AppException(400,"Email đã tồn tại");
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
		User user = userRepository.findById(id).orElseThrow(()-> new AppException(404,"User not found"));
		return new UserResponse(user.getId(),user.getName(),user.getEmail());
	}
	@Transactional
	public UserResponse userUpdate( UserRequest userRequest) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User userUpdate = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(404,"User not found"));

		User user = userRepository.findById(userRequest.getId()).orElseThrow(()-> new AppException(404,"User not found"));
		if(user.getId()!=userUpdate.getId()) {
			throw new AppException(403,"USER_NOT_PERMISSION");
		}
		user.setName(userRequest.getName());
		user.setEmail(userRequest.getEmail());
		user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		user = userRepository.save(user);
		return new UserResponse(user.getId(),user.getName(),user.getEmail());
	}
	@Transactional
	public UserResponseForAdmin adminUpdate( UserRequestForAdmin userRequestForAdmin) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    boolean isAdmin = auth.getAuthorities().stream()
	        .anyMatch(authority -> authority.getAuthority().equals("ROLE_admin"));

	    if (!isAdmin) {
	        throw new AppException(403,"USER_NOT_PERMISSION");
	    }
		
		User user = userRepository.findById(userRequestForAdmin.getId()).orElseThrow(()-> new AppException(404,"User not found"));
		user.setName(userRequestForAdmin.getName());
		user.setEmail(userRequestForAdmin.getEmail());
		Role roleId = roleRepository.findByName(userRequestForAdmin.getRole()).orElseThrow(() -> new AppException(404,"Role 'user' not found"));
		user.setRole(roleId);
		user.setPassword(passwordEncoder.encode(userRequestForAdmin.getPassword()));
		
		user = userRepository.save(user);
		return new UserResponseForAdmin(user.getId(),user.getName(),user.getEmail(),user.getRole().getName());
	}
	@Transactional
	public void deleteUser(Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    boolean isAdmin = auth.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_admin"));
        String email = auth.getName();

        User userDelete = userRepository.findByEmail(email).orElseThrow(() -> new AppException(404,"User not found"));

		User user = userRepository.findById(id).orElseThrow(()-> new AppException(404,"User not found"));
		 if(!isAdmin && userDelete.getId()!=user.getId()) {
	        	throw new AppException(403,"USER_NOT_PERMISSION");
	        }
		userRepository.deleteById(id);
	}
}
