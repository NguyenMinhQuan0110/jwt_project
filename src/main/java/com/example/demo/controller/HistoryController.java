package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.HistoryResponse;

import com.example.demo.service.HistoryService;

@RestController
@RequestMapping("/api/history")
public class HistoryController {
	
	@Autowired
	private HistoryService historyService;
	
	@GetMapping
    public ResponseEntity<List<HistoryResponse>> getAllUser() {
        List<HistoryResponse> his = historyService.getAllHistory();
        return ResponseEntity.ok(his);
    }
}
