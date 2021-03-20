package com.CimonHe.dao;

import com.CimonHe.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserMapper {

    int addUser(User user);

    int deleteUserById(int id);

    int updateUser(User user);

    User queryUserById(int id);

    List<User> queryAllUser();

    User queryUserByNameAndPwd(Map<String,String> map);

    User queryUserByEmailAndPwd(Map<String,String> map);

    User queryUserByName(String username);

    User queryUserByEmail(String email);

    int deleteUserByName(String name);

    int addComic (Map<String,Object> map);

    int addComicChapter (Map<String,Object> map);

    List<User> queryUserByNameLike (String name);
}
