package com.HNG14.task1.model;


import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import com.github.f4b6a3.uuid.UuidCreator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    private String id;

    
    

@NotBlank(message = "Name is required")
    @Size(min = 1, message = "Name cannot be empty")
    @Pattern(regexp = ".*\\D.*", message = "Name cannot be purely numeric")
@Column(nullable = false, unique = true)
    private String name;

    private String  gender;

    private double genderProbability;

    private int sampleSize;
 
    private int age;

    private String ageGroup;

    private String countryId;

    private double countryProbability;

    private String createdAt;


    @PrePersist
    public  void PrePersist() {
        if(this.id == null){
            this.id = UuidCreator.getTimeOrderedEpoch().toString();
        }

        if(this.createdAt == null){
            this.createdAt = Instant.now()
    .atOffset(ZoneOffset.UTC)
    .truncatedTo(ChronoUnit.SECONDS)
    .format(DateTimeFormatter.ISO_INSTANT);
        }
    }  
    
    
    public Profile() {
    }   

    // Getters and Setters      
    public String getId() {
        return id;
    }   
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name.toLowerCase();
    }
    public String getGender() {
        return gender;      
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public double getGenderProbability() {
        return genderProbability;
    }   
    public void setGenderProbability(double genderProbability) {
        this.genderProbability = genderProbability;
    }
    public int getSampleSize() {
        return sampleSize;
    }
    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getAgeGroup() {
        return ageGroup;
    }
    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }
    public String getCountryId() {
        return countryId;
    }
    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }
    public double getCountryProbability() {
        return countryProbability;
    }
    public void setCountryProbability(double countryProbability) {
        this.countryProbability = countryProbability;
    }
    public String getCreatedAt() {
        return createdAt;
    }

}
