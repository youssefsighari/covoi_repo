package com.CarGo.services.jwt;

import java.util.Collections; 

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.CarGo.entities.Customer;
import com.CarGo.repository.CustomerRepository;

@Service
public class CustomerServiceImpl implements UserDetailsService {
	
	private final CustomerRepository customerRepository;

	public CustomerServiceImpl(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	    System.out.println("Attempting to load user by email: " + email);
	    Customer customer = customerRepository.findByEmail(email)
	            .orElseThrow(() -> {
	                System.out.println("Customer not found with email: " + email);
	                return new UsernameNotFoundException("Customer not found with email: " + email);
	            });
	    System.out.println("Customer found: " + customer.getEmail());
	    return new User(customer.getEmail(), customer.getPassword(), Collections.emptyList());
	}

	public Customer getCustomerByEmail(String email) {
	    return customerRepository.findByEmail(email)
	            .orElseThrow(() -> new UsernameNotFoundException("Customer not found with email: " + email));
	}

	


}
 