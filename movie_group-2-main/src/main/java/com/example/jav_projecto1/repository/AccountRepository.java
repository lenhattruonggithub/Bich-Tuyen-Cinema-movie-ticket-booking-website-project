package com.example.jav_projecto1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jav_projecto1.entities.Account;
import com.example.jav_projecto1.entities.Role;
import com.example.jav_projecto1.enumm.Role_enum;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsernameAndPassword(String username, String password);
    Optional<Account> findByUsername(String username);
    List<Account> findByRole_RoleName(Role_enum roleName);
    List<Account> findByRole_RoleNameIn(List<Role_enum> roles);
    Optional<Account> findByEmail(String email);
}
