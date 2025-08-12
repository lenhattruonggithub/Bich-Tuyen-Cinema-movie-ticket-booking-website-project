package com.example.jav_projecto1.service;

import com.example.jav_projecto1.config.VNPayConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for VNPayService.
 * Uses Mockito to mock dependencies and JUnit 5 for assertions.
 */
@ExtendWith(MockitoExtension.class)
class VNPayServiceTest {
    // Mocked dependencies
    @Mock
    private HttpServletRequest request;
    // Inject mocks into VNPayService
    @InjectMocks
    private VNPayService vnPayService;

    /**
     * Test createOrder should return a payment URL containing required parameters.
     */
    @Test
    void testCreateOrder_ReturnsPaymentUrl() {
        int total = 100000;
        String orderInfo = "Test order";
        String urlReturn = "http://localhost/return";
        String paymentUrl = vnPayService.createOrder(total, orderInfo, urlReturn);
        assertNotNull(paymentUrl);
        assertTrue(paymentUrl.contains("vnp_Amount"));
        assertTrue(paymentUrl.contains("vnp_OrderInfo"));
        assertTrue(paymentUrl.contains("vnp_SecureHash"));
    }

    /**
     * Test orderReturn should return 1 if vnp_ResponseCode is '00'.
     */
    @Test
    void testOrderReturn_Success() {
        when(request.getParameterNames()).thenReturn(java.util.Collections.enumeration(java.util.List.of("vnp_ResponseCode", "vnp_TransactionStatus")));
        when(request.getParameter("vnp_ResponseCode")).thenReturn("00");
        when(request.getParameter("vnp_TransactionStatus")).thenReturn("00");
        when(request.getParameter("vnp_SecureHash")).thenReturn("abc");
        try (MockedStatic<VNPayConfig> mocked = mockStatic(VNPayConfig.class)) {
            mocked.when(() -> VNPayConfig.hashAllFields(any())).thenReturn("abc");
            int result = vnPayService.orderReturn(request);
            assertEquals(1, result);
        }
    }

    /**
     * Test orderReturn should return 0 if vnp_ResponseCode is not '00'.
     */
    @Test
    void testOrderReturn_Fail() {
        when(request.getParameterNames()).thenReturn(java.util.Collections.enumeration(java.util.List.of("vnp_ResponseCode", "vnp_TransactionStatus")));
        when(request.getParameter("vnp_ResponseCode")).thenReturn("01");
        when(request.getParameter("vnp_TransactionStatus")).thenReturn("01");
        when(request.getParameter("vnp_SecureHash")).thenReturn("abc");
        try (MockedStatic<VNPayConfig> mocked = mockStatic(VNPayConfig.class)) {
            mocked.when(() -> VNPayConfig.hashAllFields(any())).thenReturn("abc");
            int result = vnPayService.orderReturn(request);
            assertEquals(0, result);
        }
    }
} 