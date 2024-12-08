package com.CarGo.services;

import com.CarGo.entities.Trip;
import com.CarGo.Utils.JwtUtils;
import com.CarGo.dto.TripRequest;
import com.CarGo.entities.Booking;
import com.CarGo.entities.Customer;
import com.CarGo.repository.TripRepository;
import com.CarGo.repository.BookingRepository;
import com.CarGo.repository.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    public TripService(TripRepository tripRepository, BookingRepository bookingRepository, CustomerRepository customerRepository) {
        this.tripRepository = tripRepository;
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
    }

    public Trip createTrip(TripRequest tripRequest) {
        
        Customer customer = customerRepository.findById(tripRequest.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + tripRequest.getCustomerId()));

        
        Trip trip = new Trip();
        trip.setDeparture(tripRequest.getDeparture());
        trip.setDestination(tripRequest.getDestination());
        trip.setDate(tripRequest.getDate());
        trip.setTime(tripRequest.getTime());
        trip.setAvailableSeats(tripRequest.getAvailableSeats());
        trip.setPricePerPassenger(tripRequest.getPricePerPassenger());
        trip.setCustomer(customer); 

        
        return tripRepository.save(trip);
    }

    public List<Trip> getUpcomingTrips() {
        return tripRepository.findAllUpcomingTripsSortedByDateAndTime();
    }


    public List<Trip> getTripsByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        return tripRepository.findByCustomer(customer);
    }


    public List<Trip> searchTrips(String departure, String destination, LocalDate date, LocalTime time) {
        return tripRepository.findTrips(departure, destination, date, time);
    }

    public Trip getTripById(Long id) {
        return tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));
    }

    public void deleteTrip(Long id) {
        tripRepository.deleteById(id);
    }

    @Transactional
    public Booking reserveSeat(Long tripId, String passengerName, Long customerId) {
        
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + tripId));

        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

       
        if (trip.getAvailableSeats() <= 0) {
            throw new RuntimeException("No available seats for this trip.");
        }

        
        if (bookingRepository.existsByTripAndCustomer(trip, customer)) {
            throw new RuntimeException("You have already reserved a seat for this trip.");
        }

       
        trip.setAvailableSeats(trip.getAvailableSeats() - 1);
        tripRepository.save(trip);  

        
        Booking booking = new Booking();
        booking.setTrip(trip);
        booking.setCustomer(customer);
        booking.setPassengerName(passengerName);
        return bookingRepository.save(booking);
    }

    @Transactional
    public void cancelReservation(Long tripId, Long customerId) throws Exception {
       
        Optional<Booking> bookingOptional = bookingRepository.findByTripIdAndCustomerId(tripId, customerId);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            bookingRepository.delete(booking);

            
            Trip trip = booking.getTrip();
            trip.setAvailableSeats(trip.getAvailableSeats() + 1);
            tripRepository.save(trip);
        } else {
            throw new Exception("No reservation found for this trip and customer.");
        }
    }
    
    
    
    
    
    public boolean checkReservationExists(Long tripId, Long customerId) {
        Optional<Trip> trip = tripRepository.findById(tripId);
        Optional<Customer> customer = customerRepository.findById(customerId);

        if (trip.isPresent() && customer.isPresent()) {
            return bookingRepository.existsByTripAndCustomer(trip.get(), customer.get());
        }
        return false;
    }
    
    
    
    
    
    
    public List<Map<String, Object>> getReservationsForUserTrips(Long customerId) {
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        List<Trip> userTrips = tripRepository.findByCustomer(customer);
        
       
        List<Booking> bookings = bookingRepository.findByTripIn(userTrips);

       
        List<Map<String, Object>> reservationDetails = bookings.stream().map(booking -> {
            Map<String, Object> details = new HashMap<>();
            details.put("passengerName", booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName());
            details.put("tripDate", booking.getTrip().getDate());
            details.put("bookingId", booking.getId());
            return details;
        }).collect(Collectors.toList());

        return reservationDetails;
    }





    public void updateRequestStatus(Long requestId, String status) {
        Booking booking = bookingRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setEtat(status);
        bookingRepository.save(booking);
    }
    
    
    
    
    @Transactional
    public TripRequest updateTrip(Long tripId, TripRequest tripRequest, String token) {
        System.out.println("Updating trip with ID: " + tripId);

        // Rechercher le trip à mettre à jour
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + tripId));

        // Extraire l'utilisateur connecté depuis le token JWT
        String customerEmail = jwtUtils.extractUsername(token.substring(7));
        Customer customer = customerRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + customerEmail));

        // Vérifier que le client connecté est bien le propriétaire du trip
        if (!trip.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("You are not authorized to update this trip.");
        }

        // Mettre à jour les champs du trip
        trip.setDeparture(tripRequest.getDeparture());
        trip.setDestination(tripRequest.getDestination());
        trip.setDate(tripRequest.getDate());
        trip.setTime(tripRequest.getTime());
        trip.setAvailableSeats(tripRequest.getAvailableSeats());
        trip.setPricePerPassenger(tripRequest.getPricePerPassenger());

        // Sauvegarder les modifications
        Trip updatedTrip = tripRepository.save(trip);

        // Préparer la réponse TripRequest avec customerId
        TripRequest response = new TripRequest();
        response.setId(updatedTrip.getId());
        response.setDeparture(updatedTrip.getDeparture());
        response.setDestination(updatedTrip.getDestination());
        response.setDate(updatedTrip.getDate());
        response.setTime(updatedTrip.getTime());
        response.setAvailableSeats(updatedTrip.getAvailableSeats());
        response.setPricePerPassenger(updatedTrip.getPricePerPassenger());

        // Assurez-vous de bien peupler le customerId
        response.setCustomerId(updatedTrip.getCustomer().getId());

        return response;
    }




    
    
    
    
    
    
    
    
    
    
    
    
    
}