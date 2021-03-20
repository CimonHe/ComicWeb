package com.CimonHe.service;

import com.CimonHe.dao.ComicMapper;
import com.CimonHe.pojo.Comic;

import java.util.List;

public class ComicServiceImpl implements ComicService{
    private ComicMapper comicMapper;

    public void setComicMapper(ComicMapper comicMapper) {
        this.comicMapper = comicMapper;
    }

    public List<Comic> queryAllComic (){
        return comicMapper.queryAllComic();
    }

    public int addComic (Comic comic){
        return comicMapper.addComic(comic);
    }

    public int deleteComicByComicName (String comicName){
        return comicMapper.deleteComicByComicName(comicName);
    }

    public int updateByComicName(Comic comic){
        return comicMapper.updateByComicName(comic);
    }

    @Override
    public List<Comic> queryComicByTag(String tag) {
        return comicMapper.queryComicByTag(tag);
    }

    @Override
    public int countAllComic() {
        return comicMapper.countAllComic();
    }

    @Override
    public int countComicByTag(String tag) {
        return comicMapper.countComicByTag(tag);
    }

    @Override
    public int countComicByUsername(String username) {
        return comicMapper.countComicByUsername(username);
    }

    @Override
    public List<Comic> getAllUserComic(String username) {
        return comicMapper.getAllUserComic(username);
    }


}
