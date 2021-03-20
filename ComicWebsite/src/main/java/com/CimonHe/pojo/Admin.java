package com.CimonHe.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    int id;
    String adminName;
    String password;
    String email;

    public Admin(String adminName,String password,String email)
    {
        this.adminName = adminName;
        this.password = password;
        this.email = email;
    }
}
