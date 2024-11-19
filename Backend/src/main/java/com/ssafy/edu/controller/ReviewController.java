package com.ssafy.edu.controller;

import com.ssafy.edu.review.model.Service.ReviewService;
import com.ssafy.edu.review.model.dto.Review;
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
    
    @Operation(summary = "리뷰 목록 조회", description = "전체 리뷰 목록 조회")
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
    
    @Operation(summary = "리뷰 상세 조회", description = "특정 리뷰의 상세 정보 조회 (필수: reviewId)")
    @GetMapping("/detail/{reviewId}")
    public ResponseEntity<Map<String, Object>> reviewDetail(@PathVariable int reviewId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Review review = reviewService.reviewDetail(reviewId);
            if (review != null) {
                response.put("success", true);
                response.put("review", review);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("success", false);
                response.put("message", "해당 리뷰를 찾을 수 없습니다.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "리뷰 조회 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "리뷰 작성", description = "리뷰 작성 (필수: userId, placeId, title, content, images)")
    @PostMapping("/write")
    public ResponseEntity<Map<String, Object>> addReview(@RequestBody Review review) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            int result = reviewService.addReview(review);
            if (result > 0) {
                response.put("success", true);
                response.put("message", "리뷰가 작성되었습니다.");
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                response.put("success", false);
                response.put("message", "리뷰 작성에 실패했습니다.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "잘못된 입력값: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "리뷰 작성 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "리뷰 수정", description = "특정 리뷰 정보 수정 (필수: reviewId)")
    @PutMapping("/edit/{reviewId}")
    public ResponseEntity<Map<String, Object>> updateReview(@PathVariable int reviewId, @RequestBody Review review) {
        Map<String, Object> response = new HashMap<>();
        try {
            review.setReviewId(reviewId);
            int result = reviewService.updateReview(review);
            if (result > 0) {
                response.put("success", true);
                response.put("message", "리뷰가 수정되었습니다.");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("success", false);
                response.put("message", "리뷰 수정에 실패했습니다.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "잘못된 입력값: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "리뷰 수정 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "리뷰 삭제", description = "특정 리뷰 삭제 (필수: reviewId)")
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<Map<String, Object>> deleteReview(@PathVariable int reviewId) {
        Map<String, Object> response = new HashMap<>();
        try {
            int result = reviewService.deleteReview(reviewId);
            if (result > 0) {
                response.put("success", true);
                response.put("message", "리뷰가 삭제되었습니다.");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("success", false);
                response.put("message", "리뷰 삭제에 실패했습니다.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "리뷰 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "제목으로 리뷰 검색, 페이징", description = "리뷰 제목으로 검색")
    @GetMapping("/search/title")
    public ResponseEntity<Map<String, Object>> searchByTitle(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("keyword", keyword);
            searchParams.put("start", (page - 1) * size);
            searchParams.put("size", size);
            
            List<Review> reviews = reviewService.searchByTitle(searchParams);
            int totalCount = reviewService.getTitleSearchCount(searchParams);
            
            response.put("success", true);
            response.put("reviews", reviews);
            response.put("currentPage", page);
            response.put("totalItems", totalCount);
            response.put("totalPages", (int) Math.ceil((double) totalCount / size));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "리뷰 검색 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @Operation(summary = "작성자로 리뷰 검색, 페이징", description = "리뷰 작성자로 검색")
    @GetMapping("/search/writer")
    public ResponseEntity<Map<String, Object>> searchByWriter(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("keyword", keyword);
            searchParams.put("start", (page - 1) * size);
            searchParams.put("size", size);
            
            List<Review> reviews = reviewService.searchByWriter(searchParams);
            int totalCount = reviewService.getWriterSearchCount(searchParams);
            
            response.put("success", true);
            response.put("reviews", reviews);
            response.put("currentPage", page);
            response.put("totalItems", totalCount);
            response.put("totalPages", (int) Math.ceil((double) totalCount / size));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "리뷰 검색 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @Operation(summary = "장소별 리뷰 목록 조회, 페이징", description = "특정 장소의 리뷰 목록 조회 (필수: placeId)")
    @GetMapping("/place/{placeId}")
    public ResponseEntity<Map<String, Object>> getReviewsByPlace(
            @PathVariable int placeId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("placeId", placeId);
            params.put("start", (page - 1) * size);
            params.put("size", size);
            
            List<Review> reviews = reviewService.getReviewsByPlace(params);
            int totalCount = reviewService.getPlaceReviewCount(placeId);
            
            response.put("success", true);
            response.put("reviews", reviews);
            response.put("currentPage", page);
            response.put("totalItems", totalCount);
            response.put("totalPages", (int) Math.ceil((double) totalCount / size));
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "장소별 리뷰 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }    
    
}