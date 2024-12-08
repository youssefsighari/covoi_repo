package com.CarGo.dto;

import java.math.BigDecimal; 
import java.time.LocalDate;
import java.time.LocalTime;

public class TripRequest {

	private Long id; 
    private String departure;
    private String destination;
    private LocalDate date;
    private LocalTime time;
    private int availableSeats;
    private BigDecimal pricePerPassenger;
    private Long customerId;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public BigDecimal getPricePerPassenger() {
        return pricePerPassenger;
    }

    public void setPricePerPassenger(BigDecimal pricePerPassenger) {
        this.pricePerPassenger = pricePerPassenger;
    }
    
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
