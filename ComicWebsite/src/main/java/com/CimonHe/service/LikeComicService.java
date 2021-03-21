package com.CimonHe.service;

import com.CimonHe.pojo.LikeComic;

import java.util.List;

public interface LikeComicService {

    LikeComic hasLike(LikeComic likeComic);

    int queryComicLike(String comicName);

    int addComicLike(LikeComic likeComic);

    int deleteComicLike(LikeComic likeComic);



}
