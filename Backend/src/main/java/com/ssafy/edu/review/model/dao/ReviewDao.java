package com.ssafy.edu.review.model.dao;

import com.ssafy.edu.review.model.dto.Review;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReviewDao {
    
    public List<Review> reviewList();
    
    public int addReview(Review review);
    
    public int updateReview(Review review);
    
    public int deleteReview(int reviewId);
    
    public Review reviewDetail(int reviewId);
    
    
}
