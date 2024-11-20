package com.ssafy.edu.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import java.util.HashMap;
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
    
    @Operation(summary = "프로필 이미지 업로드", description = "사용자의 프로필 이미지 업로드 (필수: userId, file)")
    @PostMapping(value = "/profile/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadProfileImage(
            @RequestPart("file") MultipartFile file,
            @PathVariable int userId) {
        log.debug("프로필 이미지 업로드 요청. userId: {}", userId);
        return uploadFile(file, profileDir, "profile");
    }
    
    @Operation(summary = "리뷰 이미지 업로드", description = "리뷰에 첨부될 이미지 업로드 (필수: reviewId, file)")
    @PostMapping(value = "/review/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadReviewImage(
            @RequestPart(value = "file", required = true) MultipartFile file,
            @PathVariable int reviewId) {
        log.info("리뷰 이미지 업로드 시작: filename={}, contentType={}",
                file.getOriginalFilename(),
                file.getContentType());
        
        try {
            if (file.isEmpty()) {
                return createResponse(false, "파일이 비어있습니다", HttpStatus.BAD_REQUEST);
            }
            
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String savedFilename = UUID.randomUUID().toString() + extension;
            
            Path targetPath = Paths.get(reviewDir).resolve(savedFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("파일 업로드 성공: {}", savedFilename);
            
            Map<String, Object> data = new HashMap<>();
            data.put("filename", savedFilename);
            return createResponse(true, "리뷰 이미지 업로드 성공", data, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("파일 업로드 실패", e);
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
    
    @Operation(summary = "파일 삭제", description = "프로필 또는 리뷰 이미지 삭제 (필수: type, filename)")
    @DeleteMapping("/{type}/{filename}")
    public ResponseEntity<Map<String, Object>> deleteFile(
            @PathVariable String type,
            @PathVariable String filename) {
        String directory = type.equals("profile") ? profileDir : reviewDir;
        
        try {
            Path filePath = Paths.get(directory + filename);
            boolean deleted = Files.deleteIfExists(filePath);
            
            if (deleted) {
                return createResponse(true, "파일 삭제 성공", HttpStatus.OK);
            } else {
                return createResponse(false, "파일이 존재하지 않습니다", HttpStatus.NOT_FOUND);
            }
            
        } catch (Exception e) {
            log.error("파일 삭제 실패", e);
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