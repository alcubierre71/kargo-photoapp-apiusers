package com.appsdevblog.photoapp.api.users.ui.controllers;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appsdevblog.photoapp.api.users.service.UsersService;
import com.appsdevblog.photoapp.api.users.shared.UserDto;
import com.appsdevblog.photoapp.api.users.ui.model.CreateUserRequestModel;
import com.appsdevblog.photoapp.api.users.ui.model.CreateUserResponseModel;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UsersController {

	@Autowired
	private Environment env;

	@Autowired
	UsersService usersService;
	
	@GetMapping("/status/check")
	public String status() {
		String s = "Working: ";

		return s + env.getProperty("local.server.port");

	}
	
	@PostMapping 
	public ResponseEntity<CreateUserResponseModel> createUser(@Valid @RequestBody CreateUserRequestModel userDetails) {
		
	    ModelMapper modelMapper = new ModelMapper();
	        
	    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
	    
	    UserDto userDto = modelMapper.map(userDetails, UserDto.class);
	    
		UserDto createdUser = usersService.createUser(userDto);
		
		// Creamos el objeto de respuesta de la API (va sin las contraseñas)
		CreateUserResponseModel curm = new CreateUserResponseModel();
		
		curm = modelMapper.map(createdUser, CreateUserResponseModel.class);
		
		// CREATED --> status 201 
		ResponseEntity<CreateUserResponseModel> re = ResponseEntity.status(HttpStatus.CREATED).body(curm);
		
		return re; 
		
	}

}
