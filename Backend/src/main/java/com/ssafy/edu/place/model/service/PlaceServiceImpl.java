package com.ssafy.edu.place.model.service;

import com.ssafy.edu.place.model.dao.PlaceDao;
import com.ssafy.edu.place.model.dto.Place;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlaceServiceImpl implements PlaceService{
    
    private final PlaceDao placeDao;
    
    public PlaceServiceImpl(PlaceDao placeDao) {
        this.placeDao = placeDao;
    }
    
    @Override
    @Transactional
    public Place getPlaceByKakaoId(String kakaoPlaceId) {
        return placeDao.getPlaceByKakaoId(kakaoPlaceId);
    }
    
    @Override
    @Transactional
    public int addPlace(Place place) {
        return placeDao.addPlace(place);
    }
    
    @Override
    @Transactional
    public int updatePlace(Place place) {
        return placeDao.updatePlace(place);
    }
    
    @Override
    @Transactional
    public int countLike(int placeId) {
        return placeDao.countLike(placeId);
    }
    
    @Override
    @Transactional
    public int minusLike(int placeId) {
        return placeDao.minusLike(placeId);
    }
    
    @Override
    @Transactional
    public int updateReviewCount(int placeId) {
        return placeDao.updateReviewCount(placeId);
    }
}
