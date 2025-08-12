package com.example.jav_projecto1.service;

import com.example.jav_projecto1.entities.MovieSchedule;
import com.example.jav_projecto1.entities.MovieScheduleId;
import com.example.jav_projecto1.repository.MovieScheduleRepository;
import com.example.jav_projecto1.dto.MovieScheduleDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MovieScheduleService {
    private final MovieScheduleRepository movieScheduleRepository;

    public MovieScheduleService(MovieScheduleRepository movieScheduleRepository) {
        this.movieScheduleRepository = movieScheduleRepository;
    }

    public List<MovieScheduleDTO> getAll() {
        return movieScheduleRepository.findAll().stream().map(ms -> MovieScheduleDTO.builder()
                .movieId(ms.getId().getMovieId())
                .scheduleId(ms.getId().getScheduleId())
                .movieName(ms.getMovie() != null ? ms.getMovie().getMovieNameEnglish() : null)
                .scheduleTime(ms.getSchedule() != null ? ms.getSchedule().getScheduleTime() : null)
                .build()
        ).toList();
    }

    public ResponseEntity<MovieScheduleDTO> getById(String movieId, Integer scheduleId) {
        MovieScheduleId id = new MovieScheduleId(movieId, scheduleId);
        Optional<MovieSchedule> ms = movieScheduleRepository.findById(id);
        return ms.map(m -> ResponseEntity.ok(
                MovieScheduleDTO.builder()
                        .movieId(m.getId().getMovieId())
                        .scheduleId(m.getId().getScheduleId())
                        .movieName(m.getMovie() != null ? m.getMovie().getMovieNameEnglish() : null)
                        .scheduleTime(m.getSchedule() != null ? m.getSchedule().getScheduleTime() : null)
                        .build()
        )).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> create(MovieSchedule movieSchedule) {
        if (movieSchedule == null || movieSchedule.getId() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Dữ liệu không hợp lệ"));
        }
        MovieSchedule saved = movieScheduleRepository.save(movieSchedule);
        MovieScheduleDTO dto = MovieScheduleDTO.builder()
                .movieId(saved.getId().getMovieId())
                .scheduleId(saved.getId().getScheduleId())
                .movieName(saved.getMovie() != null ? saved.getMovie().getMovieNameEnglish() : null)
                .scheduleTime(saved.getSchedule() != null ? saved.getSchedule().getScheduleTime() : null)
                .build();
        return ResponseEntity.status(201).body(Map.of("message", "Tạo MovieSchedule thành công", "data", dto));
    }

    public ResponseEntity<?> update(String movieId, Integer scheduleId, MovieSchedule movieSchedule) {
        MovieScheduleId id = new MovieScheduleId(movieId, scheduleId);
        if (!movieScheduleRepository.existsById(id)) {
            return ResponseEntity.status(404).body(Map.of("message", "Không tìm thấy MovieSchedule"));
        }
        movieSchedule.setId(id);
        MovieSchedule updated = movieScheduleRepository.save(movieSchedule);
        MovieScheduleDTO dto = MovieScheduleDTO.builder()
                .movieId(updated.getId().getMovieId())
                .scheduleId(updated.getId().getScheduleId())
                .movieName(updated.getMovie() != null ? updated.getMovie().getMovieNameEnglish() : null)
                .scheduleTime(updated.getSchedule() != null ? updated.getSchedule().getScheduleTime() : null)
                .build();
        return ResponseEntity.ok(Map.of("message", "Cập nhật MovieSchedule thành công", "data", dto));
    }

    public ResponseEntity<?> delete(String movieId, Integer scheduleId) {
        MovieScheduleId id = new MovieScheduleId(movieId, scheduleId);
        if (!movieScheduleRepository.existsById(id)) {
            return ResponseEntity.status(404).body(Map.of("message", "Không tìm thấy MovieSchedule"));
        }
        movieScheduleRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Xóa MovieSchedule thành công"));
    }
} 