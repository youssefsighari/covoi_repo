package com.CarGo.services;

import com.CarGo.dto.SignupRequest;
import com.CarGo.entities.Customer;

public interface AuthService {

	boolean createCustomer(SignupRequest signupRequest);

	

}
