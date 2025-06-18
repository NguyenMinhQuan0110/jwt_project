package com.example.demo.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter{
	 	@Autowired
	    private JwtUtil jwtUtil;

	    @Autowired
	    private UserRepository userRepository;

	    @Override
	    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
	                                    FilterChain filterChain)
	                                    throws ServletException, IOException {

	        String authHeader = request.getHeader("Authorization");
	        String token = null;
	        String email = null;
	        String role= null;

	        if (authHeader != null && authHeader.startsWith("Bearer ")) {
	            token = authHeader.substring(7);
	            email = jwtUtil.extractEmail(token);
	            role =jwtUtil.extractRole(token);
	        }

	        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	     
	            if (jwtUtil.isTokenValid(token)) {
	            	List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
	                UsernamePasswordAuthenticationToken authToken = 
	                    new UsernamePasswordAuthenticationToken(
	                        email, null, authorities
	                    );
	                authToken.setDetails(
	                    new WebAuthenticationDetailsSource().buildDetails(request)
	                );
	                SecurityContextHolder.getContext().setAuthentication(authToken);
	            }
	        }

	        filterChain.doFilter(request, response);
	    }
}
