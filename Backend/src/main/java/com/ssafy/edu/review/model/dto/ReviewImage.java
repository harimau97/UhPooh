package com.ssafy.edu.review.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewImage {
    private int imageId;
    private int reviewId;
    private String imageUrl;
}