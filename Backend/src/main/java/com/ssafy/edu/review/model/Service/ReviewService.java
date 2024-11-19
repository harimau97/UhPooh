package com.ssafy.edu.review.model.Service;

import com.ssafy.edu.review.model.dto.Review;

import java.util.List;
import java.util.Map;

public interface ReviewService {
    
    List<Review> reviewList();
    
    int addReview(Review review);
    
    int updateReview(Review review);
    
    int deleteReview(int reviewId);
    
    Review reviewDetail(int reviewId);
    
    List<Review> searchByTitle(Map<String, Object> params);
    
    int getTitleSearchCount(Map<String, Object> params);
    
    List<Review> searchByWriter(Map<String, Object> params);
    
    int getWriterSearchCount(Map<String, Object> params);
}
