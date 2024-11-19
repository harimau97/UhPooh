package com.ssafy.edu.review.model.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    private int reviewId;
    private int userId;
    private int placeId;
    private String title;
    private String content;
    private String images;
    private String regTime;
    private String updTime;
    private String userName;

}
