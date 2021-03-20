package com.CimonHe.dao;

import com.CimonHe.pojo.Admin;
import com.CimonHe.pojo.PendingComic;
import com.CimonHe.pojo.User;

import java.util.List;
import java.util.Map;

public interface AdminMapper {
    Admin queryAdminByNameAndPwd(Map<String,String> map);
    Admin queryAdminByEmailAndPwd(Map<String,String> map);
    List<PendingComic> queryPendingComics();
}
