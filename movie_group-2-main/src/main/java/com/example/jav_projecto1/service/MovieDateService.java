package com.example.jav_projecto1.service;

import com.example.jav_projecto1.entities.MovieDate;
import com.example.jav_projecto1.entities.MovieDateId;
import com.example.jav_projecto1.repository.MovieDateRepository;
import com.example.jav_projecto1.dto.MovieDateDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.text.SimpleDateFormat;

@Service
public class MovieDateService {
    private final MovieDateRepository movieDateRepository;

    public MovieDateService(MovieDateRepository movieDateRepository) {
        this.movieDateRepository = movieDateRepository;
    }

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

    public ResponseEntity<MovieDateDTO> getById(String movieId, Integer showDateId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        MovieDateId id = new MovieDateId(movieId, showDateId);
        Optional<MovieDate> md = movieDateRepository.findById(id);
        return md.map(m -> ResponseEntity.ok(
                MovieDateDTO.builder()
                        .movieId(m.getId().getMovieId())
                        .showDateId(m.getId().getShowDateId())
                        .movieName(m.getMovie() != null ? m.getMovie().getMovieNameEnglish() : null)
                        .dateName(m.getShowDates() != null ? m.getShowDates().getDateName() : null)
                        .showDate(m.getShowDates() != null && m.getShowDates().getShowDate() != null
                                ? sdf.format(m.getShowDates().getShowDate()) : null)
                        .build()
        )).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> create(MovieDate movieDate) {
        if (movieDate == null || movieDate.getId() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Dữ liệu không hợp lệ"));
        }
        MovieDate saved = movieDateRepository.save(movieDate);
        return ResponseEntity.status(201).body(Map.of("message", "Tạo MovieDate thành công", "data", saved));
    }

    public ResponseEntity<?> update(String movieId, Integer showDateId, MovieDate movieDate) {
        MovieDateId id = new MovieDateId(movieId, showDateId);
        if (!movieDateRepository.existsById(id)) {
            return ResponseEntity.status(404).body(Map.of("message", "Không tìm thấy MovieDate"));
        }
        movieDate.setId(id);
        MovieDate updated = movieDateRepository.save(movieDate);
        return ResponseEntity.ok(Map.of("message", "Cập nhật MovieDate thành công", "data", updated));
    }

    public ResponseEntity<?> delete(String movieId, Integer showDateId) {
        MovieDateId id = new MovieDateId(movieId, showDateId);
        if (!movieDateRepository.existsById(id)) {
            return ResponseEntity.status(404).body(Map.of("message", "Không tìm thấy MovieDate"));
        }
        movieDateRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Xóa MovieDate thành công"));
    }
} 