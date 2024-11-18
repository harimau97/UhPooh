package com.ssafy.edu.review.model.Service;

import com.ssafy.edu.review.model.dto.Review;

import java.util.List;

public interface ReviewService {
    
    List<Review> reviewList();
    
    int addReview(Review review);
    
    int updateReview(Review review);
    
    int deleteReview(int reviewId);
    
    Review reviewDetail(int reviewId);
}
