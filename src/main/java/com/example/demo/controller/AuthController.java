package com.example.demo.controller;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LogoutResponse;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.exception.AppException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	@Value("${jwt.refreshTokenExpiration}")
	private long REFRESH_TOKEN_EXPIRATION;
	private final UserService userService;
	private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody UserRequest request) {
    	return userService.create(request);
    }

    @PostMapping("/login")
    public AuthResponse login( @RequestBody UserRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(404,"Email not found"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(400,"Wrong password");
        }
        String token = jwtUtil.generateToken(user);
        String refreshToken = UUID.randomUUID().toString();
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION));
        userRepository.save(user);
        return new AuthResponse(token,refreshToken);
    }
    //lấy thông tin user từ token
    @GetMapping("/getuserbytoken")
    public UserResponse getUserByToken(HttpServletRequest request) {
    	 Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	 String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(404,"Email not found"));

        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
    //lấy token mới từ RefreshToken
    @PostMapping("/refresh-token")
    public AuthResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
    	// Tìm user theo refresh token
        User user = userRepository.findAll().stream()
            .filter(u -> refreshTokenRequest.getRefreshToken().equals(u.getRefreshToken()))
            .findFirst()
            .orElseThrow(() -> new AppException(400,"Invalid refresh token"));
        
        // Kiểm tra hạn dùng
        if (user.getRefreshTokenExpiry().before(new Date())) {
            throw new AppException(400,"Refresh token expired");
        }

        // Sinh access token mới
        String newAccessToken = jwtUtil.generateToken(user);

        return new AuthResponse(newAccessToken, user.getRefreshToken());
    }
    
    //logout
    @PostMapping("/logout")
    public LogoutResponse logout(HttpServletRequest request) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(404,"User not found"));

        // Xóa refresh token & expiry
        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);

        userRepository.save(user);

        return new LogoutResponse("Đăng xuất thành công");
    }

}
