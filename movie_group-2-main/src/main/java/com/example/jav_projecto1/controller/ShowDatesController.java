package com.example.jav_projecto1.controller;

import com.example.jav_projecto1.entities.ShowDates;
import com.example.jav_projecto1.dto.ShowDatesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.jav_projecto1.service.ShowDatesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/show-dates")
public class ShowDatesController {
    @Autowired
    private ShowDatesService showDatesService;


    @GetMapping
    public List<ShowDatesDTO> getAll() {
        return showDatesService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowDatesDTO> getById(@PathVariable Integer id) {
        return showDatesService.getById(id);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ShowDates showDates) {
        return showDatesService.create(showDates);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody ShowDates showDates) {
        return showDatesService.update(id, showDates);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return showDatesService.delete(id);
    }
}