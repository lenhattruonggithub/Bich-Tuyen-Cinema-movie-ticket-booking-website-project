package com.example.jav_projecto1.service;

import com.example.jav_projecto1.dto.EmployeeDTO;
import com.example.jav_projecto1.dto.RegisterRequest;
import com.example.jav_projecto1.entities.Account;
import com.example.jav_projecto1.entities.Employee;
import com.example.jav_projecto1.enumm.Role_enum;
import com.example.jav_projecto1.repository.AccountRepository;
import com.example.jav_projecto1.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpSession;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountrepository;

    private EmployeeDTO mapToDTO(Employee emp) {
        if (emp == null) {
            return null;
        }
        Account account = emp.getAccount();
        return EmployeeDTO.builder()
                .employeeId(emp.getEmployeeId())
                .account(AccountService.toDTO(account))
                .build();
    }

    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public Optional<EmployeeDTO> getEmployeeById(Long id) {
        return employeeRepository.findById(id).map(this::mapToDTO);
    }


    @Transactional
    public EmployeeDTO createEmployee(RegisterRequest request) {
        Account account = accountService.saveByRequest(request, Role_enum.EMPLOYEE);

        Employee newEmployee = Employee.builder().account(account).build();
        Employee savedEmployee = employeeRepository.save(newEmployee);
        return mapToDTO(savedEmployee);
    }


    @Transactional
    public Optional<EmployeeDTO> updateEmployee(Long id, RegisterRequest request) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    Account account = employee.getAccount();
                    updateAccountFromRequest(account, request);
                    Employee updatedEmployee = employeeRepository.save(employee);
                    return mapToDTO(updatedEmployee);
                });
    }


    private void updateAccountFromRequest(Account account, RegisterRequest request) {
        if (accountrepository.findByUsername(request.getUsername()).isPresent()
                && !account.getUsername().equals(request.getUsername())) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken!");
        }

        if (request.getUsername() != null) account.setUsername(request.getUsername());
        if (request.getPassword() != null) account.setPassword(request.getPassword()); // Consider password encoding
        if (request.getEmail() != null) account.setEmail(request.getEmail());
        if (request.getName() != null) account.setName(request.getName());
        if (request.getBirthday() != null) account.setBirthday(request.getBirthday());
        if (request.getGender() != null) account.setGender(request.getGender());
        if (request.getIdentityCard() != null) account.setIdentityCard(request.getIdentityCard());
        if (request.getPhoneNumber() != null) account.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) account.setAddress(request.getAddress());
    }

    @Transactional
    public boolean deleteEmployee(Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isEmpty()) {
            return false;
        }
        accountService.delete(employee.get().getAccount());
        return true;
    }

    public boolean isAdmin(HttpSession session) {
        Account acc = (Account) session.getAttribute("userLogin");
        return acc != null && acc.getRole() != null && Role_enum.ADMIN.equals(acc.getRole().getRoleName());
    }
}
