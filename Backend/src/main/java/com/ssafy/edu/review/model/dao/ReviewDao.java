package com.ssafy.edu.review.model.dao;

import com.ssafy.edu.review.model.dto.Review;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReviewDao {
    
    public List<Review> reviewList();
    
    public int addReview(Review review);
    
    public int updateReview(Review review);
    
    public int deleteReview(int reviewId);
    
    public Review reviewDetail(int reviewId);
    
    public List<Review> searchByTitle(Map<String, Object> params);
    
    public int getTitleSearchCount(Map<String, Object> params);
    
    public List<Review> searchByWriter(Map<String, Object> params);
    
    public int getWriterSearchCount(Map<String, Object> params);
    
    public List<Review> getReviewsByPlace(Map<String, Object> params);
    
    public int getPlaceReviewCount(int placeId);
    
    
}
