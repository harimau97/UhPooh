package com.ssafy.edu.review.model.Service;

import java.util.List;
import java.util.Map;

import com.ssafy.edu.review.model.dto.ReviewImage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ssafy.edu.review.model.dao.ReviewDao;
import com.ssafy.edu.review.model.dto.Review;

@Service
public class ReviewServiceImpl implements ReviewService {
    
    private final ReviewDao reviewDao;
    
    public ReviewServiceImpl(ReviewDao reviewDao) {
        this.reviewDao = reviewDao;
    }
    
    @Override
    public List<Review> reviewList() {
        return reviewDao.reviewList();
    }
    
    @Override
    public int addReview(Review review) {
        return reviewDao.addReview(review);
    }
    
    @Override
    public int updateReview(Review review) {
        return reviewDao.updateReview(review);
    }
    
    @Override
    public int deleteReview(int reviewId) {
        return reviewDao.deleteReview(reviewId);
    }
    
    @Override
    public Review reviewDetail(int reviewId) {
        return reviewDao.reviewDetail(reviewId);
    }
    
    @Override
    public List<Review> searchByTitle(Map<String, Object> params) {
        return reviewDao.searchByTitle(params);
    }
    
    @Override
    public int getTitleSearchCount(Map<String, Object> params) {
        return reviewDao.getTitleSearchCount(params);
    }
    
    @Override
    public List<Review> searchByWriter(Map<String, Object> params) {
        return reviewDao.searchByWriter(params);
    }
    
    @Override
    public int getWriterSearchCount(Map<String, Object> params) {
        return reviewDao.getWriterSearchCount(params);
    }
    
    @Override
    public List<Review> getReviewsByPlace(Map<String, Object> params) {
        return reviewDao.getReviewsByPlace(params);
    }
    
    @Override
    public int getPlaceReviewCount(int placeId) {
        return reviewDao.getPlaceReviewCount(placeId);
    }
    
    @Override
    public List<Review> searchByTitleInPlace(Map<String, Object> params) {
        return reviewDao.searchByTitleInPlace(params);
    }
    
    @Override
    public int getTitleSearchCountInPlace(Map<String, Object> params) {
        return reviewDao.getTitleSearchCountInPlace(params);
    }
    
    @Override
    public List<Review> searchByWriterInPlace(Map<String, Object> params) {
        return reviewDao.searchByWriterInPlace(params);
    }
    
    @Override
    public int getWriterSearchCountInPlace(Map<String, Object> params) {
        return reviewDao.getWriterSearchCountInPlace(params);
    }
    
    // 리뷰 이미지 관련 메서드
    @Override
    @Transactional
    public int addReviewImage(ReviewImage reviewImage) {
        return reviewDao.addReviewImage(reviewImage);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewImage> getReviewImages(int reviewId) {
        return reviewDao.getReviewImages(reviewId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ReviewImage getReviewImageById(int imageId) {
        return reviewDao.getReviewImageById(imageId);
    }
    
    @Override
    @Transactional
    public int deleteReviewImage(int imageId) {
        return reviewDao.deleteReviewImage(imageId);
    }

    @Override
    public Review getReviewIdByTitleAndContent(Review review) {
        return reviewDao.getReviewIdByTitleAndContent(review);
    }

}