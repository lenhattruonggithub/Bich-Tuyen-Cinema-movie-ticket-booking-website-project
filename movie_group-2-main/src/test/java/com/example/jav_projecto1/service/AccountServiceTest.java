package com.example.jav_projecto1.service;

import com.example.jav_projecto1.dto.RegisterRequest;
import com.example.jav_projecto1.enumm.Role_enum;
import com.example.jav_projecto1.entities.Account;
import com.example.jav_projecto1.entities.Role;
import com.example.jav_projecto1.repository.AccountRepository;
import com.example.jav_projecto1.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AccountService.
 * Uses Mockito to mock dependencies and JUnit 5 for assertions.
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    // Mocked dependencies
    @Mock
    private AccountRepository accRepository;
    @Mock
    private RoleRepository roleRepository;
    // Inject mocks into AccountService
    @InjectMocks
    private AccountService accountService;

    private Account account;
    private RegisterRequest registerRequest;
    private Role role;

    /**
     * Prepare common test data before each test.
     */
    @BeforeEach
    void setUp() {
        // Build a sample Account entity
        account = Account.builder()
                .accountId(1L)
                .username("testuser")
                .password("password")
                .email("test@example.com")
                .name("Test User")
                .birthday(LocalDate.of(2000, 1, 1))
                .gender(com.example.jav_projecto1.enumm.Gender.MALE)
                .identityCard("123456789")
                .phoneNumber("0123456789")
                .address("Test Address")
                .registerDate(LocalDate.now())
                .status(true)
                .build();
        // Build a sample RegisterRequest DTO
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password");
        registerRequest.setEmail("test@example.com");
        registerRequest.setName("Test User");
        registerRequest.setBirthday(LocalDate.of(2000, 1, 1));
        registerRequest.setGender(com.example.jav_projecto1.enumm.Gender.MALE);
        registerRequest.setIdentityCard("123456789");
        registerRequest.setPhoneNumber("0123456789");
        registerRequest.setAddress("Test Address");
        // Build a sample Role entity
        role = new Role();
        role.setRoleName(Role_enum.MEMBER);
    }

    /**
     * Test saving an account using the save() method.
     * Should return the saved account.
     */
    @Test
    void testSave() {
        when(accRepository.save(any(Account.class))).thenReturn(account);
        Account saved = accountService.save(account);
        assertNotNull(saved);
        assertEquals("testuser", saved.getUsername());
    }

    /**
     * Test saving an account by RegisterRequest when username is available.
     * Should return the saved account.
     */
    @Test
    void testSaveByRequest_Success() {
        when(accRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(roleRepository.findByRoleName(any(Role_enum.class))).thenReturn(role);
        when(accRepository.save(any(Account.class))).thenReturn(account);
        Account saved = accountService.saveByRequest(registerRequest, Role_enum.MEMBER);
        assertNotNull(saved);
        assertEquals("testuser", saved.getUsername());
    }

    /**
     * Test saving an account by RegisterRequest when username is already taken.
     * Should throw IllegalArgumentException.
     */
    @Test
    void testSaveByRequest_UsernameTaken() {
        when(accRepository.findByUsername(anyString())).thenReturn(Optional.of(account));
        assertThrows(IllegalArgumentException.class, () ->
                accountService.saveByRequest(registerRequest, Role_enum.MEMBER));
    }

    /**
     * Test login with correct username and password.
     * Should return the account.
     */
    @Test
    void testLogin_Success() {
        when(accRepository.findByUsername("testuser")).thenReturn(Optional.of(account));
        Optional<Account> result = accountService.login("testuser", "password");
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    /**
     * Test login with non-existing username.
     * Should return empty Optional.
     */
    @Test
    void testLogin_Fail() {
        when(accRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        Optional<Account> result = accountService.login("testuser", "password");
        assertFalse(result.isPresent());
    }

    /**
     * Test registering a new user with available username.
     * Should return the saved account.
     */
    @Test
    void testRegisterUser_Success() {
        when(accRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(accRepository.save(any(Account.class))).thenReturn(account);
        Account saved = accountService.registerUser(account);
        assertNotNull(saved);
        assertEquals("testuser", saved.getUsername());
    }

    /**
     * Test registering a new user with a taken username.
     * Should throw IllegalArgumentException.
     */
    @Test
    void testRegisterUser_UsernameTaken() {
        when(accRepository.findByUsername(anyString())).thenReturn(Optional.of(account));
        assertThrows(IllegalArgumentException.class, () ->
                accountService.registerUser(account));
    }

    /**
     * Test getting all member accounts.
     * Should return a list containing the test account.
     */
    @Test
    void testGetAllMemberAccounts() {
        when(accRepository.findByRole_RoleName(Role_enum.MEMBER)).thenReturn(List.of(account));
        List<Account> result = accountService.getAllMemberAccounts();
        assertEquals(1, result.size());
        assertEquals("testuser", result.getFirst().getUsername());
    }
}