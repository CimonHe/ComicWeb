package com.CimonHe.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeComic {
    private int id;
    private String likeUser;
    private String comicName;

    public LikeComic(String likeUser, String comicName) {
        this.likeUser = likeUser;
        this.comicName = comicName;
    }

}
