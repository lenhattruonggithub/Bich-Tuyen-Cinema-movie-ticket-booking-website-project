package com.example.jav_projecto1.controller;

import com.example.jav_projecto1.dto.AccountBanMessageDTO;
import com.example.jav_projecto1.dto.AccountDTO;
import com.example.jav_projecto1.dto.BanMessageDTO;
import com.example.jav_projecto1.entities.Account;
import com.example.jav_projecto1.service.AccountService;
import com.example.jav_projecto1.service.BanMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member-manage")
public class MemberManageController {

    Logger logger = LoggerFactory.getLogger(MemberManageController.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private BanMessageService banMessageService;

    @GetMapping("/all")
    public AccountBanMessageDTO getAllMembers() {
        List<AccountDTO> accs = accountService.getAllMemberAndBannedAccountDTOs();
        List<BanMessageDTO> banMessageDTOS = banMessageService.getAllAccountBanMessageDTOs(accs);
        return AccountBanMessageDTO.builder().accounts(accs).bans(banMessageDTOS).build();
    }

    @PutMapping("/ban/{accountId}")
    @Transactional
    public ResponseEntity<AccountDTO> toggleBanStatus(@PathVariable Long accountId, @RequestBody(required = false) BanMessageDTO banMessageDTO) {
        logger.info("Toggling ban status for account ID: {}, message: {}", accountId, banMessageDTO != null ? banMessageDTO.getMessage() : "none");
        try {
            Account updatedAccount;
            if (banMessageDTO != null && banMessageDTO.getMessage() != null && !banMessageDTO.getMessage().trim().isEmpty()) {
                updatedAccount = banMessageService.banByAccountId(accountId, banMessageDTO.getMessage());
            } else {
                updatedAccount = banMessageService.unbanByAccountId(accountId);
            }
            AccountDTO updatedAccountDTO = AccountService.toDTO(updatedAccount); // Assuming convertToDTO exists
            return ResponseEntity.ok(updatedAccountDTO);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid account ID: {}", accountId, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error toggling ban status for account ID: {}", accountId, e);
            return ResponseEntity.status(500).build();
        }
    }
}