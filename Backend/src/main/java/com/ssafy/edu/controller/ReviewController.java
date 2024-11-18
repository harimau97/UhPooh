package com.ssafy.edu.controller;

import com.ssafy.edu.review.model.Service.ReviewService;
import com.ssafy.edu.review.model.dto.Review;
import com.ssafy.edu.user.model.dto.User;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/review")
@CrossOrigin("*")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "리뷰 목록 조회", description = "전체 리뷰 목록을 조회합니다")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> reviewList() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Review> reviews = reviewService.reviewList();
            response.put("success", true);
            response.put("reviews", reviews);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "리뷰 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
