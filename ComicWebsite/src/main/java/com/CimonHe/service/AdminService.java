package com.CimonHe.service;

import com.CimonHe.pojo.Admin;

import java.util.Map;

public interface AdminService {
    Admin queryAdminByNameAndPwd(String adminName,String password);
    Admin queryAdminByEmailAndPwd(String email,String password);

}
