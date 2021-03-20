package com.CimonHe.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Comic {
    private int id;
    private String username;
    private String comicName;
    private String tag;

    public Comic(String username, String comicName, String tag) {
        this.username = username;
        this.comicName = comicName;
        this.tag = tag;
    }

}
