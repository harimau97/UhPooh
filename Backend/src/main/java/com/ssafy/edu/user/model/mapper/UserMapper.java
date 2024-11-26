package com.ssafy.edu.user.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("SELECT userId FROM users WHERE username = #{username}")
    int getUserIdByUsername(String username);
}
