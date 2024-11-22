package com.ssafy.edu.controller;

import com.ssafy.edu.review.model.Service.ReviewService;
import com.ssafy.edu.review.model.dto.ReviewImage;
import com.ssafy.edu.user.model.dto.User;
import com.ssafy.edu.user.model.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/file")
@CrossOrigin("*")
public class FileController {
    
    private static final String ERROR_PREFIX = "파일 처리 중 오류 발생: ";
    private final String baseDir = System.getProperty("user.dir") + "/src/main/resources/images";
    private final String profileDir = baseDir + "/profiles/";
    private final String reviewDir = baseDir + "/reviews/";
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ReviewService reviewService;
    
    public FileController() {
        createDirectories();
    }
    
    private void createDirectories() {
        try {
            Files.createDirectories(Paths.get(profileDir));
            Files.createDirectories(Paths.get(reviewDir));
        } catch (Exception e) {
            log.error("디렉토리 생성 실패", e);
        }
    }
    
    @PostMapping(value = "/profile/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadProfileImage(
            @RequestPart("file") MultipartFile file,
            @PathVariable int userId) {
        log.debug("프로필 이미지 업로드 요청. userId: {}", userId);
        
        // 기존 이미지 삭제 로직 수정
        try {
            User user = userService.userDetail(userId);
            if (user.getPImage() != null && !user.getPImage().isEmpty()) {
                // 정확한 파일명 추출
                String filename = user.getPImage().substring(user.getPImage().lastIndexOf("/") + 1);
                Path oldFilePath = Paths.get(profileDir, filename);
                
                // 파일 존재 여부 확인 및 삭제 로그 추가
                if (Files.exists(oldFilePath)) {
                    Files.delete(oldFilePath);
                    log.debug("기존 프로필 이미지 삭제 성공: {}", oldFilePath);
                } else {
                    log.warn("삭제할 프로필 이미지 파일이 존재하지 않음: {}", oldFilePath);
                }
            }
        } catch (Exception e) {
            log.error("기존 이미지 삭제 실패. 경로: {}, 에러: {}", profileDir, e.getMessage());
        }
        
        // 나머지 코드는 동일
        ResponseEntity<Map<String, Object>> uploadResponse = uploadFile(file, profileDir, "profile");
        
        if (uploadResponse.getStatusCode() == HttpStatus.OK) {
            try {
                Map<String, Object> responseBody = uploadResponse.getBody();
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                String savedFilename = (String) data.get("filename");
                
                int result = userService.updateProfileImage(userId, "/images/profiles/" + savedFilename);
                
                if (result > 0) {
                    return uploadResponse;
                } else {
                    return createResponse(false, "DB 업데이트 실패", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } catch (Exception e) {
                log.error("DB 업데이트 실패", e);
                return createResponse(false, "DB 업데이트 실패: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        
        return uploadResponse;
    }
    
    @Operation(summary = "프로필 이미지 삭제", description = "사용자의 프로필 이미지 삭제")
    @DeleteMapping("/profile/user/{userId}")
    public ResponseEntity<Map<String, Object>> deleteProfileImage(@PathVariable int userId) {
        try {
            // 현재 프로필 이미지 정보 조회
            User user = userService.userDetail(userId);
            if (user.getPImage() != null) {
                // 파일 시스템에서 이미지 삭제
                String filename = user.getPImage().substring(user.getPImage().lastIndexOf("/") + 1);
                Path filePath = Paths.get(profileDir + filename);
                Files.deleteIfExists(filePath);
                
                // DB에서 이미지 정보 삭제 (null로 업데이트)
                userService.updateProfileImage(userId, null);
                
                return createResponse(true, "프로필 이미지 삭제 성공", HttpStatus.OK);
            }
            return createResponse(false, "프로필 이미지가 존재하지 않습니다", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("프로필 이미지 삭제 실패", e);
            return createResponse(false, ERROR_PREFIX + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "리뷰 이미지 업로드", description = "리뷰에 첨부될 이미지 업로드 (다중 이미지 지원)")
    @PostMapping(value = "/review/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadReviewImages(
            @RequestPart(value = "files") MultipartFile[] files,
            @PathVariable int reviewId) {
        
        List<String> savedImageUrls = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();
        
        for (MultipartFile file : files) {
            ResponseEntity<Map<String, Object>> uploadResponse = uploadFile(file, reviewDir, "review");
            
            if (uploadResponse.getStatusCode() == HttpStatus.OK) {
                try {
                    Map<String, Object> responseBody = uploadResponse.getBody();
                    Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                    String savedFilename = (String) data.get("filename");
                    String imageUrl = "/images/reviews/" + savedFilename;
                    
                    // 이미지 URL을 DB에 저장
                    ReviewImage reviewImage = new ReviewImage();
                    reviewImage.setReviewId(reviewId);
                    reviewImage.setImageUrl(imageUrl);
                    reviewService.addReviewImage(reviewImage);
                    
                    savedImageUrls.add(imageUrl);
                } catch (Exception e) {
                    failedFiles.add(file.getOriginalFilename());
                    log.error("이미지 저장 실패: " + file.getOriginalFilename(), e);
                }
            } else {
                failedFiles.add(file.getOriginalFilename());
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("savedImages", savedImageUrls);
        if (!failedFiles.isEmpty()) {
            response.put("failedFiles", failedFiles);
        }
        
        return createResponse(true, "이미지 업로드 완료", response, HttpStatus.OK);
    }
    
    @Operation(summary = "리뷰 이미지 삭제", description = "특정 리뷰 이미지 삭제")
    @DeleteMapping("/review/image/{imageId}")
    public ResponseEntity<Map<String, Object>> deleteReviewImage(@PathVariable int imageId) {
        try {
            // 이미지 정보 조회
            ReviewImage image = reviewService.getReviewImageById(imageId);
            if (image != null) {
                // 파일 시스템에서 이미지 삭제
                String filename = image.getImageUrl().substring(image.getImageUrl().lastIndexOf("/") + 1);
                Path filePath = Paths.get(reviewDir + filename);
                Files.deleteIfExists(filePath);
                
                // DB에서 이미지 정보 삭제
                reviewService.deleteReviewImage(imageId);
                
                return createResponse(true, "리뷰 이미지 삭제 성공", HttpStatus.OK);
            }
            return createResponse(false, "리뷰 이미지가 존재하지 않습니다", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("리뷰 이미지 삭제 실패", e);
            return createResponse(false, ERROR_PREFIX + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private ResponseEntity<Map<String, Object>> uploadFile(MultipartFile file, String directory, String type) {
        try {
            log.debug("파일 업로드 시작: {}, 크기: {}", file.getOriginalFilename(), file.getSize());
            
            if (file.isEmpty()) {
                return createResponse(false, "파일이 비어있습니다", HttpStatus.BAD_REQUEST);
            }
            
            if (!file.getContentType().startsWith("image/")) {
                return createResponse(false, "이미지 파일만 업로드 가능합니다", HttpStatus.BAD_REQUEST);
            }
            
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String savedFilename = UUID.randomUUID().toString() + extension;
            
            Path targetPath = Paths.get(directory).resolve(savedFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            log.debug("파일 업로드 성공: {}", savedFilename);
            
            Map<String, Object> data = new HashMap<>();
            data.put("filename", savedFilename);
            return createResponse(true, type + " 이미지 업로드 성공", data, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error(type + " 이미지 업로드 실패", e);
            return createResponse(false, ERROR_PREFIX + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
}