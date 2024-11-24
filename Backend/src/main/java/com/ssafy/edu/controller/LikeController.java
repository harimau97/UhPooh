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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ssafy.edu.like.model.dto.Like;
import com.ssafy.edu.like.model.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/like")
@CrossOrigin("*")
public class LikeController {

  private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);
  private static final int DEFAULT_PAGE_SIZE = 10;
  private static final String ERROR_PREFIX = "오류가 발생했습니다: ";

  private final LikeService likeService;

  public LikeController(LikeService likeService) {
    this.likeService = likeService;
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

  @Operation(summary = "좋아요 추가", description = "")
  @PostMapping("/addlike")
  public ResponseEntity<?> addLike(@RequestBody Like like) {
    try {
      likeService.addLike(like);
      return new ResponseEntity<>("좋아요 추가 성공", HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>("좋아요 추가 실패", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "좋아요 삭제", description = "")
  @DeleteMapping("/deletelike")
  public ResponseEntity<?> deleteMethodName(@RequestBody Like like) {
    try {
      likeService.deleteLike(like);
      return new ResponseEntity<>("좋아요 삭제 성공", HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>("좋아요 삭제 실패", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "userid로 좋아요 검색", description = "")
  @GetMapping("/getlikebyuserid/{userId}")
  public ResponseEntity<?> getLikeByUserId(@PathVariable int userId) {
    try {
      return new ResponseEntity<>(likeService.getLikeListByUserId(userId), HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>("좋아요 검색(userid) 반환 실패", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "placeid로 좋아요 검색", description = "")
  @GetMapping("/getlikebyplaceid/{placeId}")
  public ResponseEntity<?> getLikeByPlaceId(@PathVariable int placeId) {
    try {
      return new ResponseEntity<>(likeService.getLikeListByPlaceId(placeId), HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>("좋아요 검색(placeid) 반환 실패", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "좋아요 중복 체크", description = "")
  @GetMapping("/checklike")
  public ResponseEntity<?> checkLike(@RequestParam("userId") int userId, @RequestParam("placeId") int placeId) {
    Like tempLike = new Like();
    tempLike.setUserId(userId);
    tempLike.setPlaceId(placeId);
    try {
      return new ResponseEntity<>(likeService.checkLike(tempLike), HttpStatus.OK); //이미 좋아요 눌렀음
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("실패");
      return new ResponseEntity<>("좋아요 추가 가능", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
}
