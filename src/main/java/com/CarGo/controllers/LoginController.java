package com.CarGo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.CarGo.Utils.JwtUtils;
import com.CarGo.dto.LoginRequest;
import com.CarGo.dto.LoginResponse;
import com.CarGo.entities.Customer;
import com.CarGo.services.jwt.CustomerServiceImpl;


@RestController
@RequestMapping("/Login")
@CrossOrigin(origins = "http://localhost:4200")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final CustomerServiceImpl customerService;
    private final JwtUtils jwtUtils;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, CustomerServiceImpl customerService, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.customerService = customerService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("Login request received for email: " + loginRequest.getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            System.out.println("Authentication successful for email: " + loginRequest.getEmail());

        } catch (AuthenticationException e) {
            System.out.println("Authentication failed for email: " + loginRequest.getEmail() + " - " + e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails;
        try {
            userDetails = customerService.loadUserByUsername(loginRequest.getEmail());
            System.out.println("UserDetails loaded successfully for email: " + loginRequest.getEmail());

        } catch (UsernameNotFoundException e) {
            System.out.println("User not found for email: " + loginRequest.getEmail() + " - " + e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String jwt = jwtUtils.generateToken(userDetails.getUsername());
        System.out.println("JWT generated successfully for email: " + loginRequest.getEmail());
        Customer customer = customerService.getCustomerByEmail(loginRequest.getEmail());
        Long customerId = customer.getId();
        String firstName = customer.getFirstName();

        return ResponseEntity.ok(new LoginResponse(jwt, customerId, firstName)); 
    } 
}
