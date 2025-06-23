package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.History;

public interface HistoryRepository extends JpaRepository<History, Long>{
	
}
