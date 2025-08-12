package com.example.jav_projecto1.controller;
import com.example.jav_projecto1.entities.MovieSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.jav_projecto1.dto.MovieScheduleDTO;
import java.util.List;
import com.example.jav_projecto1.service.MovieScheduleService;


@RestController
@RequestMapping("/api/movie-schedules")
public class MovieScheduleController {

    @Autowired
    private MovieScheduleService movieScheduleService;

    @GetMapping
    public List<MovieScheduleDTO> getAll() {
        return movieScheduleService.getAll();
    }

    @GetMapping("/{movieId}/{scheduleId}")
    public ResponseEntity<MovieScheduleDTO> getById(@PathVariable String movieId, @PathVariable Integer scheduleId) {
        return movieScheduleService.getById(movieId, scheduleId);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody MovieSchedule movieSchedule) {
        return movieScheduleService.create(movieSchedule);
    }

    @PutMapping("/{movieId}/{scheduleId}")
    public ResponseEntity<?> update(@PathVariable String movieId, @PathVariable Integer scheduleId, @RequestBody MovieSchedule movieSchedule) {
        return movieScheduleService.update(movieId, scheduleId, movieSchedule);
    }

    @DeleteMapping("/{movieId}/{scheduleId}")
    public ResponseEntity<?> delete(@PathVariable String movieId, @PathVariable Integer scheduleId) {
        return movieScheduleService.delete(movieId, scheduleId);
    }
}