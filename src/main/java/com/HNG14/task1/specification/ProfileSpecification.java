package com.HNG14.task1.specification;
import org.springframework.data.jpa.domain.Specification;
import com.HNG14.task1.model.Profile;

public class ProfileSpecification {
  

   public static Specification<Profile> hasGender(String gender) {
    if (gender == null || gender.isBlank()) {
        return (root, query, cb) -> cb.conjunction(); // skip filter
    }
    return (root, query, cb) -> cb.equal(cb.upper(root.get("gender")), gender.toUpperCase());
}


    public static Specification<Profile> hasAgeGroup(String ageGroup) {
        return (root, query, cb) ->
            ageGroup == null ? null : cb.equal(cb.lower(root.get("ageGroup")), ageGroup.toLowerCase());
    }

    public static Specification<Profile> hasCountryId(String countryId) {
        return (root, query, cb) ->
            countryId == null ? null : cb.equal(cb.upper(root.get("countryId")), countryId.toUpperCase());
    }

    

    public static Specification<Profile> minAge(Integer minAge) {
        return (root, query, cb) ->
            minAge == null ? null : cb.greaterThanOrEqualTo(root.get("age"), minAge);
    }

    public static Specification<Profile> maxAge(Integer maxAge) {
        return (root, query, cb) ->
            maxAge == null ? null : cb.lessThanOrEqualTo(root.get("age"), maxAge);
    }

    public static Specification<Profile> minGenderProbability(Double prob) {
        return (root, query, cb) ->
            prob == null ? null : cb.greaterThanOrEqualTo(root.get("genderProbability"), prob);
    }

    public static Specification<Profile> minCountryProbability(Double prob) {
        return (root, query, cb) ->
            prob == null ? null : cb.greaterThanOrEqualTo(root.get("countryProbability"), prob);
    }

}
