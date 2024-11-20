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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ssafy.edu.jwt.service.CustomUserService;
import com.ssafy.edu.user.model.dto.User;
import com.ssafy.edu.user.model.service.UserService;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
public class UserController {
  private static final Logger logger = LoggerFactory.getLogger(UserController.class);
  private static final int DEFAULT_PAGE_SIZE = 10;
  private static final String ADMIN_ONLY_MESSAGE = "관리자만 접근할 수 있습니다.";
  private static final String SELF_ACCESS_ONLY_MESSAGE = "자신의 정보만 접근할 수 있습니다.";

  private final UserService userService;
  private final CustomUserService customUserService;

  public UserController(CustomUserService customUserService, UserService userService) {
    this.customUserService = customUserService;
    this.userService = userService;
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

  @Operation(summary = "전체 사용자 목록 조회", description = "관리자만 전체 사용자 목록 조회 가능")
  @GetMapping("/list")
  public ResponseEntity<Map<String, Object>> userList(@RequestParam int requestUserId) {
    logger.info("Requesting user list by userId: {}", requestUserId);
    try {
      User requestUser = userService.userDetail(requestUserId);
      if (requestUser == null || requestUser.getIsAdmin() == 0) {
        return createResponse(false, ADMIN_ONLY_MESSAGE, HttpStatus.FORBIDDEN);
      }

      List<User> users = userService.userList();
      return createResponse(true, "사용자 목록 조회 성공", users, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Error in userList: ", e);
      return createResponse(false, "사용자 목록 조회 중 오류가 발생했습니다: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "회원 상세 조회",
      description = "회원 상세 정보 조회 (일반 회원은 자신의 정보만, 관리자는 모든 회원 정보 조회 가능)")
  @GetMapping("/{userId}")
  public ResponseEntity<Map<String, Object>> userDetail(@PathVariable int userId,
      @RequestParam int requestUserId) {
    logger.info("Requesting user detail - userId: {}, requestUserId: {}", userId, requestUserId);
    try {
      User requestUser = userService.userDetail(requestUserId);
      if (requestUser == null) {
        return createResponse(false, "유효하지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED);
      }

      if (requestUser.getIsAdmin() == 0 && requestUserId != userId) {
        return createResponse(false, SELF_ACCESS_ONLY_MESSAGE, HttpStatus.FORBIDDEN);
      }

      User user = userService.userDetail(userId);
      if (user != null) {
        return createResponse(true, "회원 정보 조회 성공", user, HttpStatus.OK);
      } else {
        return createResponse(false, "해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      logger.error("Error in userDetail: ", e);
      return createResponse(false, "회원 정보 조회 중 오류가 발생했습니다: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인 (필수: userEmail, password)")
  @PostMapping("/login")
  public ResponseEntity<Map<String, Object>> userLogin(@RequestBody User user) {
    logger.info("Login attempt for user email: {}", user.getUserEmail());
    try {
      User loginUser = userService.userLogin(user);
      if (loginUser != null) {
        loginUser.setIsLogin(1);
        userService.updateLoginStatus(loginUser);
        customUserService.clearToken(loginUser.getUserId());
        customUserService.loginUser((long) loginUser.getUserId());
        String userToken = customUserService.tokenProvider(loginUser.getUserId());
        System.out.println(userToken.toString());
        // 토큰을 포함하기 위해 수정함
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("message", "로그인에 성공했습니다.");
        responseData.put("user", loginUser);
        responseData.put("userToken", userToken);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
      } else {
        return createResponse(false, "이메일 또는 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
      }
    } catch (Exception e) {
      logger.error("Error in userLogin: ", e);
      return createResponse(false, "로그인 중 오류가 발생했습니다: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "회원가입", description = "새로운 사용자 등록 (필수: userEmail, password, userName, userAddress)")
  @PostMapping("/signup")
  public ResponseEntity<Map<String, Object>> userSignup(@RequestBody User user) {
    logger.info("Signup attempt for user email: {}", user.getUserEmail());
    try {
      int result = userService.userSignup(user);
      if (result > 0) {
        return createResponse(true, "회원가입이 완료되었습니다.", HttpStatus.CREATED);
      } else {
        return createResponse(false, "회원가입에 실패했습니다.", HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      logger.error("Error in userSignup: ", e);
      return createResponse(false, "회원가입 중 오류가 발생했습니다: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "회원정보 수정", description = "사용자 정보 수정 (일반 회원은 자신의 정보만, 관리자는 모든 회원 정보 수정 가능)")
  @PatchMapping("/{userId}")
  public ResponseEntity<Map<String, Object>> userUpdate(@PathVariable int userId,
      @RequestBody User user, @RequestParam int requestUserId) {
    logger.info("Update attempt for userId: {}, requestUserId: {}", userId, requestUserId);
    try {
      User requestUser = userService.userDetail(requestUserId);
      if (requestUser == null) {
        return createResponse(false, "유효하지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED);
      }

      if (requestUser.getIsAdmin() == 0 && requestUserId != userId) {
        return createResponse(false, SELF_ACCESS_ONLY_MESSAGE, HttpStatus.FORBIDDEN);
      }

      user.setUserId(userId);
      int result = userService.userUpdate(user);

      if (result > 0) {
        return createResponse(true, "회원정보가 수정되었습니다.", HttpStatus.OK);
      } else {
        return createResponse(false, "회원정보 수정에 실패했습니다.", HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      logger.error("Error in userUpdate: ", e);
      return createResponse(false, "회원정보 수정 중 오류가 발생했습니다: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "이메일 중복체크", description = "이메일 중복 확인 (필수: userEmail)")
  @GetMapping("/check/email/{userEmail}")
  public ResponseEntity<Map<String, Object>> userIdCheck(@PathVariable String userEmail) {
    logger.info("Email check for: {}", userEmail);
    try {
      int result = userService.userIdCheck(userEmail);
      return createResponse(true, result == 0 ? "사용 가능한 이메일입니다." : "이미 사용중인 이메일입니다.",
          Map.of("available", result == 0), result == 0 ? HttpStatus.OK : HttpStatus.CONFLICT);
    } catch (Exception e) {
      logger.error("Error in userIdCheck: ", e);
      return createResponse(false, "이메일 중복 확인 중 오류가 발생했습니다: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "닉네임 중복체크", description = "닉네임 중복 확인 (필수: userName)")
  @GetMapping("/check/name/{userName}")
  public ResponseEntity<Map<String, Object>> userNameCheck(@PathVariable String userName) {
    logger.info("Username check for: {}", userName);
    try {
      int result = userService.userNameCheck(userName);
      return createResponse(true, result == 0 ? "사용 가능한 닉네임입니다." : "이미 사용중인 닉네임입니다.",
          Map.of("available", result == 0), result == 0 ? HttpStatus.OK : HttpStatus.CONFLICT);
    } catch (Exception e) {
      logger.error("Error in userNameCheck: ", e);
      return createResponse(false, "닉네임 중복 확인 중 오류가 발생했습니다: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "비밀번호 수정", description = "비밀번호 변경 (관리자가 변경 불가능) (필수: currentPassword, newPassword, confirmPassword)")
  @PatchMapping("/password/{userId}")
  public ResponseEntity<Map<String, Object>> updatePassword(@PathVariable int userId,
      @RequestBody Map<String, String> request, @RequestParam int requestUserId) {
    logger.info("Password update attempt for userId: {}", userId);
    try {
      User requestUser = userService.userDetail(requestUserId);
      if (requestUser == null) {
        return createResponse(false, "유효하지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED);
      }

      if (requestUserId != userId) {
        return createResponse(false, SELF_ACCESS_ONLY_MESSAGE, HttpStatus.FORBIDDEN);
      }

      if (!request.containsKey("currentPassword") || !request.containsKey("newPassword")
          || !request.containsKey("confirmPassword")) {
        return createResponse(false, "현재 비밀번호, 새로운 비밀번호, 비밀번호 확인이 모두 필요합니다.",
            HttpStatus.BAD_REQUEST);
      }

      User checkUser = new User();
      checkUser.setUserId(userId);
      checkUser.setPassword(request.get("currentPassword"));

      if (!userService.checkPassword(checkUser)) {
        return createResponse(false, "현재 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
      }

      String newPassword = request.get("newPassword");
      String confirmPassword = request.get("confirmPassword");

      if (!newPassword.equals(confirmPassword)) {
        return createResponse(false, "새로운 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
      }

      User user = new User();
      user.setUserId(userId);
      user.setPassword(newPassword);

      int result = userService.updatePassword(user);

      if (result > 0) {
        return createResponse(true, "비밀번호가 성공적으로 변경되었습니다.", HttpStatus.OK);
      } else {
        return createResponse(false, "비밀번호 변경에 실패했습니다.", HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      logger.error("Error in updatePassword: ", e);
      return createResponse(false, "비밀번호 수정 중 오류가 발생했습니다: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "로그아웃", description = "사용자 로그아웃 처리 (필수: userId)")
  @PostMapping("/logout/{userId}")
  public ResponseEntity<Map<String, Object>> userLogout(@PathVariable int userId) {
    logger.info("Logout attempt for userId: {}", userId);
    try {
      User user = new User();
      user.setUserId(userId);
      user.setIsLogin(0);

      int result = userService.updateLoginStatus(user);

      if (result > 0) {
        customUserService.logoutUser((long) userId);
        return createResponse(true, "로그아웃 되었습니다.", HttpStatus.OK);
      } else {
        return createResponse(false, "로그아웃에 실패했습니다.", HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      logger.error("Error in userLogout: ", e);
      return createResponse(false, "로그아웃 처리 중 오류가 발생했습니다: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "회원 탈퇴", description = "회원 정보 삭제 (일반 회원은 자신만, 관리자는 모든 회원 삭제 가능)")
  @DeleteMapping("/{userId}")
  public ResponseEntity<Map<String, Object>> userDelete(@PathVariable int userId,
      @RequestParam int requestUserId) {
    logger.info("Delete attempt for userId: {}, requestUserId: {}", userId, requestUserId);
    try {
      User requestUser = userService.userDetail(requestUserId);
      if (requestUser == null) {
        return createResponse(false, "유효하지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED);
      }

      if (requestUser.getIsAdmin() == 0 && requestUserId != userId) {
        return createResponse(false, "자신의 계정만 삭제할 수 있습니다.", HttpStatus.FORBIDDEN);
      }

      int result = userService.userDelete(userId);

      if (result > 0) {
        return createResponse(true, "회원 탈퇴가 완료되었습니다.", HttpStatus.OK);
      } else {
        return createResponse(false, "존재하지 않는 회원입니다.", HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      logger.error("Error in userDelete: ", e);
      return createResponse(false, "회원 탈퇴 처리 중 오류가 발생했습니다: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "이메일로 회원 검색", description = "이메일로 회원 검색")
  @GetMapping("/search/email")
  public ResponseEntity<Map<String, Object>> searchByEmail(@RequestParam String keyword,
      @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
      @RequestParam int requestUserId) {
    logger.info("Search by email - keyword: {}, page: {}, size: {}", keyword, page, size);
    try {
      User requestUser = userService.userDetail(requestUserId);
      if (requestUser == null || requestUser.getIsAdmin() == 0) {
        return createResponse(false, ADMIN_ONLY_MESSAGE, HttpStatus.FORBIDDEN);
      }

      Map<String, Object> searchParams = new HashMap<>();
      searchParams.put("keyword", keyword);
      searchParams.put("start", (page - 1) * size);
      searchParams.put("size", size);

      List<User> users = userService.searchByEmail(searchParams);
      int totalCount = userService.getEmailSearchCount(searchParams);

      Map<String, Object> data = new HashMap<>();
      data.put("users", users);
      data.put("currentPage", page);
      data.put("totalItems", totalCount);
      data.put("totalPages", (int) Math.ceil((double) totalCount / size));

      return createResponse(true, "검색 성공", data, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Error in searchByEmail: ", e);
      return createResponse(false, "회원 검색 중 오류가 발생했습니다: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "닉네임으로 회원 검색", description = "닉네임으로 회원 검색")
  @GetMapping("/search/name")
  public ResponseEntity<Map<String, Object>> searchByName(@RequestParam String keyword,
      @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
      @RequestParam int requestUserId) {
    logger.info("Search by name - keyword: {}, page: {}, size: {}", keyword, page, size);
    try {
      User requestUser = userService.userDetail(requestUserId);
      if (requestUser == null || requestUser.getIsAdmin() == 0) {
        return createResponse(false, ADMIN_ONLY_MESSAGE, HttpStatus.FORBIDDEN);
      }

      Map<String, Object> searchParams = new HashMap<>();
      searchParams.put("keyword", keyword);
      searchParams.put("start", (page - 1) * size);
      searchParams.put("size", size);

      List<User> users = userService.searchByName(searchParams);
      int totalCount = userService.getNameSearchCount(searchParams);

      Map<String, Object> data = new HashMap<>();
      data.put("users", users);
      data.put("currentPage", page);
      data.put("totalItems", totalCount);
      data.put("totalPages", (int) Math.ceil((double) totalCount / size));

      return createResponse(true, "검색 성공", data, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Error in searchByName: ", e);
      return createResponse(false, "회원 검색 중 오류가 발생했습니다: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "관리자 여부로 회원 검색", description = "관리자 여부로 회원 검색")
  @GetMapping("/search/admin")
  public ResponseEntity<Map<String, Object>> searchByAdminStatus(@RequestParam int isAdmin,
      @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
      @RequestParam int requestUserId) {
    logger.info("Search by admin status - isAdmin: {}, page: {}, size: {}", isAdmin, page, size);
    try {
      User requestUser = userService.userDetail(requestUserId);
      if (requestUser == null || requestUser.getIsAdmin() == 0) {
        return createResponse(false, ADMIN_ONLY_MESSAGE, HttpStatus.FORBIDDEN);
      }

      Map<String, Object> searchParams = new HashMap<>();
      searchParams.put("isAdmin", isAdmin);
      searchParams.put("start", (page - 1) * size);
      searchParams.put("size", size);

      List<User> users = userService.searchByAdminStatus(searchParams);
      int totalCount = userService.getAdminSearchCount(searchParams);

      Map<String, Object> data = new HashMap<>();
      data.put("users", users);
      data.put("currentPage", page);
      data.put("totalItems", totalCount);
      data.put("totalPages", (int) Math.ceil((double) totalCount / size));

      return createResponse(true, "검색 성공", data, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Error in searchByAdminStatus: ", e);
      return createResponse(false, "회원 검색 중 오류가 발생했습니다: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "토큰 검증", description = "JWT 토큰의 유효성 검사")
  @GetMapping("/validate")
  public boolean validate(@RequestParam String token, @RequestParam int userId) {
    logger.info("Token validation request received");
    if (customUserService.getToken(token, userId)) {
      return customUserService.validateAccessToken(token);
    } else {
      return false;
    }

  }


  @Operation(summary = "protected 엔드포인트", description = "인증된 사용자만 접근 가능한 테스트용 보호 엔드포인트")
  @GetMapping("/protected")
  public ResponseEntity<String> protectedEndpoint() {
    logger.info("Protected endpoint accessed");
    return ResponseEntity.ok("You have accessed a protected endpoint!");
  }

  // @Operation(summary = "token provider", description = "사용자에게 accesstoken을 부여합니다.")
  // @GetMapping("/provideToken")
  // public ResponseEntity<?> tokenprovider(@RequestBody ) {
  // logger.info("토큰 전달 완료");
  // customUserService.tokenProvider();
  // return new ResponseEntity<>("Success", HttpStatus.OK);
  // }

}
