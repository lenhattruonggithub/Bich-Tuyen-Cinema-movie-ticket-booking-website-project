package com.example.jav_projecto1.repository;

import com.example.jav_projecto1.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.jav_projecto1.enumm.Role_enum;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(Role_enum roleName);
    boolean existsByRoleName(Role_enum roleName);
}