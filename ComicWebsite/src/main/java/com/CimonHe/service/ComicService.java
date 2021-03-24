package com.CimonHe.service;

import com.CimonHe.pojo.Comic;

import java.util.List;

public interface ComicService {

    List<Comic> queryAllComic ();


    int addComic (Comic comic);

    int deleteComicByComicName (String comicName);

    int updateByComicName(Comic comic);

    List<Comic> queryComicByTag(String tag);

    int countAllComic ();

    int countComicByTag (String tag);

    int countComicByUsername (String username);

    List<Comic> getAllUserComic(String username);

    int updateTag(String oldTag,String newTag);

    Comic queryComicByName(String comicName);

    int updateComicTag(String comicName,String newTag);
}
