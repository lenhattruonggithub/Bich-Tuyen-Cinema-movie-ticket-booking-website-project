package com.example.jav_projecto1.repository;

import com.example.jav_projecto1.entities.BanMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BanMessageRepository extends JpaRepository<BanMessage, Long> {
    Optional<BanMessage> findByAccount_AccountId(Long accountId);
    void deleteByAccount_AccountId(Long accountId);
}
