package com.java.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.project.repository.UserRepository;

@RestController
public class LoadController {

	@Autowired
	private UserRepository repo;
	
	@GetMapping("/")
	public String hello() {
		
		return "Hello World !";
	}

	@GetMapping("/employees")
	public String getAllEmployees() {
		System.out.println("Hello World !");
		return repo.findAll().toString();
	}
	
}
