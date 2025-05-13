package com.appsdevblog.photoapp.api.users.ui.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appsdevblog.photoapp.api.users.ui.model.CreateUserRequestModel;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UsersController {

	@Autowired
	private Environment env;

	@GetMapping("/status/check")
	public String status() {
		String s = "Working: ";

		return s + env.getProperty("local.server.port");

	}
	
	@PostMapping 
	public String createUser() {
		return "Create user method is called"; 
	}

	@PostMapping
	public String createUser (@Valid @RequestBody CreateUserRequestModel userDetails) {

		return "Create user method is called";
		
	}


}
