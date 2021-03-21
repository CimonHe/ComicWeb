package com.CimonHe.service;

import com.CimonHe.dao.UserMapper;
import com.CimonHe.pojo.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService{
    private UserMapper userMapper;

    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public int addUser(User user) {
        return userMapper.addUser(user);
    }

    @Override
    public int deleteUserById(int id) {
        return userMapper.deleteUserById(id);
    }

    @Override
    public int updateUser(User user) {
        return userMapper.updateUser(user);
    }

    @Override
    public User queryUserById(int id) {
        return userMapper.queryUserById(id);
    }

    @Override
    public List<User> queryAllUser() {
        return userMapper.queryAllUser();
    }

    @Override
    public User queryUserByNameAndPwd(String username, String password) {
        Map<String,String> map = new HashMap<>();
        map.put("username",username);
        map.put("password",password);
        return userMapper.queryUserByNameAndPwd(map);
    }

    @Override
    public User queryUserByEmailAndPwd(String email, String password) {
        Map<String,String> map = new HashMap<>();
        map.put("email",email);
        map.put("password",password);
        return userMapper.queryUserByEmailAndPwd(map);
    }

    public User queryUserByName(String username){
        return userMapper.queryUserByName(username);
    }

    public int deleteUserByName(String username){
        return userMapper.deleteUserByName(username);
    }

    public User queryUserByEmail(String email){
        return userMapper.queryUserByEmail(email);
    }

    @Override
    public int addComic(String username, String comicName, String tag) {
        Map<String,Object> map = new HashMap<>();
        map.put("username",username);
        map.put("comicName",comicName);
        map.put("tag",tag);
        return userMapper.addComic(map);
    }


    public int addComicChapter(String username ,String comicName, String chapter) {
        Map<String,Object> map = new HashMap<>();
        map.put("username",username);
        map.put("comicName",comicName);
        map.put("chapter",chapter);
        return userMapper.addComicChapter(map);
    }

    @Override
    public List<User> queryUserByNameLike(String name) {
        return userMapper.queryUserByNameLike("%%"+name+"%%");
    }


}
