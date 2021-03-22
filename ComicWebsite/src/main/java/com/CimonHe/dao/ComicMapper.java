package com.CimonHe.dao;

import com.CimonHe.pojo.Comic;

import java.util.List;
import java.util.Map;

public interface ComicMapper {

    List<Comic> queryAllComic ();

    int addComic (Comic comic);

    int deleteComicByComicName (String comicName);

    int updateByComicName(Comic comic);

    int updateTag(Map<String,String> map);

    List<Comic> queryComicByTag(String tag);

    int countAllComic ();

    int countComicByTag (String tag);

    int countComicByUsername (String username);

    List<Comic> getAllUserComic(String username);

    Comic queryComicByName(String comicName);


}
