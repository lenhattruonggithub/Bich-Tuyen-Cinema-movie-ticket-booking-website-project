package com.example.jav_projecto1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class CaptchaService {

    @Value("${recaptcha.secret.key}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean verifyCaptcha(String captchaToken) {
        try {
            Map<String, String> requestParams = new HashMap<>();
            requestParams.put("secret", secretKey);
            requestParams.put("response", captchaToken);

            String url = RECAPTCHA_VERIFY_URL + "?secret=" + secretKey + "&response=" + captchaToken;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("success")) {
                return (Boolean) response.get("success");
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error verifying captcha: " + e.getMessage());
            return false;
        }
    }
} 