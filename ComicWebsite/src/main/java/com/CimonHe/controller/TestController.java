package com.CimonHe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class TestController {

    // 欢迎页面
    @RequestMapping("/test2")
    @ResponseBody
    public Map test() {

        Map<String,String> map = new HashMap<String, String>();
        map.put("测试1","123");
        map.put("测试2","456");

        return map;
    }
}
