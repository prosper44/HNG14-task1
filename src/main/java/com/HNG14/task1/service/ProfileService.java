package com.HNG14.task1.service;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;

import com.HNG14.task1.exception.CustomNotFoundException;
import com.HNG14.task1.model.Profile;
import com.HNG14.task1.repository.ProfileRepository;
import com.HNG14.task1.specification.ProfileSpecification;


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
        

        Optional<Profile> profileExist = profileRepository.findByName(name);

        if(profileExist.isPresent())
        {
            Profile existing = profileExist.get();
               Map<String, Object> response1 = new LinkedHashMap<>();

         response1.put("id", existing.getId());
            response1.put("name", existing.getName());
            response1.put("gender", existing.getGender());
            response1.put("gender_probability", existing.getGenderProbability());
            response1.put("country_name", existing.getCountryName());
            response1.put("age", existing.getAge());
            response1.put("age_group", existing.getAgeGroup());
            response1.put("country_id", existing.getCountryId());
            response1.put("country_probability", existing.getCountryProbability());
            response1.put("created_at", existing.getCreatedAt().toString());

        
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

            String countryName;
            
            switch (countryId) {
                case "US": 
                    countryName = "United States";
                    break;
                case "NG": 
                    countryName = "Nigeria";
                    break;
                case "KE":
                    countryName = "Kenya";
                    break;
                case "GB":
                    countryName = "United Kingdom";
                    break;
                case "DE":
                    countryName = "Germany";
                    break;
                case "FR":
                    countryName = "France";
                    break;
                case "IT":
                    countryName = "Italy";
                    break;
                case "ES":
                    countryName = "Spain";
                    break;
                case "CA":
                    countryName = "Canada";
                    break;
                case "AU":
                    countryName = "Australia";
                    break;
            
                default: countryName = "unknown";
                    break;
            }

            profile.setName(name);
            profile.setGender(gender);
            profile.setGenderProbability(genderPobability);
            profile.setCountryName(countryName);
            profile.setAge(age);
            profile.setAgeGroup(ageGroup);
            profile.setCountryId(countryId);
            profile.setCountryProbability(countryProbability);

            profileRepository.save(profile);

            displayProfile.put("id", profile.getId());
            displayProfile.put("name", profile.getName());
            displayProfile.put("gender", profile.getGender());
            displayProfile.put("gender_probability", profile.getGenderProbability());
            displayProfile.put("country_name", profile.getCountryName());
            displayProfile.put("age", profile.getAge());
            displayProfile.put("age_group", profile.getAgeGroup());
            displayProfile.put("country_id", profile.getCountryId());
            displayProfile.put("country_probability", profile.getCountryProbability());
            displayProfile.put("created_at", profile.getCreatedAt().toString());
            

            

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
            response1.put("country_name", getExisting.getCountryName());
            response1.put("age", getExisting.getAge());
            response1.put("age_group", getExisting.getAgeGroup());
            response1.put("country_id", getExisting.getCountryId());
            response1.put("country_probability", getExisting.getCountryProbability());
            response1.put("created_at", getExisting.getCreatedAt().toString());
       

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

    public Map<String, Object> getProfiles( String gender,
        String ageGroup,
        String countryId,
        Integer minAge,
        Integer maxAge,
        Double minGenderProbability,
        Double minCountryProbability,
        String sortBy,
        String order,
        int page,
        int limit)
    {
         

        Specification<Profile> spec = Specification
            .where(ProfileSpecification.hasGender(gender))
            .and(ProfileSpecification.hasAgeGroup(ageGroup))
            .and(ProfileSpecification.hasCountryId(countryId))
            .and(ProfileSpecification.minAge(minAge))
            .and(ProfileSpecification.maxAge(maxAge))
            .and(ProfileSpecification.minGenderProbability(minGenderProbability))
            .and(ProfileSpecification.minCountryProbability(minCountryProbability));


      Set<String> allowedSortFields = Set.of("createdAt", "age", "countryId");
    if (sortBy != null && !allowedSortFields.contains(sortBy)) {
        return Map.of("status", "error", "message", "Invalid sort_by field");
    }

    if (order != null && !(order.equalsIgnoreCase("asc") || order.equalsIgnoreCase("desc"))) {
    return Map.of("status", "error", "message", "Invalid order value");
}
    Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;

   Sort sort = Sort.by(direction, sortBy != null ? sortBy : "createdAt");

    Pageable pageable = PageRequest.of(page - 1, limit, sort);

    Page<Profile> result = profileRepository.findAll(spec, pageable);
    if(result.isEmpty()) {
        return Map.of(
            "status", "success",
            "message", "No profiles found matching the criteria",
            "data", List.of()
        );
    }
    
    List<Map<String, Object>> data = result.getContent().stream().map(profile -> {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("id", profile.getId());
        p.put("name", profile.getName());
        p.put("gender", profile.getGender());
        p.put("gender_probability", profile.getGenderProbability());
        p.put("age", profile.getAge());
        p.put("age_group", profile.getAgeGroup());
        p.put("country_id", profile.getCountryId());
        p.put("country_name", profile.getCountryName());
        p.put("country_probability", profile.getCountryProbability());
        p.put("created_at", profile.getCreatedAt().toString());
        return p;
    }).toList();

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("status", "success");
    response.put("page", page);
    response.put("limit", limit);
    response.put("total", result.getTotalElements());
    response.put("data", data);

    return response;
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

               
    }


   public Map<String, Object> search(String q, int page, int limit) {
    String query = q.toLowerCase();

    String gender = null;
    String ageGroup = null;
    String countryId = null;
    Integer minAge = null;
    Integer maxAge = null;
    Double minCountryProbability = null;

    // Gender detection
    if (query.contains("male")) gender = "male";
    if (query.contains("female")) gender = "female";

    // Age group detection
    if (query.contains("adult")) ageGroup = "adult";
    if (query.contains("teenager")) ageGroup = "teenager";

    // Age ranges
    if (query.contains("young")) {
        minAge = 16;
        maxAge = 24;
    }
    if (query.contains("above")) {
        String[] parts = query.split("above", 2);
        if (parts.length > 1) {
            String[] tokens = parts[1].trim().split("\\s+");
            if (tokens.length > 0 && tokens[0].matches("\\d+")) {
                minAge = Integer.parseInt(tokens[0]);
            } else {
                return Map.of("status", "error", "message", "Invalid query: 'above' must be followed by a number");
            }
        }
    }
    if (query.contains("below")) {
        String[] parts = query.split("below", 2);
        if (parts.length > 1) {
            String[] tokens = parts[1].trim().split("\\s+");
            if (tokens.length > 0 && tokens[0].matches("\\d+")) {
                maxAge = Integer.parseInt(tokens[0]);
            } else {
                return Map.of("status", "error", "message", "Invalid query: 'below' must be followed by a number");
            }
        }
    }

    // Country detection
   Map<String, String> countries = Map.ofEntries(
    Map.entry("nigeria", "NG"),
    Map.entry("kenya", "KE"),
    Map.entry("united states", "US"),
    Map.entry("usa", "US"),
    Map.entry("us", "US"),
    Map.entry("united kingdom", "GB"),
    Map.entry("uk", "GB"),
    Map.entry("britain", "GB"),
    Map.entry("germany", "DE"),
    Map.entry("france", "FR"),
    Map.entry("italy", "IT"),
    Map.entry("spain", "ES"),
    Map.entry("canada", "CA"),
    Map.entry("australia", "AU"),
    Map.entry("ghana", "GH")
);

    for (Map.Entry<String, String> entry : countries.entrySet()) {
        if (query.contains(entry.getKey())) {
            countryId = entry.getValue();
            break;
        }
    }

    // Probability threshold (example: "probability above 0.7")
    if (query.contains("probability")) {
        String[] tokens = query.split("probability");
        if (tokens.length > 1) {
            String after = tokens[1].trim();
            if (after.matches("above\\s+\\d+(\\.\\d+)?")) {
                String num = after.split("\\s+")[1];
                minCountryProbability = Double.parseDouble(num);
            }
        }
    }

    // If nothing detected
    if (gender == null && ageGroup == null && countryId == null && minAge == null && maxAge == null && minCountryProbability == null) {
        return Map.of("status", "error", "message", "Unable to interpret query");
    }

    // Call profile retrieval with strict filters + safe sorting
    return getProfiles(gender, ageGroup, countryId, minAge, maxAge, minCountryProbability, null, "created_at", "asc", page, limit);
}

}
