package com.example.jav_projecto1.service;

import com.example.jav_projecto1.dto.AccountDTO;
import com.example.jav_projecto1.dto.BanMessageDTO;
import com.example.jav_projecto1.entities.Account;
import com.example.jav_projecto1.entities.BanMessage;
import com.example.jav_projecto1.repository.AccountRepository;
import com.example.jav_projecto1.repository.BanMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BanMessageService.
 * Uses Mockito to mock dependencies and JUnit 5 for assertions.
 */
@ExtendWith(MockitoExtension.class)
class BanMessageServiceTest {
    @Mock
    private BanMessageRepository banMessageRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountService accountService;
    @InjectMocks
    private BanMessageService banMessageService;

    private Account account;
    private BanMessage banMessage;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setAccountId(1L);
        account.setName("Test User");
        account.setStatus(false);
        banMessage = BanMessage.builder()
                .account(account)
                .message("Banned for violation")
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Test createBanMessage should save and return BanMessage.
     */
    @Test
    void testCreateBanMessage() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(banMessageRepository.save(any(BanMessage.class))).thenReturn(banMessage);
        BanMessage result = banMessageService.createBanMessage(1L, "Banned for violation");
        assertNotNull(result);
        assertEquals("Banned for violation", result.getMessage());
    }

    /**
     * Test createBanMessage should throw if account not found.
     */
    @Test
    void testCreateBanMessage_AccountNotFound() {
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () ->
                banMessageService.createBanMessage(2L, "msg"));
    }

    /**
     * Test banByAccountId should create ban message and toggle ban status.
     */
    @Test
    void testBanByAccountId() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(banMessageRepository.save(any(BanMessage.class))).thenReturn(banMessage);
        when(accountService.toggleBanStatus(1L, true)).thenReturn(account);
        Account result = banMessageService.banByAccountId(1L, "Banned for violation");
        assertNotNull(result);
        verify(accountService).toggleBanStatus(1L, true);
    }

    /**
     * Test unbanByAccountId should delete ban message and toggle ban status.
     */
    @Test
    void testUnbanByAccountId() {
        doNothing().when(banMessageRepository).deleteByAccount_AccountId(1L);
        when(accountService.toggleBanStatus(1L, false)).thenReturn(account);
        Account result = banMessageService.unbanByAccountId(1L);
        assertNotNull(result);
        verify(banMessageRepository).deleteByAccount_AccountId(1L);
        verify(accountService).toggleBanStatus(1L, false);
    }

    /**
     * Test findByAccount_AccountId should return BanMessage if found.
     */
    @Test
    void testFindByAccount_AccountId_Found() {
        when(banMessageRepository.findByAccount_AccountId(1L)).thenReturn(Optional.of(banMessage));
        Optional<BanMessage> result = banMessageService.findByAccount_AccountId(1L);
        assertTrue(result.isPresent());
        assertEquals("Banned for violation", result.get().getMessage());
    }

    /**
     * Test findByAccount_AccountId should return empty if not found.
     */
    @Test
    void testFindByAccount_AccountId_NotFound() {
        when(banMessageRepository.findByAccount_AccountId(2L)).thenReturn(Optional.empty());
        Optional<BanMessage> result = banMessageService.findByAccount_AccountId(2L);
        assertFalse(result.isPresent());
    }

    /**
     * Test getAllAccountBanMessageDTOs should return list of BanMessageDTOs for accounts with ban messages.
     */
    @Test
    void testGetAllAccountBanMessageDTOs() {
        AccountDTO accDTO = new AccountDTO();
        accDTO.setAccountId(1L);
        List<AccountDTO> accList = List.of(accDTO);
        when(banMessageRepository.findByAccount_AccountId(1L)).thenReturn(Optional.of(banMessage));
        List<BanMessageDTO> result = banMessageService.getAllAccountBanMessageDTOs(accList);
        assertEquals(1, result.size());
        assertEquals("Test User", result.getFirst().getAccountName());
    }

    /**
     * Test toDTO should return null if input is null.
     */
    @Test
    void testToDTO_Null() {
        assertNull(BanMessageService.toDTO(null));
    }

    /**
     * Test toDTO should return BanMessageDTO with correct fields.
     */
    @Test
    void testToDTO_Valid() {
        BanMessageDTO dto = BanMessageService.toDTO(banMessage);
        assertNotNull(dto);
        assertEquals(1L, dto.getAccountId());
        assertEquals("Test User", dto.getAccountName());
        assertEquals("Banned for violation", dto.getMessage());
        assertTrue(dto.getIsBanned()); // account status = false means banned, so isBanned = true
    }
} 