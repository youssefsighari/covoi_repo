package com.CarGo.repository;

import com.CarGo.entities.Booking;
import com.CarGo.entities.Customer;
import com.CarGo.entities.Trip;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
	 boolean existsByTripAndCustomer(Trip trip, Customer customer);

	Optional<Booking> findByTripIdAndCustomerId(Long tripId, Long customerId);

	List<Booking> findByTripIn(List<Trip> userTrips);
}
