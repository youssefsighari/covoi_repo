package com.CarGo.repository;

import com.CarGo.entities.Customer; 
import com.CarGo.entities.Trip; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;



@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
	
	@Query("SELECT t FROM Trip t ORDER BY t.date ASC, t.time ASC")
	List<Trip> findAllUpcomingTripsSortedByDateAndTime();


	
	 List<Trip> findByCustomer(Customer customer);

    
    @Modifying
    @Transactional
    @Query("DELETE FROM Trip t WHERE (t.date < :currentDate OR (t.date = :currentDate AND t.time < :currentTime))")
    int deleteOldTrips(LocalDate currentDate, LocalTime currentTime);
    
    @Query("SELECT t FROM Trip t WHERE " +
            "(:departure IS NULL OR t.departure = :departure) AND " +
            "(:destination IS NULL OR t.destination = :destination) AND " +
            "(:date IS NULL OR t.date = :date) AND " +
            "(:time IS NULL OR t.time = :time)")
    List<Trip> findTrips(@Param("departure") String departure,
                         @Param("destination") String destination,
                         @Param("date") LocalDate date,
                         @Param("time") LocalTime time);
    
    
    
}



