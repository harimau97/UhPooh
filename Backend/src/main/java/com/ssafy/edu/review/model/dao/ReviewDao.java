package com.ssafy.edu.review.model.dao;

import java.util.List;
import java.util.Map;

import com.ssafy.edu.review.model.dto.ReviewImage;
import org.apache.ibatis.annotations.Mapper;
import com.ssafy.edu.review.model.dto.Review;

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
    
    public int getReviewCountByUserId(int userId);
    
    public int getTitleSearchCountInPlace(Map<String, Object> params);
    
    public List<Review> searchByTitleInPlace(Map<String, Object> params);
    
    public int getWriterSearchCountInPlace(Map<String, Object> params);
    
    public List<Review> searchByWriterInPlace(Map<String, Object> params);

    public Review getReviewIdByTitleAndContent(Review review);
    
    public int addReviewImage(ReviewImage reviewImage);
    
    public List<ReviewImage> getReviewImages(int reviewId);
    
    public int deleteReviewImage(int imageId);
    
    public ReviewImage getReviewImageById(int imageId);

    
}
