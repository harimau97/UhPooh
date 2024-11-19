package com.ssafy.edu.jwt.dao;

import org.apache.ibatis.annotations.Param;

public interface tokenDao {

  void insertAccessToken(@Param("userId") Long userId, @Param("accessToken") String accessToken);

  void invalidateAccessToken(@Param("userId") Long userId);

  String getAccessTokenByUserId(@Param("userId") Long userId);
}
