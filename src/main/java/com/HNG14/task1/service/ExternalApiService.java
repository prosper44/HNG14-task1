package com.HNG14.task1.service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;




@Service
public class ExternalApiService {
    
    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getGenderName(String name){
        String url = "https://api.genderize.io?name=" + name;
        ResponseEntity<Map> response = restTemplate.getForEntity(url,Map.class);
        Map<String, Object> body = response.getBody();

        if(body == null || body.get("gender") == null || ((Number)body.get("count")).intValue() == 0){
            
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("status", "502");   
            errorResponse.put("message", url + " returned an invalid response");
            return errorResponse;
        }

        return body;
        
    }

    public Map<String, Object> getAgeData(String name)
    {
        String url = "https://api.agify.io?name=" + name;
        ResponseEntity<Map> response = restTemplate.getForEntity(url,Map.class);
        Map<String, Object> body = response.getBody();

        if(body == null || body.get("age") == null){
             Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("status", "502");   
            errorResponse.put("message", url + " returned an invalid response");
            return errorResponse;
        }
        return body;

    }

    public Map<String, Object> getNationalityData(String name)
    {
        String url = "https://api.nationalize.io?name=" + name;

        ResponseEntity<Map> response = restTemplate.getForEntity(url,Map.class);
        Map<String, Object> body = response.getBody();
        if(body == null)
        {
             Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("status", "502");   
            errorResponse.put("message", url + " returned an invalid response");
            return errorResponse;
        }

        List<Map<String, Object>> countries = (List<Map<String, Object>>) body.get("country");

        

        if(countries == null || countries.isEmpty()){
             Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("status", "502");   
            errorResponse.put("message", url + " returned an invalid response");
            return errorResponse;
        }
        return body;
    }

    public Map<String, Object> getTopCountry(List<Map<String, Object>> countries)
    {

        if(countries == null || countries.isEmpty()){
             Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("status", "502");   
            errorResponse.put("message", " returned an invalid response");
            return errorResponse;
        }
        return countries.stream()
            .max(Comparator.comparing(country -> ((Number)country.get("probability")).doubleValue()))
            .get();
    }
}


