package com.example.jav_projecto1.controller;

import com.example.jav_projecto1.entities.Schedule;
import com.example.jav_projecto1.repository.ScheduleRepository;
import com.example.jav_projecto1.dto.ScheduleDTO;
import com.example.jav_projecto1.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private com.example.jav_projecto1.repository.MovieDateRepository movieDateRepository;
    @Autowired
    private com.example.jav_projecto1.repository.MovieScheduleRepository movieScheduleRepository;
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping
    public List<ScheduleDTO> getAll() {
        return scheduleService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> getById(@PathVariable Integer id) {
        return scheduleService.getById(id);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Schedule schedule) {
        return scheduleService.create(schedule);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Schedule schedule) {
        return scheduleService.update(id, schedule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return scheduleService.delete(id);
    }

    @GetMapping("/by-movie-date")
    public ResponseEntity<?> getSchedulesByMovieAndDate(
            @RequestParam String movieId,
            @RequestParam Integer showDateId
    ) {
        return scheduleService.getSchedulesByMovieAndDate(movieId, showDateId);
    }
}