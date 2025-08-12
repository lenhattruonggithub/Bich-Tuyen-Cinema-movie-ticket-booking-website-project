package com.example.jav_projecto1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EmailService.
 * Uses Mockito to mock JavaMailSender and JUnit 5 for assertions.
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    @Mock private JavaMailSender mailSender;
    @Mock private HttpServletRequest request;
    @Mock private HttpSession session;
    @InjectMocks private EmailService emailService;

    @BeforeEach
    void setUp() {

    }

    /**
     * Test sendBookingConfirmation should send an email.
     */
    @Test
    void testSendBookingConfirmation() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        emailService.sendBookingConfirmation("to@example.com", "Movie", "Show", "A1");
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    /**
     * Test sendCancelBooking should send an email.
     */
    @Test
    void testSendCancelBooking() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        emailService.sendCancelBooking("to@example.com", "Movie", "Show", "A1");
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    /**
     * Test sendForgotPasswordEmail should send an email.
     */
    @Test
    void testSendForgotPasswordEmail() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        emailService.sendForgotPasswordEmail("to@example.com", "user", "pass");
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    /**
     * Test sendVerifyCodeEmail should send an email.
     */
    @Test
    void testSendVerifyCodeEmail() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        emailService.sendVerifyCodeEmail("to@example.com", "123456");
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    /**
     * Test generateAndSendVerifyCode should set session attributes and send email.
     */
    @Test
    void testGenerateAndSendVerifyCode() {
        when(request.getSession(anyBoolean())).thenReturn(session);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        String code = emailService.generateAndSendVerifyCode("to@example.com", request);
        assertNotNull(code);
        verify(session).setAttribute(eq("verifyEmail"), eq("to@example.com"));
        verify(session).setAttribute(eq("verifyCode"), anyString());
        verify(session).setMaxInactiveInterval(300);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    /**
     * Test generateAndSendVerifyCode should throw if email is null or empty.
     */
    @Test
    void testGenerateAndSendVerifyCode_InvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> emailService.generateAndSendVerifyCode(null, request));
        assertThrows(IllegalArgumentException.class, () -> emailService.generateAndSendVerifyCode("", request));
    }
} 