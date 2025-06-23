package com.example.demo.service;



import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ForgetPasswordRequest;
import com.example.demo.dto.LockRequest;
import com.example.demo.dto.PasswordRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserRequestForAdmin;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserResponseForAdmin;
import com.example.demo.exception.AppException;
import com.example.demo.model.History;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.HistoryRepository;
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
	private HistoryRepository  historyRepository;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailService emailService;
	
	public List<UserResponseForAdmin> getAllUser(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    boolean isAdmin = auth.getAuthorities().stream()
	        .anyMatch(authority -> authority.getAuthority().equals("ROLE_admin"));

	    if (!isAdmin) {
	        throw new AppException(403,"User ");
	    }
		return userRepository.findAll().stream().map(u-> new UserResponseForAdmin(u.getId(),u.getName(),u.getEmail(),u.getRole().getName(), u.getBlock(),u.getReason(),u.getCreatedBy(),u.getCreatedTime(),u.getUpdateBy(),u.getUpdateTime())).collect(Collectors.toList());
	}
	
	@Transactional
	public Map<String, Object> create (RegisterRequest userRequest) {
		Role roleId = roleRepository.findByName("user").orElseThrow(() -> new AppException(404,"Role 'user' not found"));

		    if (userRepository.existsByEmail(userRequest.getEmail())) {
		        throw new AppException(400,"Email already exists");
		    }
		User user = new User();
		user.setName(userRequest.getName());
		user.setEmail(userRequest.getEmail());
		String randomPassword = UUID.randomUUID().toString().substring(0, 8);
		user.setPassword(passwordEncoder.encode(randomPassword));
		user.setRole(roleId);
		user.setBlock(false);
		user.setLoginfail(0);
		user.setCreatedBy(userRequest.getName());
		user.setCreatedTime(new Date(System.currentTimeMillis()));
		user.setUpdateBy(null);
		user.setUpdateTime(null);
		user= userRepository.save(user);
		// Gửi email cho user
	    String subject = "JWT";
	    String text = "Hello " + user.getName() + ",\n\n"
	                + "Your password is: " + randomPassword + "\n\n"
	                + "Please log in and change it as soon as possible.";

	    emailService.sendSimpleMessage(user.getEmail(), subject, text);
	    //Lưu lịch sử
	    History history = new History();
	    history.setImplementer(userRequest.getName());
	    history.setAction( "Registered account");
	    history.setDateimp(new Date(System.currentTimeMillis()));
	    history= historyRepository.save(history);
	    Map<String, Object> response = new HashMap<>();
		response.put("status", 200);
		response.put("message", "Account registration  successfully. Please check your email!");
		return response; 
	}
	
	public UserResponse getUserById(Long id) {
		User user = userRepository.findById(id).orElseThrow(()-> new AppException(404,"User not found"));
		return new UserResponse(user.getId(),user.getName(),user.getEmail());
	}
	@Transactional
	public UserResponse userUpdate( UserRequestForAdmin userRequest) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User userUpdate = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(404,"User not found"));
        boolean isAdmin = auth.getAuthorities().stream()
    	        .anyMatch(authority -> authority.getAuthority().equals("ROLE_admin"));

		User user = userRepository.findById(userRequest.getId()).orElseThrow(()-> new AppException(404,"User not found"));
		if(!isAdmin &&user.getId()!=userUpdate.getId()) {
			throw new AppException(403,"User not permission");
		}
		user.setName(userRequest.getName());
		user.setEmail(userRequest.getEmail());
		user.setUpdateBy(userRequest.getName());
		user.setUpdateTime(new Date(System.currentTimeMillis()));
		user = userRepository.save(user);
		//Lưu lịch sử
		History history = new History();
	    history.setImplementer(userRequest.getName());
	    history.setAction( "Updated account");
	    history.setDateimp(new Date(System.currentTimeMillis()));
	    history= historyRepository.save(history);
		return new UserResponse(user.getId(),user.getName(),user.getEmail());
	}
	@Transactional
	public UserResponseForAdmin adminUpdate( UserRequestForAdmin userRequestForAdmin) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    
	    boolean isAdmin = auth.getAuthorities().stream()
	        .anyMatch(authority -> authority.getAuthority().equals("ROLE_admin"));

	    if (!isAdmin) {
	        throw new AppException(403,"User not permission");
	    }
	    String email = auth.getName();

        User useradmin = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(404,"User not found"));
		
		User user = userRepository.findById(userRequestForAdmin.getId()).orElseThrow(()-> new AppException(404,"User not found"));
		user.setName(userRequestForAdmin.getName());
		user.setEmail(userRequestForAdmin.getEmail());
		Role roleId = roleRepository.findByName(userRequestForAdmin.getRole()).orElseThrow(() -> new AppException(404,"Role 'user' not found"));
		user.setRole(roleId);
		user.setPassword(passwordEncoder.encode(userRequestForAdmin.getPassword()));
		user.setUpdateBy(useradmin.getName());
		user.setUpdateTime(new Date(System.currentTimeMillis()));
		user = userRepository.save(user);
		//Lưu lịch sử
		History history = new History();
		history.setImplementer(useradmin.getName());
		history.setAction(useradmin.getName()+ " updated account " + userRequestForAdmin.getName());
		history.setDateimp(new Date(System.currentTimeMillis()));
		history= historyRepository.save(history);
		return new UserResponseForAdmin(user.getId(),user.getName(),user.getEmail(),user.getRole().getName(),user.getBlock(),user.getReason(),user.getCreatedBy(),user.getCreatedTime(),user.getUpdateBy(),user.getUpdateTime());
	}
	@Transactional
	public Map<String, Object> setPasswordForUser(PasswordRequest passwordRequest) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    boolean isAdmin = auth.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_admin"));
        String email = auth.getName();

        User userSetPassWord = userRepository.findByEmail(email).orElseThrow(() -> new AppException(404,"User not found"));

		User user = userRepository.findById(passwordRequest.getId()).orElseThrow(()-> new AppException(404,"User not found"));
		 if(!isAdmin && userSetPassWord.getId()!=user.getId()) {
	        	throw new AppException(403,"User not permission");
	        }
		 if (!passwordEncoder.matches(passwordRequest.getOldpassword(), user.getPassword())) {
	            throw new AppException(400,"Wrong password");
	        }
		 if(passwordRequest.getOldpassword().equals(passwordRequest.getNewpassword())) {
			 throw new AppException(400,"New password must be different from old password");
		 }
		 user.setPassword(passwordEncoder.encode(passwordRequest.getNewpassword()));
		 user.setRefreshToken(null);
	     user.setRefreshTokenExpiry(null);
	     user.setUpdateBy(userSetPassWord.getName());
	     user.setUpdateTime(new Date(System.currentTimeMillis()));
		 user=userRepository.save(user);
		 //Lưu lịch sử
		 History history = new History();
		 history.setImplementer(userSetPassWord.getName());
		 history.setAction(userSetPassWord.getName()+ " seted pasword account " + user.getName());
		 history.setDateimp(new Date(System.currentTimeMillis()));
		 history= historyRepository.save(history);
		 Map<String, Object> response = new HashMap<>();
		 response.put("status", 200);
		 response.put("message", "Password changed successfully");
		 return response;
	}
	@Transactional
	public Map<String, Object> forgetPassword(ForgetPasswordRequest request){
		User userForgetPassword = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new AppException(404,"User not found"));
		Date now = new Date();
        if (userForgetPassword.getBlock()==true && userForgetPassword.getBlockExpiry() != null && userForgetPassword.getBlockExpiry().after(now)) {
            throw new AppException(400, "Your account is blocked until: " + userForgetPassword.getBlockExpiry());
        }
		String randomPassword = UUID.randomUUID().toString().substring(0, 8);
		userForgetPassword.setPassword(passwordEncoder.encode(randomPassword));
		userForgetPassword.setLoginfail(0);
		userForgetPassword.setUpdateBy("system");
		userForgetPassword.setUpdateTime(new Date(System.currentTimeMillis()));
		userForgetPassword= userRepository.save(userForgetPassword);
		// Gửi email cho user
	    String subject = "JWT";
	    String text = "Hello " + userForgetPassword.getName() + ",\n\n"
	                + "Your new password is: " + randomPassword + "\n\n"
	                + "Please log in and change it as soon as possible.";

	    emailService.sendSimpleMessage(userForgetPassword.getEmail(), subject, text);
		 //Lưu lịch sử
		 History history = new History();
		 history.setImplementer("system");
		 history.setAction("system seted pasword account " + userForgetPassword.getName());
		 history.setDateimp(new Date(System.currentTimeMillis()));
		 history= historyRepository.save(history);
		Map<String, Object> response = new HashMap<>();
		response.put("status", 200);
		response.put("message", "Password changed successfully. Please check your email!");
		return response;
	}
	@Transactional
	public Map<String, Object> lockUser(LockRequest lockRequest){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    boolean isAdmin = auth.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_admin"));
        String email = auth.getName();

        User userLock = userRepository.findByEmail(email).orElseThrow(() -> new AppException(404,"User not found"));

		User user = userRepository.findById(lockRequest.getId()).orElseThrow(()-> new AppException(404,"User not found"));
		if(!isAdmin || userLock.getId()==user.getId()) {
			throw new AppException(403,"User not permission");
		}
		user.setBlock(true);
		user.setReason(lockRequest.getReason());
		user.setUpdateBy(userLock.getName());
		user.setUpdateTime(new Date(System.currentTimeMillis()));
		user= userRepository.save(user);
		//Lưu lịch sử
		 History history = new History();
		 history.setImplementer(userLock.getName());
		 history.setAction(userLock.getName()+" locked account " + user.getName());
		 history.setDateimp(new Date(System.currentTimeMillis()));
		 history= historyRepository.save(history);
		Map<String, Object> response = new HashMap<>();
		response.put("status", 200);
		response.put("message", "Locked account "+ user.getName() +" successfully.");
		return response;
	}
	@Transactional
	public Map<String, Object> unLockUser(Long id){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    boolean isAdmin = auth.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_admin"));
        String email = auth.getName();

        User userUnLock = userRepository.findByEmail(email).orElseThrow(() -> new AppException(404,"User not found"));

		User user = userRepository.findById(id).orElseThrow(()-> new AppException(404,"User not found"));
		if(!isAdmin || userUnLock.getId()==user.getId()) {
			throw new AppException(403,"User not permission");
		}
		user.setBlock(false);
		user.setReason(null);
		user.setUpdateBy(userUnLock.getName());
		user.setUpdateTime(new Date(System.currentTimeMillis()));
		user= userRepository.save(user);
		//Lưu lịch sử
		 History history = new History();
		 history.setImplementer(userUnLock.getName());
		 history.setAction(userUnLock.getName()+" unlocked account " + user.getName());
		 history.setDateimp(new Date(System.currentTimeMillis()));
		 history= historyRepository.save(history);
		Map<String, Object> response = new HashMap<>();
		response.put("status", 200);
		response.put("message", "Unlocked account "+ user.getName() +" successfully.");
		return response;
	}
	@Transactional
	public void deleteUser(Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    boolean isAdmin = auth.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_admin"));
        String email = auth.getName();

        User userDelete = userRepository.findByEmail(email).orElseThrow(() -> new AppException(404,"User not found"));

		User user = userRepository.findById(id).orElseThrow(()-> new AppException(404,"User not found"));
		if(isAdmin) {
			if(userDelete.getId().equals(user.getId())) {
				throw new AppException(403,"You can not delete yourself");
			}
			userRepository.deleteById(id);
			//Lưu lịch sử
			 History history = new History();
			 history.setImplementer(userDelete.getName());
			 history.setAction(userDelete.getName()+" deleted account " + user.getName());
			 history.setDateimp(new Date(System.currentTimeMillis()));
			 history= historyRepository.save(history);
		}else {
			throw new AppException(403,"User not permission");
		}
	}
}
