package com.CimonHe.service;

import com.CimonHe.pojo.PendingComic;

import java.util.List;

public interface PendingComicService {
    List<PendingComic> queryAllPendingComics();
    int addPendingComic(String username,String comicName);
    PendingComic queryPendingComic(String username,String comicName);
    int deletePendingComic(String comicName);
    int getCountAllPendingComics();
}
