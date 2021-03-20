package com.CimonHe.service;

import com.CimonHe.dao.LikeComicMapper;
import com.CimonHe.pojo.LikeComic;

import java.util.List;

public class LikeComicServiceImpl implements LikeComicService{

    private LikeComicMapper likeComicMapper;

    public void setLikeComicMapper(LikeComicMapper likeComicMapper) {
        this.likeComicMapper = likeComicMapper;
    }

    @Override
    public LikeComic hasLike(LikeComic likeComic) {
        return this.likeComicMapper.hasLike(likeComic);
    }

    @Override
    public List<LikeComic> queryComicLike(String comicName) {
        return this.likeComicMapper.queryComicLike(comicName);
    }

    @Override
    public int addComicLike(LikeComic likeComic) {
        return this.likeComicMapper.addComicLike(likeComic);
    }

    @Override
    public int deleteComicLike(LikeComic likeComic) {
        return this.likeComicMapper.deleteComicLike(likeComic);
    }
}
