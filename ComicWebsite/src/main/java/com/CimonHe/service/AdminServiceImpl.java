package com.CimonHe.service;

import com.CimonHe.dao.AdminMapper;
import com.CimonHe.pojo.Admin;

import java.util.HashMap;
import java.util.Map;

public class AdminServiceImpl implements AdminService {


    private AdminMapper adminMapper;

    public void setAdminMapper(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    @Override
    public Admin queryAdminByNameAndPwd(String adminName,String password) {
        Map<String,String> map = new HashMap<>();
        map.put("adminName",adminName);
        map.put("password",password);
        return adminMapper.queryAdminByNameAndPwd(map);
    }

    @Override
    public Admin queryAdminByEmailAndPwd(String email,String password) {
        Map<String,String> map = new HashMap<>();
        map.put("email",email);
        map.put("password",password);
        return adminMapper.queryAdminByEmailAndPwd(map);    }
}
