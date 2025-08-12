package com.example.jav_projecto1.service;


import com.example.jav_projecto1.repository.RewardPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.jav_projecto1.dto.RegisterRequest;
import com.example.jav_projecto1.entities.Account;
import com.example.jav_projecto1.entities.Member;
import com.example.jav_projecto1.enumm.Role_enum;
import com.example.jav_projecto1.repository.MemberRepository;
import com.example.jav_projecto1.entities.RewardPoint;
import com.example.jav_projecto1.dto.RewardPointHistoryDTO;
import java.util.Optional;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class MemberService {
    @Autowired
    public MemberRepository memberRepository;

    @Autowired
    public RewardPointRepository rewardPointRepository;

    @Autowired
    public AccountService accountService;


    public Member saveByRequest(RegisterRequest registerRequest) {
        Account account = accountService.saveByRequest(registerRequest, Role_enum.MEMBER);
        Member member = Member.builder().account(account).score(0).build();
        return memberRepository.save(member);
    }

    public Map<String, Object> getMemberPoints(Long accountId) {
        Optional<Member> memberOpt = memberRepository.findByAccount_AccountId(accountId);
        int points = memberOpt.map(Member::getScore).orElse(0);
        return Map.of("points", points);
    }

    public Map<String, Object> deductPoints(Long accountId, int points) {
        Optional<Member> memberOpt = memberRepository.findByAccount_AccountId(accountId);
        if (memberOpt.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy thành viên");
        }
        Member member = memberOpt.get();
        int current = member.getScore();
        if (points <= 0) {
            throw new IllegalArgumentException("Số điểm trừ phải lớn hơn 0");
        }
        if (current < points) {
            throw new IllegalArgumentException("Không đủ điểm để trừ");
        }
        member.setScore(current - points);
        memberRepository.save(member);
        RewardPoint rewardPoint = new RewardPoint();
        rewardPoint.setMember(member);
        rewardPoint.setPoints(points);
        rewardPoint.setRewardDate(LocalDateTime.now());
        rewardPoint.setType("DEDUCT");
        rewardPointRepository.save(rewardPoint);
        return Map.of("points", member.getScore());
    }

    public List<RewardPointHistoryDTO> getPointHistory(Long accountId) {
        Optional<Member> memberOpt = memberRepository.findByAccount_AccountId(accountId);
        if (memberOpt.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy thành viên");
        }
        Member member = memberOpt.get();
        List<RewardPoint> history = rewardPointRepository.findByMember_MemberIdOrderByRewardDateDesc(member.getMemberId());
        return history.stream()
                .map(rp -> RewardPointHistoryDTO.builder()
                        .rewardId(rp.getRewardId())
                        .points(rp.getPoints())
                        .rewardDate(rp.getRewardDate())
                        .type(rp.getType())
                        .build())
                .collect(Collectors.toList());
    }
}
