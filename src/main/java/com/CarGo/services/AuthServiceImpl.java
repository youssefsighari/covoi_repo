package com.CarGo.services;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.CarGo.dto.SignupRequest;
import com.CarGo.entities.Customer;
import com.CarGo.repository.CustomerRepository;

@Service
public class AuthServiceImpl implements AuthService {
	
	private final CustomerRepository customerRepository;
	
	private final PasswordEncoder passwordEncoder; 
	
	 
	 
	@Autowired
	 public AuthServiceImpl(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
	        this.customerRepository = customerRepository;
	        this.passwordEncoder = passwordEncoder;
	    }

	@Override
	public boolean createCustomer(SignupRequest signupRequest) {
		
		if(customerRepository.existsByEmail(signupRequest.getEmail())) {
			return false;
		}
			Customer customer = new Customer();
			BeanUtils.copyProperties(signupRequest, customer);
			
			if (signupRequest.getFirstName() != null) {
		        customer.setFirstName(signupRequest.getFirstName());
		    } else {
		        throw new RuntimeException("First name is required");
		    }
			
			
			 
			String hashPassword = passwordEncoder.encode(signupRequest.getPassword());
			customer.setPassword(hashPassword);
			customerRepository.save(customer);
		return true ;
	}
	
	

}
