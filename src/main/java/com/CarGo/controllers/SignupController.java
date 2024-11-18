package com.CarGo.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CarGo.dto.SignupRequest;
import com.CarGo.services.AuthService;

@RestController
@RequestMapping("/signup")
@CrossOrigin(origins = "http://localhost:4200")
public class SignupController { 
	
	private final AuthService authService;
	
	@Autowired
	public SignupController(AuthService authService) {
		this.authService=authService;
	}
	
	@PostMapping
	public ResponseEntity<Map<String, String>> signupCustomer(@RequestBody SignupRequest signupRequest) {
	    Map<String, String> response = new HashMap<>();

	    
	    if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
	        response.put("message", "Password and Confirm Password do not match");
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }

	    boolean isUserCreated = authService.createCustomer(signupRequest);
	    if (isUserCreated) {
	        response.put("message", "Customer created successfully");
	        return ResponseEntity.ok(response);
	    } else {
	        response.put("message", "Failed to create customer");
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }
	}



}