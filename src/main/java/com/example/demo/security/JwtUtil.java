package com.example.demo.security;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	private final String SECRET_KEY = "my-super-secret-key-which-is-very-strong-123456"; // đổi key dài tí
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 1 ngày(đơn vị là 1/1000 giây) 

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Tạo token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Lấy username từ token
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Kiểm tra token hợp lệ
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
//phương thức này sai
//    public boolean validateToken(String token, String email) {
//        final String username = extractUsername(token);
//        return (username.equals(email) && !isTokenValid(token));
//    }
    public boolean validateToken(String token, String email) {
        final String username = extractUsername(token);
        return (username.equals(email) && isTokenValid(token));
    }
    
}
