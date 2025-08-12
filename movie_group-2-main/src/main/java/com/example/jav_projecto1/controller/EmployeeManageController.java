package com.example.jav_projecto1.controller;

import com.example.jav_projecto1.dto.EmployeeDTO;
import com.example.jav_projecto1.dto.RegisterRequest;
import com.example.jav_projecto1.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/employees")
public class EmployeeManageController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeManageController.class);
    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAll(HttpSession session) {
        if (!employeeService.isAdmin(session)) {
            return ResponseEntity.status(403).build();
        }
        List<EmployeeDTO> dtos = employeeService.getAllEmployees();
        logger.info("Retrieved {} employees", dtos.size());
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getById(@PathVariable Long id, HttpSession session) {
        if (!employeeService.isAdmin(session)) {
            return ResponseEntity.status(403).build();
        }
        return employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<EmployeeDTO> create(@RequestBody RegisterRequest request, HttpSession session) {
        if (!employeeService.isAdmin(session)) {
            return ResponseEntity.status(403).build();
        }
        EmployeeDTO createdEmployee = employeeService.createEmployee(request);
        return ResponseEntity.ok(createdEmployee);
    }


    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> update(@PathVariable Long id, @RequestBody RegisterRequest request,
                                              HttpSession session) {
        if (!employeeService.isAdmin(session)) {
            return ResponseEntity.status(403).build();
        }
        return employeeService.updateEmployee(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpSession session) {
        if (!employeeService.isAdmin(session)) {
            return ResponseEntity.status(403).build();
        }
        if (employeeService.deleteEmployee(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
