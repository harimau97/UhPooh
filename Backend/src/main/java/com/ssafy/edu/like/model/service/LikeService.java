package com.ssafy.edu.like.model.service;

import java.util.List;


import com.ssafy.edu.like.model.dto.Like;

public interface LikeService {
    void addLike(Like like);
    void deleteLike(Like like);
    List<Like> getLikeListByUserId(int userId);
    List<Like> getLikeListByPlaceId(int placeId);
    boolean checkLike(Like like);

}