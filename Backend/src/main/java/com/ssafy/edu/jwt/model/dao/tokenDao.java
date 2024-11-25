package com.ssafy.edu.jwt.model.dao;

import org.apache.ibatis.annotations.Param;

public interface tokenDao {

  void insertAccessToken(@Param("userId") Long userId, @Param("accessToken") String accessToken);

  void invalidateAccessToken(@Param("userId") Long userId);

  String getAccessTokenByUserId(@Param("userId") Long userId);

  String provideToken(@Param("userId") Long userId);

  String getToken(@Param("accessToken") String accessToken, @Param("userId") Long userId);

  // void clearToken(@Param("userId") Long userId);
}
