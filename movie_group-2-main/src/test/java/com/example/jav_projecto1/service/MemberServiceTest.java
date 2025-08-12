package com.example.jav_projecto1.service;

import com.example.jav_projecto1.dto.RegisterRequest;
import com.example.jav_projecto1.dto.RewardPointHistoryDTO;
import com.example.jav_projecto1.entities.Account;
import com.example.jav_projecto1.entities.Member;
import com.example.jav_projecto1.entities.RewardPoint;
import com.example.jav_projecto1.enumm.Role_enum;
import com.example.jav_projecto1.repository.MemberRepository;
import com.example.jav_projecto1.repository.RewardPointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MemberService.
 * Uses Mockito to mock dependencies and JUnit 5 for assertions.
 */
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock public MemberRepository memberRepository;
    @Mock public RewardPointRepository rewardPointRepository;
    @Mock public AccountService accountService;
    @InjectMocks public MemberService memberService;

    private Account account;
    private Member member;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setAccountId(1L);
        member = Member.builder().memberId(1L).account(account).score(100).build();
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("user1");
        registerRequest.setPassword("pass");
        registerRequest.setEmail("test@example.com");
    }

    /**
     * Test saveByRequest should create and save a new member.
     */
    @Test
    void testSaveByRequest() {
        when(accountService.saveByRequest(any(RegisterRequest.class), eq(Role_enum.MEMBER))).thenReturn(account);
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        Member result = memberService.saveByRequest(registerRequest);
        assertNotNull(result);
        assertEquals(1L, result.getMemberId());
    }

    /**
     * Test getMemberPoints should return points if member exists.
     */
    @Test
    void testGetMemberPoints_Found() {
        when(memberRepository.findByAccount_AccountId(1L)).thenReturn(Optional.of(member));
        Map<String, Object> result = memberService.getMemberPoints(1L);
        assertEquals(100, result.get("points"));
    }

    /**
     * Test getMemberPoints should return 0 if member not found.
     */
    @Test
    void testGetMemberPoints_NotFound() {
        when(memberRepository.findByAccount_AccountId(2L)).thenReturn(Optional.empty());
        Map<String, Object> result = memberService.getMemberPoints(2L);
        assertEquals(0, result.get("points"));
    }

    /**
     * Test deductPoints should deduct points and save reward point if valid.
     */
    @Test
    void testDeductPoints_Success() {
        when(memberRepository.findByAccount_AccountId(1L)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(rewardPointRepository.save(any(RewardPoint.class))).thenReturn(new RewardPoint());
        Map<String, Object> result = memberService.deductPoints(1L, 50);
        assertEquals(50, result.get("points"));
    }

    /**
     * Test deductPoints should throw if member not found.
     */
    @Test
    void testDeductPoints_MemberNotFound() {
        when(memberRepository.findByAccount_AccountId(2L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> memberService.deductPoints(2L, 10));
    }

    /**
     * Test deductPoints should throw if points <= 0.
     */
    @Test
    void testDeductPoints_InvalidPoints() {
        when(memberRepository.findByAccount_AccountId(1L)).thenReturn(Optional.of(member));
        assertThrows(IllegalArgumentException.class, () -> memberService.deductPoints(1L, 0));
        assertThrows(IllegalArgumentException.class, () -> memberService.deductPoints(1L, -5));
    }

    /**
     * Test deductPoints should throw if not enough points.
     */
    @Test
    void testDeductPoints_NotEnoughPoints() {
        member.setScore(10);
        when(memberRepository.findByAccount_AccountId(1L)).thenReturn(Optional.of(member));
        assertThrows(IllegalArgumentException.class, () -> memberService.deductPoints(1L, 20));
    }

    /**
     * Test getPointHistory should return list of RewardPointHistoryDTO if member exists.
     */
    @Test
    void testGetPointHistory_Found() {
        RewardPoint rp = new RewardPoint();
        rp.setRewardId(1L);
        rp.setPoints(10);
        rp.setRewardDate(LocalDateTime.now());
        rp.setType("ADD");
        when(memberRepository.findByAccount_AccountId(1L)).thenReturn(Optional.of(member));
        when(rewardPointRepository.findByMember_MemberIdOrderByRewardDateDesc(1L)).thenReturn(List.of(rp));
        List<RewardPointHistoryDTO> result = memberService.getPointHistory(1L);
        assertEquals(1, result.size());
        assertEquals(10, result.getFirst().getPoints());
    }

    /**
     * Test getPointHistory should throw if member not found.
     */
    @Test
    void testGetPointHistory_MemberNotFound() {
        when(memberRepository.findByAccount_AccountId(2L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> memberService.getPointHistory(2L));
    }
} 