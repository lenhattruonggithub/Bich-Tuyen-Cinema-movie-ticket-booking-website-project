package com.example.jav_projecto1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CaptchaService.
 * Uses Mockito to mock RestTemplate and JUnit 5 for assertions.
 */
@ExtendWith(MockitoExtension.class)
class CaptchaServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private CaptchaService captchaService;

    @BeforeEach
    void setUp() {
        // Set the secretKey field using reflection
        ReflectionTestUtils.setField(captchaService, "secretKey", "test-secret");
        // Inject the mocked RestTemplate
        ReflectionTestUtils.setField(captchaService, "restTemplate", restTemplate);
    }

    /**
     * Test verifyCaptcha should return true when response contains success=true.
     */
    @Test
    void testVerifyCaptcha_Success() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);
        boolean result = captchaService.verifyCaptcha("token");
        assertTrue(result);
    }

    /**
     * Test verifyCaptcha should return false when response contains success=false.
     */
    @Test
    void testVerifyCaptcha_Fail() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);
        boolean result = captchaService.verifyCaptcha("token");
        assertFalse(result);
    }

    /**
     * Test verifyCaptcha should return false when response is null.
     */
    @Test
    void testVerifyCaptcha_NullResponse() {
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(null);
        boolean result = captchaService.verifyCaptcha("token");
        assertFalse(result);
    }

    /**
     * Test verifyCaptcha should return false when exception is thrown.
     */
    @Test
    void testVerifyCaptcha_Exception() {
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenThrow(new RuntimeException("error"));
        boolean result = captchaService.verifyCaptcha("token");
        assertFalse(result);
    }
} 