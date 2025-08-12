package com.example.jav_projecto1.service;

import com.example.jav_projecto1.dto.BanMessageDTO;
import com.example.jav_projecto1.entities.BanMessage;
import com.example.jav_projecto1.entities.Account;
import com.example.jav_projecto1.repository.AccountRepository;
import com.example.jav_projecto1.repository.BanMessageRepository;
import com.example.jav_projecto1.repository.RoleRepository;
import com.example.jav_projecto1.dto.AccountDTO;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BanMessageService {
    @Autowired
    private BanMessageRepository banMessageRepository;

    @Autowired
    private AccountRepository accountrepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountService accountService;

    public BanMessage createBanMessage(Long accountId, String message) {
        Account account = accountrepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Set the createdAt timestamp
        BanMessage banMessage = BanMessage.builder()
                .account(account)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        return banMessageRepository.save(banMessage);
    }

    public Account banByAccountId(Long accountId, String message) {
        createBanMessage(accountId, message);
        return  accountService.toggleBanStatus(accountId, true);
    }

    @Transactional
    public Account unbanByAccountId(Long accountId) {
        banMessageRepository.deleteByAccount_AccountId(accountId);
        return accountService.toggleBanStatus(accountId, false);
    }

    public Optional<BanMessage> findByAccount_AccountId(Long accountId) {
        return banMessageRepository.findByAccount_AccountId(accountId);
    }

    public List<BanMessageDTO> getAllAccountBanMessageDTOs(List<AccountDTO> accs) {
        return accs.stream()
                .map(acc -> findByAccount_AccountId(acc.getAccountId()))
                .filter(Optional::isPresent)
                .map(acc -> BanMessageService.toDTO(acc.get()))
                .collect(Collectors.toList());
    }

    public static BanMessageDTO toDTO(BanMessage banMessage) {
        if (banMessage == null) {
            return null;
        }
        return BanMessageDTO.builder()
                .accountId(banMessage.getAccount().getAccountId())
                .accountName(banMessage.getAccount().getName())
                .message(banMessage.getMessage())
                .banDate(Timestamp.valueOf(banMessage.getCreatedAt()))
                .isBanned(!banMessage.getAccount().getStatus()) // Assuming status=false means banned
                .build();
    }
}
