package com.ssafy.edu.like.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Like {
    int likeId ;
    int userId ;
    int placeId ;
    private String regTime;
}
