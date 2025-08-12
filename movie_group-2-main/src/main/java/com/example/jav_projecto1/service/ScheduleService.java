package com.example.jav_projecto1.service;

import com.example.jav_projecto1.entities.Schedule;
import com.example.jav_projecto1.repository.ScheduleRepository;
import com.example.jav_projecto1.dto.ScheduleDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final com.example.jav_projecto1.repository.MovieDateRepository movieDateRepository;
    private final com.example.jav_projecto1.repository.MovieScheduleRepository movieScheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository,
                          com.example.jav_projecto1.repository.MovieDateRepository movieDateRepository,
                          com.example.jav_projecto1.repository.MovieScheduleRepository movieScheduleRepository) {
        this.scheduleRepository = scheduleRepository;
        this.movieDateRepository = movieDateRepository;
        this.movieScheduleRepository = movieScheduleRepository;
    }

    public List<ScheduleDTO> getAll() {
        return scheduleRepository.findAll().stream().map(sc -> ScheduleDTO.builder()
                .scheduleId(sc.getScheduleId())
                .scheduleTime(sc.getScheduleTime())
                .build()
        ).toList();
    }

    public ResponseEntity<ScheduleDTO> getById(Integer id) {
        Optional<Schedule> sc = scheduleRepository.findById(id);
        return sc.map(s -> ResponseEntity.ok(
                ScheduleDTO.builder()
                        .scheduleId(s.getScheduleId())
                        .scheduleTime(s.getScheduleTime())
                        .build()
        )).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> create(Schedule schedule) {
        if (schedule == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Dữ liệu không hợp lệ"));
        }
        Schedule saved = scheduleRepository.save(schedule);
        ScheduleDTO dto = ScheduleDTO.builder()
                .scheduleId(saved.getScheduleId())
                .scheduleTime(saved.getScheduleTime())
                .build();
        return ResponseEntity.status(201).body(Map.of("message", "Tạo Schedule thành công", "data", dto));
    }

    public ResponseEntity<?> update(Integer id, Schedule schedule) {
        if (!scheduleRepository.existsById(id)) {
            return ResponseEntity.status(404).body(Map.of("message", "Không tìm thấy Schedule"));
        }
        schedule.setScheduleId(id);
        Schedule updated = scheduleRepository.save(schedule);
        ScheduleDTO dto = ScheduleDTO.builder()
                .scheduleId(updated.getScheduleId())
                .scheduleTime(updated.getScheduleTime())
                .build();
        return ResponseEntity.ok(Map.of("message", "Cập nhật Schedule thành công", "data", dto));
    }

    public ResponseEntity<?> delete(Integer id) {
        if (!scheduleRepository.existsById(id)) {
            return ResponseEntity.status(404).body(Map.of("message", "Không tìm thấy Schedule"));
        }
        scheduleRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Xóa Schedule thành công"));
    }

    public ResponseEntity<?> getSchedulesByMovieAndDate(String movieId, Integer showDateId) {
        Optional<com.example.jav_projecto1.entities.MovieDate> movieDateOpt =
                movieDateRepository.findById(new com.example.jav_projecto1.entities.MovieDateId(movieId, showDateId));
        if (movieDateOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không tìm thấy movie-date"));
        }
        List<com.example.jav_projecto1.entities.MovieSchedule> movieSchedules =
                movieScheduleRepository.findAll().stream()
                        .filter(ms -> ms.getMovie().getMovieId().equals(movieId))
                        .toList();

        List<ScheduleDTO> result = movieSchedules.stream()
                .map(ms -> ScheduleDTO.builder()
                        .scheduleId(ms.getSchedule().getScheduleId())
                        .scheduleTime(ms.getSchedule().getScheduleTime())
                        .build())
                .toList();

        return ResponseEntity.ok(result);
    }
} 