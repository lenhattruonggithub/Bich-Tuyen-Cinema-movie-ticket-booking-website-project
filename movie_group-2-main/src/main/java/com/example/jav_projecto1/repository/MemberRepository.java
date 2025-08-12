package com.example.jav_projecto1.repository;

import com.example.jav_projecto1.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import com.example.jav_projecto1.entities.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByAccount_AccountId(Long accountId);

    Optional<Member> findByAccount(Account account);
}
