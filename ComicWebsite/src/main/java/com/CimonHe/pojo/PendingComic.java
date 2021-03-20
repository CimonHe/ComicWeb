package com.CimonHe.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingComic {
    int id;
    String username;
    String comicName;

    public PendingComic (String username,String comicName)
    {
        this.username=username;
        this.comicName=comicName;
    }
}
