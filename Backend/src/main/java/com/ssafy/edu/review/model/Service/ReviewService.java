package com.ssafy.edu.review.model.Service;

import java.util.List;
import java.util.Map;

import com.ssafy.edu.review.model.dto.Review;
import com.ssafy.edu.review.model.dto.ReviewImage;

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
    
    List<Review> getReviewsByPlace(Map<String, Object> params);
    
    int getPlaceReviewCount(int placeId);
    
    List<Review> searchByTitleInPlace(Map<String, Object> params);
    
    int getTitleSearchCountInPlace(Map<String, Object> params);
    
    List<Review> searchByWriterInPlace(Map<String, Object> params);
    
    int getWriterSearchCountInPlace(Map<String, Object> params);
    
    int addReviewImage(ReviewImage reviewImage);
    
    List<ReviewImage> getReviewImages(int reviewId);
    
    ReviewImage getReviewImageById(int imageId);
    
    int deleteReviewImage(int imageId);
}