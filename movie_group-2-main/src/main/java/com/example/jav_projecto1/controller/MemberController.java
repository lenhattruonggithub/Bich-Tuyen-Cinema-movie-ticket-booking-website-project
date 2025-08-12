package com.example.jav_projecto1.controller;

import com.example.jav_projecto1.repository.MemberRepository;
import com.example.jav_projecto1.repository.RewardPointRepository;
import com.example.jav_projecto1.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/member")
public class MemberController {
    @Autowired
    private  MemberRepository memberRepository;
    @Autowired
    private RewardPointRepository rewardPointRepository;
    @Autowired
    private MemberService memberService;


    @GetMapping("/points/{accountId}")
    public ResponseEntity<?> getMemberPoints(@PathVariable Long accountId) {
        try {
            return ResponseEntity.ok(memberService.getMemberPoints(accountId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/points/deduct/{accountId}")
    public ResponseEntity<?> deductPoints(@PathVariable Long accountId, @RequestParam int points) {
        try {
            return ResponseEntity.ok(memberService.deductPoints(accountId, points));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/points/history/{accountId}")
    public ResponseEntity<?> getPointHistory(@PathVariable Long accountId) {
        try {
            return ResponseEntity.ok(memberService.getPointHistory(accountId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
} 