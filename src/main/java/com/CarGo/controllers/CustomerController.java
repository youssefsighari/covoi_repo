package com.CarGo.controllers;

import com.CarGo.dto.CustomerDTO; 
import com.CarGo.entities.Customer;
import com.CarGo.repository.CustomerRepository;
import com.CarGo.services.FileStorageService;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;
    
   

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Endpoint pour uploader une photo de profil pour un client.
     *
     * @param id   ID du client
     * @param file Fichier à uploader
     * @return Nom du fichier uploadé
     */
    @PostMapping("/{id}/upload-photo")
    public ResponseEntity<Map<String, String>> uploadProfilePhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            fileStorageService.storeFile(file, id);

            return ResponseEntity.ok(Map.of(
                "message", "Photo uploaded successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/{id}/profile-photo")
    public ResponseEntity<byte[]> getProfilePhotoByCustomerId(@PathVariable Long id) {
        try {
            byte[] imageData = fileStorageService.getProfilePhoto(id);

            // Détection du type MIME par défaut en image JPEG
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageData);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

 
    
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerDetails(@PathVariable Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            return ResponseEntity.ok(customer.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    
    @GetMapping("/details/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        CustomerDTO customerDTO = fileStorageService.getCustomerById(id);
        return ResponseEntity.ok(customerDTO);
    }
    
    @PutMapping("/{id}/update")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerDTO customerDTO) {
        // Appeler la méthode du service
        CustomerDTO updatedCustomer = fileStorageService.updateCustomer(id, customerDTO);
        return ResponseEntity.ok(updatedCustomer);
    }

    
    @PutMapping("/{id}/update-password")
    public ResponseEntity<?> updatePasswordWithoutVerification(
            @PathVariable Long id,
            @RequestBody Map<String, String> passwordData) {
        try {
            String newPassword = passwordData.get("newPassword");
            String confirmPassword = passwordData.get("confirmPassword");

            // Appeler le service pour mettre à jour le mot de passe
            String responseMessage = fileStorageService.updatePasswordWithoutVerification(id, newPassword, confirmPassword);

            return ResponseEntity.ok(Map.of("message", responseMessage));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
  
    @PostMapping("/{id}/add-minibio")
    public ResponseEntity<Map<String, String>> addMiniBio(@PathVariable Long id, @RequestBody String miniBio) {
        // Validation de la miniBio : vérifier si elle est vide ou nulle
        if (miniBio == null || miniBio.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "La mini-bio ne peut pas être vide."));
        }

        // Nettoyage de la chaîne si elle contient un JSON mal formé
        if (miniBio.startsWith("{") && miniBio.endsWith("}")) {
            miniBio = miniBio.substring(miniBio.indexOf(":") + 2, miniBio.lastIndexOf("\""));
        }

        // Ajouter la mini-bio dans le service
        fileStorageService.addMiniBio(id, miniBio);

        // Retourner une réponse JSON avec un message de succès
        return ResponseEntity.ok(Map.of("message", "Mini-bio ajoutée avec succès !"));
    }

    // Endpoint pour récupérer les mini-bios
    @GetMapping("/{id}/minibios")
    public ResponseEntity<List<String>> getMiniBios(@PathVariable Long id) {
        List<String> miniBios = fileStorageService.getMiniBios(id);
        return ResponseEntity.ok(miniBios);
    }

    // Endpoint pour supprimer une mini-bio
    @DeleteMapping("/{id}/delete-minibio")
    public ResponseEntity<String> deleteMiniBio(@PathVariable Long id, @RequestParam(required = false) String miniBio) {
        if (miniBio == null || miniBio.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Le paramètre 'miniBio' est requis.");
        }

        // Nettoyer la chaîne avant de l'utiliser
        miniBio = miniBio.trim();

        try {
            fileStorageService.deleteMiniBio(id, miniBio);
            return ResponseEntity.ok("Mini-bio supprimée avec succès !");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }



} 
