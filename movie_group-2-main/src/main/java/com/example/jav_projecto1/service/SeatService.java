package com.example.jav_projecto1.service;

import com.example.jav_projecto1.entities.Seat;
import com.example.jav_projecto1.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeatService {
    @Autowired
    private SeatRepository seatRepository;

    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    public Optional<Seat> getSeatById(Long id) {
        return seatRepository.findById(id);
    }

    public Seat createSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    public Optional<Seat> updateSeat(Long id, Seat seatDetails) {
        Optional<Seat> seatOpt = seatRepository.findById(id);
        if (seatOpt.isEmpty()) {
            return Optional.empty();
        }
        Seat seat = seatOpt.get();
        seat.setRowLabel(seatDetails.getRowLabel());
        seat.setSeatNumber(seatDetails.getSeatNumber());
        seat.setSeatCode(seatDetails.getSeatCode());
        seat.setSeatType(seatDetails.getSeatType());
        seat.setStatus(seatDetails.getStatus());
        seat.setCinemaRoom(seatDetails.getCinemaRoom());
        return Optional.of(seatRepository.save(seat));
    }

    public boolean deleteSeat(Long id) {
        if (!seatRepository.existsById(id)) {
            return false;
        }
        seatRepository.deleteById(id);
        return true;
    }
         public List<Seat> getSeatsByCinemaRoomId(Integer cinemaRoomId) {
        return seatRepository.findByCinemaRoom_CinemaRoomId(cinemaRoomId);
    }

    public void unbindSeatFromCinemaRoom(Integer id) {
        List<Seat> seats = seatRepository.findByCinemaRoom_CinemaRoomId(id);
        for (Seat seat : seats) {
            seat.setCinemaRoom(null);
            seatRepository.save(seat);
        }
    }
}