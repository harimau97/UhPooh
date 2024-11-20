package com.ssafy.edu.config;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FileConfiguration {

  @Value("${file.path}")
  private String uploadPath;

  @Value("${file.url.default-profile}")
  private String defaultProfileUrl;

  private void createDirectory(String path) {
    File directory = new File(path);
    if (!directory.exists()) {
      directory.mkdirs();
    }
  }

  // @Bean
  public void initializeFileSystem() throws IOException {
    // 프로필 이미지 디렉토리 생성
    String profilePath = uploadPath + "/profile";
    String defaultProfileDir = profilePath + "/default";
    createDirectory(profilePath);
    createDirectory(defaultProfileDir);

    // 기본 프로필 이미지 복사
    Path defaultProfilePath = Paths.get(defaultProfileDir, "default-profile.png");

    if (!Files.exists(defaultProfilePath)) {
      try (InputStream is = getClass().getResourceAsStream("/static/images/default-profile.png")) {
        if (is != null) {
          Files.copy(is, defaultProfilePath, StandardCopyOption.REPLACE_EXISTING);
        }
      } catch (IOException e) {
        // 기본 이미지 복사 실패 시 로그 처리 또는 예외 처리
        System.err.println("Failed to copy default profile image: " + e.getMessage());
        throw e;
      }
    }
  }

  @Bean
  public String defaultProfileUrl() {
    return defaultProfileUrl;
  }

  @Bean
  public String uploadPath() {
    return uploadPath;
  }
}


