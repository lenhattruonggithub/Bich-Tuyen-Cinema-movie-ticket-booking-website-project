package com.example.jav_projecto1.controller;

import com.example.jav_projecto1.entities.MovieDate;
import com.example.jav_projecto1.entities.MovieDateId;
import com.example.jav_projecto1.repository.MovieDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.jav_projecto1.dto.MovieDateDTO;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.text.SimpleDateFormat;
import com.example.jav_projecto1.service.MovieDateService;
import org.springframework.beans.factory.annotation.Autowired;
@RestController
@RequestMapping("/api/movie-dates")
public class MovieDateController {
    @Autowired
    private MovieDateRepository movieDateRepository;
    @Autowired
    private MovieDateService movieDateService;

  @GetMapping
public List<MovieDateDTO> getAll() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    return movieDateRepository.findAll().stream().map(md -> MovieDateDTO.builder()
            .movieId(md.getId().getMovieId())
            .showDateId(md.getId().getShowDateId())
            .movieName(md.getMovie() != null ? md.getMovie().getMovieNameEnglish() : null)
            .dateName(md.getShowDates() != null ? md.getShowDates().getDateName() : null)
            .showDate(md.getShowDates() != null && md.getShowDates().getShowDate() != null
                    ? sdf.format(md.getShowDates().getShowDate()) : null)
            .build()
    ).toList();
}

@GetMapping("/{movieId}/{showDateId}")
public ResponseEntity<MovieDateDTO> getById(@PathVariable String movieId, @PathVariable Integer showDateId) {
    return movieDateService.getById(movieId, showDateId);
}

   @PostMapping
public ResponseEntity<?> create(@RequestBody MovieDate movieDate) {
    return movieDateService.create(movieDate);
}

@PutMapping("/{movieId}/{showDateId}")
public ResponseEntity<?> update(@PathVariable String movieId, @PathVariable Integer showDateId, @RequestBody MovieDate movieDate) {
    return movieDateService.update(movieId, showDateId, movieDate);
}

@DeleteMapping("/{movieId}/{showDateId}")
public ResponseEntity<?> delete(@PathVariable String movieId, @PathVariable Integer showDateId) {
    return movieDateService.delete(movieId, showDateId);
}
}