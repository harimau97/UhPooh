package com.ssafy.edu.place.model.dao;

import com.ssafy.edu.place.model.dto.Place;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PlaceDao {
    
    public Place getPlaceByKakaoId(String kakaoPlaceId);
    
    public int addPlace(Place place);
    
    public int updatePlace(Place place);
    
    public int countLike(int placeId);
    
    public int minusLike(int placeId);
    
    public int updateReviewCount(int placeId);

    public int[] getPlaceIdByUserId(int userId);

    public Place getPlaceByPlaceId(int placeId);
    
}
