package com.example.jav_projecto1.controller;

import com.example.jav_projecto1.dto.AccountDTO;
import com.example.jav_projecto1.entities.Account;
import com.example.jav_projecto1.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProfileController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    @Autowired
     AccountService accountService;

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody AccountDTO accountDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            logger.warn("No session found for profile update");
            return ResponseEntity.status(401).body("Not logged in");
        }
        Object userObj = session.getAttribute("userLogin");
        logger.info("Update profile request, session id={}, user={}", session.getId(), userObj);
        if (userObj == null) {
            logger.warn("Unauthorized profile update attempt: no user in session id={}", session.getId());
            return ResponseEntity.status(401).body("Not logged in");
        }
        Account user = (Account) userObj;
        try {
            Account updated = accountService.updateProfileWithSession(user.getAccountId(), accountDTO, session);
            return ResponseEntity.ok(AccountService.toDTO(updated));
        } catch (Exception e) {
            logger.error("Profile update error", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> req, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            logger.warn("No session found for change-password");
            return ResponseEntity.status(401).body("Not logged in");
        }
        try {
            String result = accountService.changePasswordWithSession(req, session);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
