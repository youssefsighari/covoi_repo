package com.CarGo.dto;

public class LoginResponse {
    private String jwt;
    private Long customerId; 
    private String firstName;

    public LoginResponse(String jwt, Long customerId, String firstName) {
        this.jwt = jwt;
        this.customerId = customerId;
        this.firstName = firstName;
    }

   
    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
