package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.HistoryResponse;
import com.example.demo.exception.AppException;
import com.example.demo.repository.HistoryRepository;

@Service
public class HistoryService {
	
	@Autowired
	private HistoryRepository  historyRepository;
	
	
	public List<HistoryResponse> getAllHistory(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    boolean isAdmin = auth.getAuthorities().stream()
	        .anyMatch(authority -> authority.getAuthority().equals("ROLE_admin"));

	    if (!isAdmin) {
	        throw new AppException(403,"User not permission");
	    }
		return historyRepository.findAll().stream().map(h-> new HistoryResponse(h.getId(),h.getImplementer(),h.getAction(),h.getDateimp())).collect(Collectors.toList());
	}
}
