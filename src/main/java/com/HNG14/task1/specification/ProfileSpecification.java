package com.HNG14.task1.specification;
import org.springframework.data.jpa.domain.Specification;
import com.HNG14.task1.model.Profile;
public class ProfileSpecification {

    public static Specification<Profile> hasGender(String gender) {
        return (root, query, cb) -> {
            if (gender == null || gender.isBlank()) return cb.conjunction();
            return cb.equal(cb.lower(root.get("gender")), gender.toLowerCase());
        };
    }

    public static Specification<Profile> hasAgeGroup(String ageGroup) {
        return (root, query, cb) -> {
            if (ageGroup == null || ageGroup.isBlank()) return cb.conjunction();
            return cb.equal(cb.lower(root.get("ageGroup")), ageGroup.toLowerCase());
        };
    }

    public static Specification<Profile> hasCountryId(String countryId) {
        return (root, query, cb) -> {
            if (countryId == null || countryId.isBlank()) return cb.conjunction();
            return cb.equal(cb.upper(root.get("countryId")), countryId.toUpperCase());
        };
    }

    public static Specification<Profile> minAge(Integer minAge) {
        return (root, query, cb) -> {
            if (minAge == null) return cb.conjunction();
            return cb.greaterThanOrEqualTo(root.get("age"), minAge);
        };
    }

    public static Specification<Profile> maxAge(Integer maxAge) {
        return (root, query, cb) -> {
            if (maxAge == null) return cb.conjunction();
            return cb.lessThanOrEqualTo(root.get("age"), maxAge);
        };
    }

    public static Specification<Profile> minGenderProbability(Double prob) {
        return (root, query, cb) -> {
            if (prob == null) return cb.conjunction();
            return cb.greaterThanOrEqualTo(root.get("genderProbability"), prob);
        };
    }

    public static Specification<Profile> minCountryProbability(Double prob) {
        return (root, query, cb) -> {
            if (prob == null) return cb.conjunction();
            return cb.greaterThanOrEqualTo(root.get("countryProbability"), prob);
        };
    }
}
