package com.HNG14.task1.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.HNG14.task1.exception.CustomNotFoundException;
import com.HNG14.task1.model.Profile;
import com.HNG14.task1.repository.ProfileRepository;


@Service
public class ProfileService {
    private final ExternalApiService externalApiService;
    private final ProfileRepository profileRepository;

    public ProfileService(ExternalApiService externalApiService, ProfileRepository profileRepository) {
        this.externalApiService = externalApiService;
        this.profileRepository = profileRepository;
    }

    public Map<String, Object> createProfile(String name)
    {
        name = name.toLowerCase();
        

        Optional<Profile> profiles = profileRepository.findByName(name);

        if(profiles.isPresent())
        {
            Profile existing = profiles.get();
               Map<String, Object> response1 = new LinkedHashMap<>();

         response1.put("id", existing.getId());
            response1.put("name", existing.getName());
            response1.put("gender", existing.getGender());
            response1.put("gender_probability", existing.getGenderProbability());
            response1.put("sample_size", existing.getSampleSize());
            response1.put("age", existing.getAge());
            response1.put("age_group", existing.getAgeGroup());
            response1.put("country_id", existing.getCountryId());
            response1.put("country_probability", existing.getCountryProbability());
            response1.put("created_at", existing.getCreatedAt());

        
            Map<String, Object> existingProfile = new LinkedHashMap<>();
            existingProfile.put("status", "success");
            existingProfile.put("message", "Profile already exists");
            existingProfile.put("data", response1);
            return existingProfile;
        

        }
        
     

        try {
            
            Map<String, Object> genderData = externalApiService.getGenderName(name);
            Map<String, Object> ageData = externalApiService.getAgeData(name);
            Map<String, Object> nationality = externalApiService.getNationalityData(name);

            String gender = (String) genderData.get("gender");
            double genderPobability = ((Number) genderData.get("probability")).doubleValue();
            int sampleSize = ((Number) genderData.get("count")).intValue();

            int age = ((Number) ageData.get("age")).intValue();

            List<Map<String, Object>> countries = (List<Map<String, Object>>) nationality.get("country");
            Map<String, Object> topCountry = externalApiService.getTopCountry(countries);

            String countryId = (String) topCountry.get("country_id");
            double countryProbability = ((Number) topCountry.get("probability")).doubleValue();

            String ageGroup;
            if(age <= 12){
                ageGroup = "child";
            } else if(age <= 19){
                ageGroup = "teenager";
            } else if(age <= 59) {
                ageGroup = "adult";
            } else {
                ageGroup = "senior";
            }

        
            Profile profile = new Profile();
            Map<String,Object> displayProfile = new LinkedHashMap<>();

            profile.setName(name);
            profile.setGender(gender);
            profile.setGenderProbability(genderPobability);
            profile.setSampleSize(sampleSize);
            profile.setAge(age);
            profile.setAgeGroup(ageGroup);
            profile.setCountryId(countryId);
            profile.setCountryProbability(countryProbability);

            profileRepository.save(profile);

            displayProfile.put("id", profile.getId());
            displayProfile.put("name", profile.getName());
            displayProfile.put("gender", profile.getGender());
            displayProfile.put("gender_probability", profile.getGenderProbability());
            displayProfile.put("sample_size", profile.getSampleSize());
            displayProfile.put("age", profile.getAge());
            displayProfile.put("age_group", profile.getAgeGroup());
            displayProfile.put("country_id", profile.getCountryId());
            displayProfile.put("country_probability", profile.getCountryProbability());
            displayProfile.put("created_at", profile.getCreatedAt());
            

            

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("status", "success");
            response.put("data", displayProfile);

            return response;
        } catch (Exception e) {
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return errorResponse;
        }
    }

    public Map<String, Object> getProfileById(String id)
    {
        Optional<Profile> existing = profileRepository.findById(id);
        Profile getExisting = existing.get();
              Map<String, Object> response1 = new LinkedHashMap<>();

         response1.put("id", getExisting.getId());
            response1.put("name", getExisting.getName());
            response1.put("gender", getExisting.getGender());
            response1.put("gender_probability", getExisting.getGenderProbability());
            response1.put("sample_size", getExisting.getSampleSize());
            response1.put("age", getExisting.getAge());
            response1.put("age_group", getExisting.getAgeGroup());
            response1.put("country_id", getExisting.getCountryId());
            response1.put("country_probability", getExisting.getCountryProbability());
            response1.put("created_at", getExisting.getCreatedAt());

        if(existing.isPresent()){
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("status", "success");
            response.put("data", response1);
            return response;
        } else {
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Profile not found for id: " + id);
            return errorResponse;
        }
    }

    public ResponseEntity<Map<String, Object>> getProfiles(String gender, String countryId, String ageGroup)
    {
       List<Profile> profiles;

        // validate gender
        if (gender != null) {
            gender = gender.trim().toLowerCase();
            if (!gender.equals("male") && !gender.equals("female")) {
                Map<String,Object> error = new LinkedHashMap<>();
                error.put("status", "error");
                error.put("message", "Invalid gender provided. Allowed values: male, female");
                return ResponseEntity.badRequest().body(error);

            }
        }

        // validate ageGroup
        if (ageGroup != null) {
            ageGroup = ageGroup.trim().toLowerCase();
            List<String> validAgeGroups = Arrays.asList("child", "teenager", "adult", "senior");
            if (!validAgeGroups.contains(ageGroup)) {
                Map<String,Object> error = new LinkedHashMap<>();
                error.put("status", "error");   
                    error.put("message", "Invalid ageGroup provided. Allowed values: child, teenager, adult, senior");
                return ResponseEntity.badRequest().body(error);

            }
        }

        // validate countryId
        if (countryId != null) {
            countryId = countryId.trim().toUpperCase();
            if (countryId.length() != 2) {
                Map<String,Object> error = new LinkedHashMap<>();
                error.put("status", "error");
                error.put("message", "Invalid countryId. Must be ISO 3166-1 alpha-2 code (e.g., NG, US, GB)");
                
                return ResponseEntity.badRequest().body(error);
            }
        }

        // apply filters
        if (gender != null && ageGroup != null && countryId != null) {
            profiles = profileRepository.findByGenderIgnoreCaseAndAgeGroupIgnoreCaseAndCountryIdIgnoreCase(gender, ageGroup, countryId);
        } else if (gender != null) {
            profiles = profileRepository.findByGenderIgnoreCase(gender);
        } else if (ageGroup != null) {
            profiles = profileRepository.findByAgeGroupIgnoreCase(ageGroup);
        } else if (countryId != null) {
            profiles = profileRepository.findByCountryIdIgnoreCase(countryId);
        } else {
            profiles = profileRepository.findAll();
        }


        List<Map<String, Object>> response = new ArrayList<>();
        for (Profile profile : profiles) {
            Map<String, Object> profileData = new LinkedHashMap<>();
            profileData.put("id", profile.getId());
            profileData.put("name", profile.getName());
            profileData.put("gender", profile.getGender());
            profileData.put("gender_probability", profile.getGenderProbability());
            profileData.put("sample_size", profile.getSampleSize());
            profileData.put("age", profile.getAge());
            profileData.put("age_group", profile.getAgeGroup());
            profileData.put("country_id", profile.getCountryId());
            profileData.put("country_probability", profile.getCountryProbability());
            profileData.put("created_at", profile.getCreatedAt());
            response.add(profileData);
        }

        Map<String, Object> finalResponse = new LinkedHashMap<>();
        finalResponse.put("status", "success");
        finalResponse.put("count", profiles.size());
        finalResponse.put("data", response);

        return ResponseEntity.ok(finalResponse);
    }

    public void deleteProfile(String id)
    {
        Optional<Profile> existing = profileRepository.findById(id);
        if(!(existing.isPresent()))
        {
            throw new CustomNotFoundException("Profile not found");  
        }

        Profile getExisting = existing.get();
        profileRepository.delete(getExisting);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "deleted successfully");
        response.put("message", "Profile with id: " + id + " has been deleted");

        

           

        
    }
}
