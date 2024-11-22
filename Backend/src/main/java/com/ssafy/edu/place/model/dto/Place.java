package com.ssafy.edu.place.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Place {

    private int placeId;
    private String kakaoPlaceId;
    private String placeName;
    private int reivewCount;
    private int likeCount;
    private String regTime;
    private String updTime;
    
}
