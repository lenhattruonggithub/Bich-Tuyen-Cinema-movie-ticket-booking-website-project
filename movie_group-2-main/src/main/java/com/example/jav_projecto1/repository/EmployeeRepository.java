package com.example.jav_projecto1.repository;

import com.example.jav_projecto1.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jav_projecto1.entities.Employee;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>{
    Optional<Employee> findByAccount(Account account);

    Optional<Employee> findByAccount_AccountId(Long accountAccountId);
}
