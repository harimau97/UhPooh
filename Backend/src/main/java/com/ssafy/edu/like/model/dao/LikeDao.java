package com.ssafy.edu.like.model.dao;

import java.util.List;
import java.util.Map;

import com.ssafy.edu.like.model.dto.Like;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface LikeDao {
    void addLike(Like like);

    void deleteLike(Like like);

    List<Like> getLikeListByUserId(int userId);
    
    List<Like> getLikeListByPlaceId(int placeId);

    Like checkLike(Like like);
}
