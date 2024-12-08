package com.CarGo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.CarGo.dto.CustomerDTO;
import com.CarGo.entities.Customer;
import com.CarGo.repository.CustomerRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileStorageService {
	
	 private CustomerRepository customerRepository;
	 
	 @Autowired
	    public FileStorageService(CustomerRepository customerRepository) {
	        this.customerRepository = customerRepository;
	    }

    private final String uploadDir = "uploads/";

    public void storeFile(MultipartFile file, Long customerId) {
        try {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

            // Lire le contenu du fichier en tant que tableau de bytes
            byte[] imageData = file.getBytes();
            customer.setProfilePhoto(imageData);

            customerRepository.save(customer);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store the file in the database. Error: " + ex.getMessage());
        }
    }


    public byte[] getProfilePhoto(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

        if (customer.getProfilePhoto() == null) {
            throw new RuntimeException("No profile photo found for customer with ID: " + customerId);
        }

        return customer.getProfilePhoto();
    }




    
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
        
        // Mapper explicitement les données nécessaires dans le DTO
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFirstName(customer.getFirstName());
        customerDTO.setLastName(customer.getLastName());
        customerDTO.setEmail(customer.getEmail());
        customerDTO.setPhoneNumber(customer.getPhoneNumber());
        return customerDTO;
    }
    
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        // Trouver le client par ID
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));

        // Mettre à jour les champs
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setEmail(customerDTO.getEmail());
        customer.setPhoneNumber(customerDTO.getPhoneNumber());

        // Sauvegarder dans la base de données
        Customer updatedCustomer = customerRepository.save(customer);

        // Retourner un DTO mis à jour
        CustomerDTO updatedDTO = new CustomerDTO();
        updatedDTO.setFirstName(updatedCustomer.getFirstName());
        updatedDTO.setLastName(updatedCustomer.getLastName());
        updatedDTO.setEmail(updatedCustomer.getEmail());
        updatedDTO.setPhoneNumber(updatedCustomer.getPhoneNumber());

        return updatedDTO;
    }

    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public String updatePasswordWithoutVerification(Long customerId, String newPassword, String confirmPassword) {
        // Vérifier si les deux mots de passe correspondent
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Les mots de passe ne correspondent pas");
        }

        // Récupérer l'utilisateur par ID
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        Customer customer = customerOptional.get();

        // Mettre à jour le mot de passe encodé
        customer.setPassword(passwordEncoder.encode(newPassword));
        customerRepository.save(customer);

        return "Mot de passe mis à jour avec succès";
    }

    
    public void addMiniBio(Long customerId, String miniBio) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Vérifie et initialise si nécessaire
        if (customer.getMiniBios() == null) {
            customer.setMiniBios(new ArrayList<>()); 
        }

        customer.getMiniBios().add(miniBio);
        customerRepository.save(customer);
    }

    // Récupérer les mini-bios
    public List<String> getMiniBios(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return customer.getMiniBios();
    }

    // Supprimer une mini-bio
    public void deleteMiniBio(Long customerId, String miniBio) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (customer.getMiniBios() != null) {
            // Rechercher la minibio correspondante de manière case-insensitive et trim
            Optional<String> matchingMiniBio = customer.getMiniBios().stream()
                    .filter(bio -> bio.trim().equalsIgnoreCase(miniBio.trim()))
                    .findFirst();

            if (matchingMiniBio.isPresent()) {
                customer.getMiniBios().remove(matchingMiniBio.get());
                customerRepository.save(customer);
            } else {
                throw new RuntimeException("Mini-bio not found");
            }
        } else {
            throw new RuntimeException("Mini-bio list is empty");
        }
    }


    
    
    
    
    
}
