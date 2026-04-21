# HNG14 Task 1 – Profile Classification API

A Spring Boot REST API that integrates multiple external services (Genderize, Agify, Nationalize) to generate and store user profile data including gender, age, and nationality insights.

---

## 🚀 Live API

---

## 📌 Features

- Accepts a name input
- Calls 3 external APIs:
  - Genderize API
  - Agify API
  - Nationalize API
- Generates enriched profile data
- Stores profiles in a MySQL database
- Prevents duplicate profile creation (idempotency)
- Supports filtering and retrieval
- Full CRUD support (Create, Read, Delete)

---

## 🛠️ Tech Stack

- Java 17+
- Spring Boot
- Spring Data JPA
- MySQL
- Maven
- RestTemplate

---

## 📡 API Endpoints

### 1. Create Profile
POST /api/profiles


#### Request Body:
```json
{
  "name": "ella"
}

{
  "status": "success",
  "data": {
    "id": "uuid-v7",
    "name": "ella",
    "gender": "female",
    "gender_probability": 0.99,
    "sample_size": 1234,
    "age": 46,
    "age_group": "adult",
    "country_id": "NG",
    "country_probability": 0.85,
    "created_at": "2026-04-01T12:00:00Z"
  }
}

GET /api/profiles/{id}

GET /api/profiles?gender=male&countryId=NG&ageGroup=adult
DELETE /api/profiles/{id}




# Profile Search API

A Spring Boot application that provides an API for searching user profiles with **rule-based parsing** of plain English queries.  
The system converts natural phrases like *"young males from Nigeria"* into structured filters (gender, age ranges, age groups, country codes, pagination) and queries the database accordingly.

---

## 🚀 Features

- **Rule-based query parsing** (no AI/LLMs)
- **Combinable filters**: results must match all non-empty conditions
- **Pagination & sorting** support (`page`, `limit`, `sort_by`, `order`)
- **Seed data loading** from JSON (with duplicate prevention)
- **Error handling** for malformed queries (e.g. `"males above "`)

---

## 🔎 Example Queries

| Plain English Query                  | Parsed Filters                                    |
|--------------------------------------|---------------------------------------------------|
| `young males`                        | `gender=male`, `min_age=16`, `max_age=24`         |
| `females above 30`                   | `gender=female`, `min_age=30`                     |
| `people from angola`                 | `country_id=AO`                                   |
| `adult males from kenya`             | `gender=male`, `age_group=adult`, `country_id=KE` |
| `male and female teenagers above 17` | `age_group=teenager`, `min_age=17`                |
| `young males from nigeria`           | `gender=male`, `min_age=16`, `max_age=24`, `country_id=NG` |

---

## 📡 API Endpoints

### Search Profiles
