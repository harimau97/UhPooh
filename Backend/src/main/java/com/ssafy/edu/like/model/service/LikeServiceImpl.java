package com.ssafy.edu.like.model.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ssafy.edu.like.model.dto.Like;
import com.ssafy.edu.like.model.dao.LikeDao;

@Service
public class LikeServiceImpl implements LikeService {

    private final LikeDao likeDao;

    public LikeServiceImpl(LikeDao likeDao) {
        this.likeDao = likeDao;
    }
    
    @Override
    public void addLike(Like like) {
        likeDao.addLike(like);
    }

    @Override
    public void deleteLike(Like like) {
        likeDao.deleteLike(like);
    }

    @Override
    public List<Like> getLikeListByUserId(int userId) {
       return likeDao.getLikeListByUserId(userId);
    }

    @Override
    public List<Like> getLikeListByPlaceId(int placeId) {
        return likeDao.getLikeListByPlaceId(placeId);   
    }

    @Override
    public boolean checkLike(Like like) {
        Like tempLike = likeDao.checkLike(like);
        if(tempLike != null) {
            return true;
        }
        return false;
    }
}
