package com.example.jav_projecto1.controller;

import com.example.jav_projecto1.dto.ReviewRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.jav_projecto1.service.ReviewService;


@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Gửi đánh giá phim
    @PostMapping
    public ResponseEntity<?> submitReview(@RequestBody ReviewRequest req) {
        return reviewService.submitReview(req);
    }

    // Xem điểm trung bình của phim
  @GetMapping("/average")
    public ResponseEntity<?> getAverageRating(@RequestParam String movieId) {
        return reviewService.getAverageRating(movieId);
    }
    // Lấy tất cả đánh giá của phim
   @GetMapping
    public ResponseEntity<?> getReviewsByMovie(@RequestParam String movieId) {
        return reviewService.getReviewsByMovie(movieId);
    }


    // Sửa đánh giá phim
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewRequest req) {
        return reviewService.updateReview(reviewId, req);
    }

    // Xóa đánh giá phim
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        return reviewService.deleteReview(reviewId);
    }
}