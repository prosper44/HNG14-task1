package com.HNG14.task1.seeder;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.HNG14.task1.model.Profile;
import com.HNG14.task1.repository.ProfileRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class DataSeeder {
  

    @Bean
    CommandLineRunner seedDatabase(ProfileRepository profileRepository, ObjectMapper mapper) {
        return args -> {
            
        try (InputStream inputStream = getClass().getResourceAsStream("/seed_profiles.json")) {
    JsonNode root = mapper.readTree(inputStream);
    JsonNode profilesNode = root.get("profiles");

   


    List<Profile> profiles = mapper.convertValue(
        profilesNode,
        new TypeReference<List<Profile>>() {}
    );

     List<Profile> existingProfile = profileRepository.findAll();
   
     if(existingProfile.isEmpty()) 
    {
         profileRepository.saveAll(profiles);
    }
    

   
}

        };
    }
    }

