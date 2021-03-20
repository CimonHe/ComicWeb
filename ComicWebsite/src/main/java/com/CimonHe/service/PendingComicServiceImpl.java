package com.CimonHe.service;

import com.CimonHe.dao.PendingComicMapper;
import com.CimonHe.pojo.PendingComic;

import java.util.List;

public class PendingComicServiceImpl implements PendingComicService {
    private PendingComicMapper pendingComicMapper;

    public void setPendingComicMapper(PendingComicMapper pendingComicMapper) {
        this.pendingComicMapper = pendingComicMapper;
    }

    @Override
    public List<PendingComic> queryAllPendingComics() {
        return pendingComicMapper.queryAllPendingComics();
    }

    @Override
    public int addPendingComic(String username, String comicName) {
        PendingComic pendingComic = new PendingComic(username,comicName);
        return pendingComicMapper.addPendingComic(pendingComic);
    }

    @Override
    public PendingComic queryPendingComic(String username, String comicName) {
        PendingComic pendingComic = new PendingComic(username,comicName);
        return pendingComicMapper.queryPendingComic(pendingComic);
    }

    @Override
    public int deletePendingComic(String comicName) {
        return pendingComicMapper.deletePendingComic(comicName);
    }

    @Override
    public int getCountAllPendingComics() {
        return pendingComicMapper.getCountAllPendingComics();
    }


}
