package com.CimonHe.dao;

import com.CimonHe.pojo.LikeComic;

import java.util.List;

public interface LikeComicMapper {

    LikeComic hasLike(LikeComic likeComic);

    int queryComicLike(String comicName);

    int addComicLike(LikeComic likeComic);

    int deleteComicLike(LikeComic likeComic);

}
