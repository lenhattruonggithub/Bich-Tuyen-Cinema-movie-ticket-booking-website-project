package com.example.jav_projecto1.controller;

import com.example.jav_projecto1.entities.CinemaRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.example.jav_projecto1.service.CinemaRoomService;
@RestController
@RequestMapping("/api/cinema-rooms")
public class CinemaRoomController {
    @Autowired
    private CinemaRoomService cinemaRoomService;

    @GetMapping
    public List<CinemaRoom> getAllCinemaRooms() {
        return cinemaRoomService.getAllCinemaRooms();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CinemaRoom> getCinemaRoomById(@PathVariable Integer id) {
        return cinemaRoomService.getCinemaRoomById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public CinemaRoom createCinemaRoom(@RequestBody CinemaRoom cinemaRoom) {
        return cinemaRoomService.createCinemaRoom(cinemaRoom);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CinemaRoom> updateCinemaRoom(@PathVariable Integer id, @RequestBody CinemaRoom roomDetails) {
        return cinemaRoomService.updateCinemaRoom(id, roomDetails)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCinemaRoom(@PathVariable Integer id) {
        if (!cinemaRoomService.deleteCinemaRoom(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}