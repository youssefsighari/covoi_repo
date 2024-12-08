package com.CarGo.controllers;
 import com.CarGo.dto.UpdateRequestStatusRequest;

import com.CarGo.entities.Trip; 
import com.CarGo.dto.TripRequest;
import com.CarGo.entities.Booking;
import com.CarGo.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trips")
@PreAuthorize("isAuthenticated()") 
@CrossOrigin(origins = "http://localhost:4200")
public class TripController {

    private final TripService tripService;

    @Autowired
    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping("/create")
    public ResponseEntity<Trip> createTrip(@RequestBody TripRequest tripRequest) {
        Trip createdTrip = tripService.createTrip(tripRequest);
        return ResponseEntity.ok(createdTrip);
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Trip>> getTripsByCustomer(@PathVariable Long customerId) {
        List<Trip> trips = tripService.getTripsByCustomer(customerId);
        return ResponseEntity.ok(trips);
    }  

    @GetMapping("/allTrips")
    public ResponseEntity<List<Trip>> getUpcomingTrips() {
        List<Trip> trips = tripService.getUpcomingTrips();
        return ResponseEntity.ok(trips);
    }

    
    @GetMapping("/search")
    public ResponseEntity<List<Trip>> searchTrips(
            @RequestParam(required = false) String departure,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) LocalTime time) {
        List<Trip> trips = tripService.searchTrips(departure, destination, date, time);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable Long id) {
        Trip trip = tripService.getTripById(id);
        return ResponseEntity.ok(trip);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        tripService.deleteTrip(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/reserve")
    public ResponseEntity<String> reserveTrip(@RequestBody Map<String, Object> requestData) {
        try {
            if (!requestData.containsKey("tripId") || !requestData.containsKey("passengerName") || !requestData.containsKey("customerId")) {
                System.out.println("Missing required parameters in request data: " + requestData);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required parameters.");
            }

            Long tripId = Long.valueOf(requestData.get("tripId").toString());
            String passengerName = requestData.get("passengerName").toString();
            Long customerId = Long.valueOf(requestData.get("customerId").toString());

            System.out.println("Received tripId: " + tripId + ", passengerName: " + passengerName + ", customerId: " + customerId);

            tripService.reserveSeat(tripId, passengerName, customerId);
            return ResponseEntity.ok("Reservation successful");
        } catch (NullPointerException e) {
            System.err.println("One or more required fields are missing.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("One or more required fields are missing.");
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
    
    
    @PostMapping("/cancelReservation")
    public ResponseEntity<String> cancelReservation(@RequestBody Map<String, Object> requestData) {
        try {
            if (!requestData.containsKey("tripId") || !requestData.containsKey("customerId")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required parameters.");
            }

            Long tripId = Long.valueOf(requestData.get("tripId").toString());
            Long customerId = Long.valueOf(requestData.get("customerId").toString());

            tripService.cancelReservation(tripId, customerId);
            return ResponseEntity.ok("Reservation cancelled successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }


    @GetMapping("/checkReservation")
    public ResponseEntity<Boolean> checkReservationExists(@RequestParam Long tripId, @RequestParam Long customerId) {
        boolean exists = tripService.checkReservationExists(tripId, customerId);
        return ResponseEntity.ok(exists);
    } 
    
    
    
    
    
    @GetMapping("/reservationsForUserTrips/{customerId}")
    public ResponseEntity<List<Map<String, Object>>> getReservationsForUserTrips(@PathVariable Long customerId) {
        List<Map<String, Object>> bookings = tripService.getReservationsForUserTrips(customerId);
        return ResponseEntity.ok(bookings);
    }




    @PutMapping("/updateStatus")
    public ResponseEntity<Void> updateRequestStatus(@RequestBody UpdateRequestStatusRequest request) {
        tripService.updateRequestStatus(request.getRequestId(), request.getStatus());
        return ResponseEntity.ok().build();
    }
    
    
    
    @PutMapping("/update/{id}")
    public ResponseEntity<TripRequest> updateTrip(
            @PathVariable Long id,
            @RequestBody TripRequest tripRequest,
            @RequestHeader("Authorization") String token) {
        TripRequest response = tripService.updateTrip(id, tripRequest, token);
        return ResponseEntity.ok(response);
    }

     
    
    
    
    
    
    
    
    
    
    
    
    
    
    


}