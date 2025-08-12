package com.example.jav_projecto1.controller;

import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.jav_projecto1.dto.*;
import com.example.jav_projecto1.entities.*;
import com.example.jav_projecto1.enumm.Role_enum;
import com.example.jav_projecto1.service.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private BanMessageService banMessageService;

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(
            @RequestBody RegisterRequest registerRequest,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(true);
        try {
            LoginResponse resp = accountService.handleRegister(registerRequest, session, memberService);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            logger.error("Registration error", e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        Optional<Account> accOpt = accountService.login(request.getUsername(), request.getPassword());
        if (accOpt.isPresent()) {
            Account acc = accOpt.get();

            if (acc.getRole() != null && acc.getRole().getRoleName() == Role_enum.BANNED) {
                Optional<BanMessage> banOpt = banMessageService.findByAccount_AccountId(acc.getAccountId());
                BanMessageDTO banDto = banOpt.map(BanMessageService::toDTO).orElse(null);
                return ResponseEntity.status(403).body(Map.of(
                        "banned", true,
                        "banMessage", banDto != null ? banDto.getMessage() : "Your account is banned.",
                        "banDate", banDto != null ? banDto.getBanDate() : null
                ));
            }

            HttpSession session = httpRequest.getSession(true); // create if missing
            session.setAttribute("userLogin", acc);

            logger.info("Created session id={} for user={}", session.getId(), acc.getUsername());

                LoginResponse resp = accountService.toLoginResponse(acc);
            return ResponseEntity.ok(resp);
        }

        logger.warn("Failed login attempt for username: {}", request.getUsername());
        return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // only get if exists
        if (session != null) {
            session.invalidate();
            logger.info("Session invalidated");
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/personal")
    public ResponseEntity<LoginResponse> getPersonalPage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            logger.warn("No session for /personal");
            return ResponseEntity.status(401).build();
        }

        Account acc = (Account) session.getAttribute("userLogin");
        if (acc == null) {
            logger.warn("No userLogin in session id={}", session.getId());
            return ResponseEntity.status(401).build();
        }

        LoginResponse resp = accountService.toLoginResponse(acc);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        if (!captchaService.verifyCaptcha(request.getCaptchaToken())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid captcha!"));
        }
        Optional<Account> accOpt = accountService.findByEmail(request.getEmail());
        if (accOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email does not exist!"));
        }
        Account acc = accOpt.get();
        emailService.sendForgotPasswordEmail(acc.getEmail(), acc.getUsername(), acc.getPassword());
        return ResponseEntity.ok(Map.of("message", "Account information has been sent to your email!"));
    }

    @PostMapping("/send-verify-code")
    public ResponseEntity<?> sendVerifyCode(
            @RequestBody Map<String, String> body,
            HttpServletRequest request
    ) {
        String email = body.get("email");
        try {
            emailService.generateAndSendVerifyCode(email, request);
            return ResponseEntity.ok(Map.of("message", "Verification code has been sent to your email!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
