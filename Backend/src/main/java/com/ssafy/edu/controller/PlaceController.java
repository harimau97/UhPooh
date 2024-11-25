package com.ssafy.edu.controller;

import com.ssafy.edu.place.model.dto.Place;
import com.ssafy.edu.place.model.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Place Controller", description = "장소 관련 API")
@RestController
@RequestMapping("/api/place")
public class PlaceController {
    
    private static final Logger logger = LoggerFactory.getLogger(PlaceController.class);
    private static final String ERROR_PREFIX = "오류가 발생했습니다: ";
    
    private final PlaceService placeService;
    
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }
    
    private ResponseEntity<Map<String, Object>> createResponse(boolean success, String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }
    
    private ResponseEntity<Map<String, Object>> createResponse(boolean success, String message, Object data, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        response.put("data", data);
        return new ResponseEntity<>(response, status);
    }
    
    @Operation(summary = "카카오 장소 ID로 조회", description = "카카오 장소 ID로 장소를 조회합니다")
    @GetMapping("/kakao/{kakaoPlaceId}")
    public ResponseEntity<Map<String, Object>> getPlaceByKakaoId(@PathVariable String kakaoPlaceId) {
        logger.info("Retrieving place with kakaoPlaceId: {}", kakaoPlaceId);
        try {
            Place place = placeService.getPlaceByKakaoId(kakaoPlaceId);
            if (place != null) {
                return createResponse(true, "장소 조회 성공", place, HttpStatus.OK);
            } else {
                return createResponse(false, "장소를 찾을 수 없습니다", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in getPlaceByKakaoId: ", e);
            return createResponse(false, ERROR_PREFIX + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "장소 추가", description = "새로운 장소를 추가합니다 (필수: kakaoPlaceId(String), placeName)")
    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> addPlace(@RequestBody Place place) {
        logger.info("Adding new place: {}", place.getKakaoPlaceId());
        try {
            int result = placeService.addPlace(place);
            if (result > 0) {
                return createResponse(true, "장소 추가 성공", place, HttpStatus.CREATED);
            } else {
                return createResponse(false, "장소가 이미 존재합니다", HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            logger.error("Error in addPlace: ", e);
            return createResponse(false, ERROR_PREFIX + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "장소 정보 수정", description = "장소 정보를 수정합니다 (필수: kakaoPlaceId(String), placeName)")
    @PutMapping("/{placeId}")
    public ResponseEntity<Map<String, Object>> updatePlace(@RequestBody Place place) {
        logger.info("Updating place: {}", place.getKakaoPlaceId());
        try {
            int result = placeService.updatePlace(place);
            if (result > 0) {
                return createResponse(true, "장소 정보 수정 성공", place, HttpStatus.OK);
            } else {
                return createResponse(false, "수정할 장소를 찾을 수 없습니다", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in updatePlace: ", e);
            return createResponse(false, ERROR_PREFIX + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "좋아요 추가", description = "장소의 좋아요 수를 증가시킵니다")
    @PutMapping("/like/{placeId}")
    public ResponseEntity<Map<String, Object>> increaseLike(@PathVariable int placeId) {
        logger.info("Increasing like count for place: {}", placeId);
        try {
            int result = placeService.countLike(placeId);
            if (result > 0) {
                return createResponse(true, "좋아요 추가 성공", HttpStatus.OK);
            } else {
                return createResponse(false, "좋아요 추가 실패", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in increaseLike: ", e);
            return createResponse(false, ERROR_PREFIX + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "좋아요 취소", description = "장소의 좋아요 수를 감소시킵니다")
    @PutMapping("/like/cancel/{placeId}")
    public ResponseEntity<Map<String, Object>> decreaseLike(@PathVariable int placeId) {
        logger.info("Decreasing like count for place: {}", placeId);
        try {
            int result = placeService.minusLike(placeId);
            if (result > 0) {
                return createResponse(true, "좋아요 취소 성공", HttpStatus.OK);
            } else {
                return createResponse(false, "좋아요 취소 실패", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in decreaseLike: ", e);
            return createResponse(false, ERROR_PREFIX + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "리뷰 수 업데이트", description = "장소의 리뷰 수를 업데이트합니다")
    @PutMapping("/review-count/{placeId}")
    public ResponseEntity<Map<String, Object>> updateReviewCount(@PathVariable int placeId) {
        logger.info("Updating review count for place: {}", placeId);
        try {
            int result = placeService.updateReviewCount(placeId);
            if (result > 0) {
                return createResponse(true, "리뷰 수 업데이트 성공", HttpStatus.OK);
            } else {
                return createResponse(false, "리뷰 수 업데이트 실패", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in updateReviewCount: ", e);
            return createResponse(false, ERROR_PREFIX + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

@Operation(summary = "userId로 placeId 검색", description = "")
  @GetMapping("/getplaceidbyuserid/{userId}")
  public ResponseEntity<?> getPlaceIdByUserId(@PathVariable int userId) {
    try {
      return new ResponseEntity<>(placeService.getPlaceIdByUserId(userId), HttpStatus.OK);  
    } catch (Exception e) {
      return new ResponseEntity<>("장소 id 목록 반환 실패",HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
  }

  @Operation(summary = "placeId로 place 검색", description = "특정 사용자가 작성한 장소 검색")
  @GetMapping("/getplacebyplaceid/{placeId}")
  public ResponseEntity<?> getPlaceByPlaceId(@PathVariable int placeId) {
    try {
      return new ResponseEntity<>(placeService.getPlaceByPlaceId(placeId), HttpStatus.OK);  
    } catch (Exception e) {
      return new ResponseEntity<>("장소 목록 반환 실패",HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
  }
}
