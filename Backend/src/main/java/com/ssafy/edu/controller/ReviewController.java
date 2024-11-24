package com.ssafy.edu.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ssafy.edu.review.model.Service.ReviewService;
import com.ssafy.edu.review.model.dto.Review;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/review")
@CrossOrigin("*")
public class ReviewController {

  private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);
  private static final int DEFAULT_PAGE_SIZE = 10;
  private static final String ERROR_PREFIX = "오류가 발생했습니다: ";

  private final ReviewService reviewService;

  public ReviewController(ReviewService reviewService) {
    this.reviewService = reviewService;
  }

  private ResponseEntity<Map<String, Object>> createResponse(boolean success, String message,
      HttpStatus status) {
    Map<String, Object> response = new HashMap<>();
    response.put("success", success);
    response.put("message", message);
    return new ResponseEntity<>(response, status);
  }

  private ResponseEntity<Map<String, Object>> createResponse(boolean success, String message,
      Object data, HttpStatus status) {
    Map<String, Object> response = new HashMap<>();
    response.put("success", success);
    response.put("message", message);
    response.put("data", data);
    return new ResponseEntity<>(response, status);
  }

  private Map<String, Object> createPageResponse(List<?> items, int page, int totalCount,
      int size) {
    Map<String, Object> data = new HashMap<>();
    data.put("items", items);
    data.put("currentPage", page);
    data.put("totalItems", totalCount);
    data.put("totalPages", (int) Math.ceil((double) totalCount / size));
    return data;
  }

  @Operation(summary = "리뷰 목록 조회", description = "전체 리뷰 목록 조회")
  @GetMapping("/list")
  public ResponseEntity<Map<String, Object>> reviewList() {
    logger.info("Requesting review list");
    try {
      List<Review> reviews = reviewService.reviewList();
      return createResponse(true, "리뷰 목록 조회 성공", reviews, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Error in reviewList: ", e);
      return createResponse(false, ERROR_PREFIX + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "리뷰 상세 조회", description = "특정 리뷰의 상세 정보 조회 (필수: reviewId)")
  @GetMapping("/detail/{reviewId}")
  public ResponseEntity<Map<String, Object>> reviewDetail(@PathVariable int reviewId) {
    logger.info("Requesting review detail - reviewId: {}", reviewId);
    try {
      Review review = reviewService.reviewDetail(reviewId);
      if (review != null) {
        return createResponse(true, "리뷰 조회 성공", review, HttpStatus.OK);
      } else {
        return createResponse(false, "해당 리뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      logger.error("Error in reviewDetail: ", e);
      return createResponse(false, ERROR_PREFIX + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "리뷰 작성", description = "리뷰 작성 (필수: userId, placeId, title, content, images)")
  @PostMapping("/write")
  public ResponseEntity<Map<String, Object>> addReview(@RequestBody Review review) {
    logger.info("Adding new review for userId: {}, placeId: {}", review.getUserId(),
        review.getPlaceId());
    try {
      int result = reviewService.addReview(review);
      if (result > 0) {
        Review newReview = reviewService.getReviewIdByTitleAndContent(review);
        if (newReview != null) {
          Map<String, Object> data = new HashMap<>();
          data.put("reviewId", newReview.getReviewId());
          return createResponse(true, "리뷰가 작성되었습니다.", data, HttpStatus.CREATED);
        } else {
          return createResponse(false, "리뷰 ID를 찾을 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
      } else {
        return createResponse(false, "리뷰 작성에 실패했습니다.", HttpStatus.BAD_REQUEST);
      }
    } catch (IllegalArgumentException e) {
      logger.error("Invalid input in addReview: ", e);
      return createResponse(false, "잘못된 입력값: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Error in addReview: ", e);
      return createResponse(false, ERROR_PREFIX + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "리뷰 수정", description = "특정 리뷰 정보 수정 (필수: reviewId)")
  @PutMapping("/edit/{reviewId}")
  public ResponseEntity<Map<String, Object>> updateReview(@PathVariable int reviewId,
      @RequestBody Review review) {
    logger.info("Updating review - reviewId: {}", reviewId);
    try {
      review.setReviewId(reviewId);
      int result = reviewService.updateReview(review);
      if (result > 0) {
        return createResponse(true, "리뷰가 수정되었습니다.", HttpStatus.OK);
      } else {
        return createResponse(false, "리뷰 수정에 실패했습니다.", HttpStatus.BAD_REQUEST);
      }
    } catch (IllegalArgumentException e) {
      logger.error("Invalid input in updateReview: ", e);
      return createResponse(false, "잘못된 입력값: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Error in updateReview: ", e);
      return createResponse(false, ERROR_PREFIX + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "리뷰 삭제", description = "특정 리뷰 삭제 (필수: reviewId)")
  @DeleteMapping("/delete/{reviewId}")
  public ResponseEntity<Map<String, Object>> deleteReview(@PathVariable int reviewId) {
    logger.info("Deleting review - reviewId: {}", reviewId);
    try {
      int result = reviewService.deleteReview(reviewId);
      if (result > 0) {
        return createResponse(true, "리뷰가 삭제되었습니다.", HttpStatus.OK);
      } else {
        return createResponse(false, "리뷰 삭제에 실패했습니다.", HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      logger.error("Error in deleteReview: ", e);
      return createResponse(false, ERROR_PREFIX + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "제목으로 리뷰 검색, 페이징", description = "리뷰 제목으로 검색")
  @GetMapping("/search/title")
  public ResponseEntity<Map<String, Object>> searchByTitle(@RequestParam String keyword,
      @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
    logger.info("Searching reviews by title - keyword: {}, page: {}, size: {}", keyword, page,
        size);
    try {
      Map<String, Object> searchParams = new HashMap<>();
      searchParams.put("keyword", keyword);
      searchParams.put("start", (page - 1) * size);
      searchParams.put("size", size);

      List<Review> reviews = reviewService.searchByTitle(searchParams);
      int totalCount = reviewService.getTitleSearchCount(searchParams);

      Map<String, Object> pageData = createPageResponse(reviews, page, totalCount, size);
      return createResponse(true, "검색 성공", pageData, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Error in searchByTitle: ", e);
      return createResponse(false, ERROR_PREFIX + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "작성자로 리뷰 검색, 페이징", description = "리뷰 작성자로 검색")
  @GetMapping("/search/writer")
  public ResponseEntity<Map<String, Object>> searchByWriter(@RequestParam String keyword,
      @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
    logger.info("Searching reviews by writer - keyword: {}, page: {}, size: {}", keyword, page,
        size);
    try {
      Map<String, Object> searchParams = new HashMap<>();
      searchParams.put("keyword", keyword);
      searchParams.put("start", (page - 1) * size);
      searchParams.put("size", size);

      List<Review> reviews = reviewService.searchByWriter(searchParams);
      int totalCount = reviewService.getWriterSearchCount(searchParams);

      Map<String, Object> pageData = createPageResponse(reviews, page, totalCount, size);
      return createResponse(true, "검색 성공", pageData, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Error in searchByWriter: ", e);
      return createResponse(false, ERROR_PREFIX + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "장소별 리뷰 목록 조회, 페이징", description = "특정 장소의 리뷰 목록 조회 (필수: placeId)")
  @GetMapping("/place/{placeId}")
  public ResponseEntity<Map<String, Object>> getReviewsByPlace(@PathVariable int placeId,
      @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
    logger.info("Getting reviews by place - placeId: {}, page: {}, size: {}", placeId, page, size);
    try {
      Map<String, Object> params = new HashMap<>();
      params.put("placeId", placeId);
      params.put("start", (page - 1) * size);
      params.put("size", size);

      List<Review> reviews = reviewService.getReviewsByPlace(params);
      int totalCount = reviewService.getPlaceReviewCount(placeId);

      Map<String, Object> pageData = createPageResponse(reviews, page, totalCount, size);
      return createResponse(true, "장소별 리뷰 조회 성공", pageData, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Error in getReviewsByPlace: ", e);
      return createResponse(false, ERROR_PREFIX + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "장소별 작성자로 리뷰 검색, 페이징", description = "특정 장소의 리뷰를 작성자로 검색")
  @GetMapping("/place/search/writer/{placeId}")
  public ResponseEntity<Map<String, Object>> searchByWriterInPlace(@PathVariable int placeId,
      @RequestParam String keyword, @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    logger.info(
        "Searching reviews by writer in place - placeId: {}, keyword: {}, page: {}, size: {}",
        placeId, keyword, page, size);
    try {
      Map<String, Object> searchParams = new HashMap<>();
      searchParams.put("placeId", placeId);
      searchParams.put("keyword", keyword);
      searchParams.put("start", (page - 1) * size);
      searchParams.put("size", size);

      List<Review> reviews = reviewService.searchByWriterInPlace(searchParams);
      int totalCount = reviewService.getWriterSearchCountInPlace(searchParams);

      Map<String, Object> pageData = createPageResponse(reviews, page, totalCount, size);
      return createResponse(true, "장소별 작성자 검색 성공", pageData, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Error in searchByWriterInPlace: ", e);
      return createResponse(false, ERROR_PREFIX + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "장소별 제목으로 리뷰 검색, 페이징", description = "특정 장소의 리뷰를 제목으로 검색")
  @GetMapping("/place/search/title/{placeId}")
  public ResponseEntity<Map<String, Object>> searchByTitleInPlace(@PathVariable int placeId,
      @RequestParam String keyword, @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    logger.info(
        "Searching reviews by title in place - placeId: {}, keyword: {}, page: {}, size: {}",
        placeId, keyword, page, size);
    try {
      Map<String, Object> searchParams = new HashMap<>();
      searchParams.put("placeId", placeId);
      searchParams.put("keyword", keyword);
      searchParams.put("start", (page - 1) * size);
      searchParams.put("size", size);

      List<Review> reviews = reviewService.searchByTitleInPlace(searchParams);
      int totalCount = reviewService.getTitleSearchCountInPlace(searchParams);

      Map<String, Object> pageData = createPageResponse(reviews, page, totalCount, size);
      return createResponse(true, "장소별 제목 검색 성공", pageData, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Error in searchByTitleInPlace: ", e);
      return createResponse(false, ERROR_PREFIX + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "reviewId로 이미지 검색", description = "특정 리뷰에 속한 이미지 검색")
  @GetMapping("/reviewimages/{reviewId}")
  public ResponseEntity<?> getRevieimagesByReviewId(@PathVariable int reviewId) {
    logger.info("Getting review images by reviewId");
    
    try {
      return new ResponseEntity<>(reviewService.getReviewImages(reviewId), HttpStatus.OK);  
    } catch (Exception e) {
      return new ResponseEntity<>("이미지 목록 반환 실패",HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  

}
