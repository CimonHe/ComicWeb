package com.CimonHe.service;

import com.CimonHe.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserService {
    int addUser(User user);

    int deleteUserById(int id);

    int updateUser(User user);

    User queryUserById(int id);

    List<User> queryAllUser();

    User queryUserByNameAndPwd(String username, String password);

    User queryUserByEmailAndPwd(String email, String password);

    User queryUserByName(String username);

    User queryUserByEmail(String username);

    int deleteUserByName(String username);

    int addComic (String username ,String comicName,String tag);

    int addComicChapter(String username ,String comicName, String chapter);

    List<User> queryUserByNameLike (String name);


}
