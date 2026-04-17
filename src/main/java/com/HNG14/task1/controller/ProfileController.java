package com.HNG14.task1.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.HNG14.task1.exception.CustomNotFoundException;
import com.HNG14.task1.model.Profile;
import com.HNG14.task1.service.ProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/")

public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

  

    @PostMapping("/api/profiles")
    public ResponseEntity<?> createProfile(@Valid @RequestBody Profile profile) {

        Object nameObj = profile.getName();

      
      

        if (nameObj == null || nameObj.toString().trim().isEmpty()) {
            

            Map<String,Object> error = new LinkedHashMap<>();
                error.put("status", "error");
                error.put("message", "Name is required and cannot be empty");
                
            return ResponseEntity.badRequest().body(error);
        }
        
        if( nameObj instanceof Number
    || nameObj.toString().matches("\\d+")
    || !nameObj.toString().matches("^[A-Za-z\\-'\\s]+$")
    || nameObj.toString().matches(".*([A-Za-z])\\1{3,}.*")
    || nameObj.toString().length() < 2
    || nameObj.toString().length() > 15) 
        {
             Map<String,Object> error = new LinkedHashMap<>();
                error.put("status", "error");
                error.put("message", "Invalid name type");
                
            return ResponseEntity.unprocessableEntity().body(error);
        }

    


       

        try {
            Map<String, Object> profileData = profileService.createProfile(nameObj.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(profileData);
        } catch (CustomNotFoundException e) {

            Map<String,Object> error = new LinkedHashMap<>();

                error.put("status", "error");
                error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
        }
    }

    @GetMapping("/api/profiles/{id}")
    public ResponseEntity<?> getProfile(@PathVariable String id) {
        try {
            Map<String, Object> profileData = profileService.getProfileById(id);
            return ResponseEntity.ok(profileData);
        } catch (RuntimeException e) {
            Map<String,Object> error = new LinkedHashMap<>();
                error.put("status", "error");
                error.put("message", "Profile not found for the given ID: " + id + ". " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/api/profiles")
    public ResponseEntity<?> getAllProfiles(
        @RequestParam(required = false) String gender,
        @RequestParam(required = false) String countryId,
        @RequestParam(required = false) String ageGroup
    )
    {
        return ResponseEntity.ok(profileService.getProfiles(gender, countryId, ageGroup));
    }
    
    @DeleteMapping("/api/profiles/{id}")
    public ResponseEntity<?> deleteProfile(@PathVariable String id) {

        
        try {
            if(id == null || id.trim().isEmpty() || !(id instanceof String) ||!id.equals(id))
            {
            Map<String,Object> error = new LinkedHashMap<>();
                error.put("status", "error");
                error.put("message", "ID is required and cannot be empty");
            return ResponseEntity.badRequest().body(error);
        }
            profileService.deleteProfile(id);
                Map<String,Object> response = new LinkedHashMap<>();
                    response.put("status", "success");
                    response.put("message", "Profile with ID: " + id + " has been deleted successfully.");
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            Map<String,Object> error = new LinkedHashMap<>();
                error.put("status", "error");
                error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
