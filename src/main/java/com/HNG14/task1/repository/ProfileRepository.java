package com.HNG14.task1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.HNG14.task1.model.Profile;


@Repository
public interface ProfileRepository extends JpaRepository<Profile, String>,JpaSpecificationExecutor<Profile>  {

    Optional<Profile> findByName(String name);

    List<Profile> findByGenderIgnoreCase(String gender);
    List<Profile> findByAgeGroupIgnoreCase(String ageGroup);
    List<Profile> findByCountryIdIgnoreCase(String countryId);
    List<Profile> findByGenderIgnoreCaseAndAgeGroupIgnoreCaseAndCountryIdIgnoreCase(String gender, String ageGroup, String countryId);

    boolean existsByName(String name);  
    
}
