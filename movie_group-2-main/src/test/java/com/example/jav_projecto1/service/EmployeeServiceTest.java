package com.example.jav_projecto1.service;

import com.example.jav_projecto1.dto.EmployeeDTO;
import com.example.jav_projecto1.dto.RegisterRequest;
import com.example.jav_projecto1.entities.Account;
import com.example.jav_projecto1.entities.Employee;
import com.example.jav_projecto1.enumm.Role_enum;
import com.example.jav_projecto1.repository.AccountRepository;
import com.example.jav_projecto1.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EmployeeService.
 * Uses Mockito to mock dependencies and JUnit 5 for assertions.
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @Mock private EmployeeRepository employeeRepository;
    @Mock private AccountService accountService;
    @Mock private AccountRepository accountrepository;
    @Mock private HttpSession session;
    @InjectMocks private EmployeeService employeeService;

    private Employee employee;
    private Account account;
    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setAccountId(1L);
        account.setUsername("user1");
        employee = Employee.builder().employeeId(1L).account(account).build();
        request = new RegisterRequest();
        request.setUsername("user1");
        request.setPassword("pass");
        request.setEmail("test@example.com");
    }

    /**
     * Test getAllEmployees should return list of EmployeeDTO.
     */
    @Test
    void testGetAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        List<EmployeeDTO> result = employeeService.getAllEmployees();
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getEmployeeId());
    }

    /**
     * Test getEmployeeById should return EmployeeDTO if found.
     */
    @Test
    void testGetEmployeeById_Found() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        Optional<EmployeeDTO> result = employeeService.getEmployeeById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getEmployeeId());
    }

    /**
     * Test getEmployeeById should return empty if not found.
     */
    @Test
    void testGetEmployeeById_NotFound() {
        when(employeeRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<EmployeeDTO> result = employeeService.getEmployeeById(2L);
        assertFalse(result.isPresent());
    }

    /**
     * Test createEmployee should save and return EmployeeDTO.
     */
    @Test
    void testCreateEmployee() {
        when(accountService.saveByRequest(any(RegisterRequest.class), eq(Role_enum.EMPLOYEE))).thenReturn(account);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        EmployeeDTO result = employeeService.createEmployee(request);
        assertNotNull(result);
        assertEquals(1L, result.getEmployeeId());
    }

    /**
     * Test updateEmployee should update and return EmployeeDTO if found.
     */
    @Test
    void testUpdateEmployee_Found() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(accountrepository.findByUsername(anyString())).thenReturn(Optional.of(account));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        Optional<EmployeeDTO> result = employeeService.updateEmployee(1L, request);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getEmployeeId());
    }

    /**
     * Test updateEmployee should return empty if not found.
     */
    @Test
    void testUpdateEmployee_NotFound() {
        when(employeeRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<EmployeeDTO> result = employeeService.updateEmployee(2L, request);
        assertFalse(result.isPresent());
    }

    /**
     * Test deleteEmployee should delete and return true if found.
     */
    @Test
    void testDeleteEmployee_Found() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        doNothing().when(accountService).delete(account);
        boolean result = employeeService.deleteEmployee(1L);
        assertTrue(result);
        verify(accountService).delete(account);
    }

    /**
     * Test deleteEmployee should return false if not found.
     */
    @Test
    void testDeleteEmployee_NotFound() {
        when(employeeRepository.findById(2L)).thenReturn(Optional.empty());
        boolean result = employeeService.deleteEmployee(2L);
        assertFalse(result);
    }

    /**
     * Test isAdmin should return true if session has admin account.
     */
    @Test
    void testIsAdmin_True() {
        Account admin = new Account();
        admin.setRole(new com.example.jav_projecto1.entities.Role());
        admin.getRole().setRoleName(Role_enum.ADMIN);
        when(session.getAttribute("userLogin")).thenReturn(admin);
        boolean result = employeeService.isAdmin(session);
        assertTrue(result);
    }

    /**
     * Test isAdmin should return false if session has non-admin account or null.
     */
    @Test
    void testIsAdmin_False() {
        when(session.getAttribute("userLogin")).thenReturn(null);
        boolean result = employeeService.isAdmin(session);
        assertFalse(result);
    }
} 