package com.CimonHe.dao;

import com.CimonHe.pojo.PendingComic;

import java.util.List;

public interface PendingComicMapper {
    List<PendingComic> queryAllPendingComics();

    int addPendingComic(PendingComic pendingComic);

    PendingComic queryPendingComic(PendingComic pendingComic);

    int deletePendingComic(String comicName);

    int getCountAllPendingComics();
}
