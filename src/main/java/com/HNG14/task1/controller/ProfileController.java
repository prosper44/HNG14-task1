package com.HNG14.task1.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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



@RestController
@RequestMapping("/")

public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

  

    @PostMapping("/api/profiles")
    public ResponseEntity<?> createProfile(@RequestBody Profile profile) {

        Object nameObj = profile.getName();

      
         

    // Missing name
    if (nameObj == null) {
        return ResponseEntity.badRequest().body(Map.of(
            "status", "error",
            "message", "Name is required"
        ));
    }

    String name = nameObj.toString().trim();

    // Empty name
    if (name.isEmpty()) {
        return ResponseEntity.badRequest().body(Map.of(
            "status", "error",
            "message", "Name cannot be empty"
        ));
    }

    // Numeric name
    if (name.matches("\\d+")) {
        return ResponseEntity.unprocessableEntity().body(Map.of(
            "status", "error",
            "message", "Name cannot be numeric"
        ));
    }

    // Other invalid cases (symbols, too short/long, repeated letters)
    if (!name.matches("^[A-Za-z\\-'\\s]+$") ||
        name.matches(".*([A-Za-z])\\1{3,}.*") ||
        name.length() < 2 ||
        name.length() > 15) {
        return ResponseEntity.unprocessableEntity().body(Map.of(
            "status", "error",
            "message", "Invalid name format"
        ));
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
        @RequestParam(required = false) String ageGroup,
        @RequestParam(required = false) String countryId,
        @RequestParam(required = false) Integer min_age,
        @RequestParam(required = false) Integer max_age,
        @RequestParam(required = false) Double min_gender_probability,
        @RequestParam(required = false) Double min_country_probability,
        @RequestParam(defaultValue = "createdAt") String sort_by,
        @RequestParam(defaultValue = "asc") String order,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int limit
    )
    {
         return ResponseEntity.ok(
        profileService.getProfiles(
            gender, ageGroup, countryId,
            min_age, max_age,
            min_gender_probability,
            min_country_probability,
            sort_by, order,
            page, limit
        )
    );
    }
    
    @DeleteMapping("/api/profiles/{id}")
    public ResponseEntity<?> deleteProfile(@PathVariable String id) {

        
        try {
            if(id == null || id.trim().isEmpty())
            {
            Map<String,Object> error = new LinkedHashMap<>();
                error.put("status", "error");
                error.put("message", "ID is required and cannot be empty");
            return ResponseEntity.badRequest().body(error);
        }
            profileService.deleteProfile(id);
                
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            Map<String,Object> error = new LinkedHashMap<>();
                error.put("status", "error");
                error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }


    @GetMapping("/api/profiles/search")
public ResponseEntity<?> searchProfiles(
        @RequestParam String q,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int limit
) {
    if(q == null || q.trim().isEmpty()) {
        Map<String,Object> error = new LinkedHashMap<>();
                error.put("status", "error");
                error.put("message", "Search query 'q' is required and cannot be empty");
        return ResponseEntity.badRequest().body(error);
    }
    return ResponseEntity.ok(profileService.search(q, page, limit));
}
}
