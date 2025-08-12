package com.example.jav_projecto1.config;

import com.example.jav_projecto1.enumm.Role_enum;
import com.example.jav_projecto1.entities.Role;
import com.example.jav_projecto1.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AutoAddRoleCmdRunner implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        for (Role_enum roleEnum : Role_enum.values()) {
            boolean exists = roleRepository.existsByRoleName(roleEnum);
            if (!exists) {
                Role role = new Role();
                role.setRoleName(roleEnum);
                roleRepository.save(role);
                System.out.println("Added role: " + roleEnum);
            }
        }
    }
}
