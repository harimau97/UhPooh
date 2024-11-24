package com.ssafy.edu.place.model.service;

import com.ssafy.edu.place.model.dto.Place;

public interface PlaceService {
    
    Place getPlaceByKakaoId(String kakaoPlaceId);
    
    int addPlace(Place place);
    
    int updatePlace(Place place);
    
    int countLike(int placeId);
    
    int minusLike(int placeId);
    
    int updateReviewCount(int placeId);

    int[] getPlaceIdByUserId(int userId);

    Place getPlaceByPlaceId(int placeId);
}
