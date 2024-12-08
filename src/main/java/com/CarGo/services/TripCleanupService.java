package com.CarGo.services;

import com.CarGo.repository.TripRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Service
public class TripCleanupService {

    @Autowired
    private TripRepository tripRepository;

    @Scheduled(fixedRate = 60000) 
    @Transactional 
    public void deleteOldTrips() {
        System.out.println("Scheduled task started at: " + LocalDateTime.now());
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        int deletedTripsCount = tripRepository.deleteOldTrips(currentDate, currentTime);
        System.out.println("Number of old trips deleted: " + deletedTripsCount);
    }
}
