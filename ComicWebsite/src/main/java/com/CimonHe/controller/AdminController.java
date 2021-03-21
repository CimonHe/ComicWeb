package com.CimonHe.controller;

import com.CimonHe.pojo.Admin;
import com.CimonHe.pojo.PendingComic;
import com.CimonHe.service.AdminService;
import com.CimonHe.service.ComicService;
import com.CimonHe.service.PendingComicService;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    @Qualifier("AdminServiceImpl")
    private AdminService adminService;

    @Autowired
    @Qualifier("PendingComicServiceImpl")
    private PendingComicService pendingComicService;

    @Autowired
    @Qualifier("ComicServiceImpl")
    private ComicService comicService;

    public final int SUCCESS = 20;

    public final int FAIL = 30;

    private List<String> tags = new ArrayList<String>(Arrays.asList("中二","热血","青少年"));

    public List<String> getTags() {
        return tags;
    }

    @RequestMapping(value = "loginByPwd",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    public String loginByPwd(@RequestParam("adminInf") String adminInf, @RequestParam("password") String password, HttpSession session ){
        JSONObject returnValue = new JSONObject();
        System.out.println(adminInf+password);
        Admin admin = null;
        if (adminService.queryAdminByEmailAndPwd(adminInf,password)!=null)
            admin = adminService.queryAdminByEmailAndPwd(adminInf,password);
        if (adminService.queryAdminByNameAndPwd(adminInf,password)!=null)
            admin = adminService.queryAdminByNameAndPwd(adminInf,password);
        if (admin!=null)
        {
            session.setAttribute("admin",admin.getAdminName());
            System.out.println("success");
            returnValue.put("status",SUCCESS);
            returnValue.put("msg","管理员登录成功！");
            returnValue.put("adminName",admin.getAdminName());
            returnValue.put("email",admin.getEmail());
        }
        else
        {
            System.out.println("fall");
        }
        return returnValue.toString();
    }

    @RequestMapping(value = "countPendingComic",produces = { "application/json;charset=UTF-8" })
    public String countPendingComic(int pageSize){
        JSONObject returnValue = new JSONObject();
        System.out.println(pendingComicService.getCountAllPendingComics());
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","返回总页数成功！");
        returnValue.put("allPages",(pendingComicService.getCountAllPendingComics()+pageSize-1)/pageSize);
        return returnValue.toString();
    }

    @RequestMapping(value = "checkPendingComic",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    public String checkComic(HttpSession session,@RequestParam("pageNum")int pageNum,@RequestParam("pageSize")int pageSize ){
        JSONObject returnValue = new JSONObject();


        PageHelper.startPage(pageNum, pageSize);
        //分页查询
        List<PendingComic> pendingComics= pendingComicService.queryAllPendingComics();
        List <String> imgPaths = new ArrayList<>();
        List <String> usernames = new ArrayList<>();
        List <String> comicNames = new ArrayList<>();
        for (PendingComic pendingComic: pendingComics)
        {
            imgPaths.add("/upload/"+pendingComic.getUsername()+"/"+pendingComic.getComicName()+".jpg");
            //将图片文件返回前端
            usernames.add(pendingComic.getUsername());
            comicNames.add(pendingComic.getComicName());
        }
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","返回所有待审核漫画！");
        returnValue.put("imgPaths",imgPaths);
        returnValue.put("username",usernames);
        returnValue.put("comicNames",comicNames);
        return returnValue.toString();
    }

    @RequestMapping("/test")
    public String test(){
        JSONObject returnValue = new JSONObject();
        returnValue.put("value","password is not equal");
        return returnValue.toString();
    }

    @RequestMapping(value = "/checkPendingChapter",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    public String checkChapter(HttpSession session,String username,String comicName){

        JSONObject returnValue = new JSONObject();

        String path = session.getServletContext().getRealPath("/upload/"+username+"/"+comicName+"/");
        List<String> chapters =new  ArrayList<>();
        System.out.println(path);
        //读取文件中的所有的章节信息并返回给前端
        File file=new File(path);
        File[] tempList = file.listFiles();
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < tempList.length; i++) {
            list.add(tempList[i].getName());
            System.out.println(tempList[i].getName());
            chapters.add(tempList[i].getName());
        }
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","返回所有待审核章节！");
        returnValue.put("chapters",chapters);
        return returnValue.toString();
    }

    @RequestMapping(value = "/readChapter",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    public String  readCheckChapter(HttpSession session,String username,String comicName,String chapter){

        JSONObject returnValue = new JSONObject();

        String path = "/upload/"+username+"/"+comicName+"/"+chapter;
        System.out.println("***"+path+"***");
        //将章节的所有图片传给前端
        List<String> imgPaths = new ArrayList<>();
        File file=new File(session.getServletContext().getRealPath(path));
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            imgPaths.add(path+"/"+tempList[i].getName());
            System.out.println(tempList[i]);
            System.out.println(tempList[i].getName());
        }
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","返回此章节的所有图片！");
        returnValue.put("imgPaths",imgPaths);
        return returnValue.toString();
    }

    @RequestMapping(value = "/checkedNotPass",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    public String checkedNotPass(HttpSession session,String username,String comicName,String chapter){

        JSONObject returnValue = new JSONObject();

        String chapterPath = session.getServletContext().getRealPath("/upload/"+username+"/"+comicName+"/"+chapter);
        String comicPath = session.getServletContext().getRealPath("/upload/"+username+"/"+comicName+"/");
        System.out.println(chapterPath);
        System.out.println(comicPath);
        File chapterFile = new File(chapterPath);
        File comicFile = new File(comicPath);
        deleteDirect(chapterFile);
        //删除path文件夹漫画
        System.out.println(comicFile.listFiles()==null);
        if (comicFile.listFiles()==null)
            pendingComicService.deletePendingComic(comicName);
        //从审核数据库中删除
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","审核漫画已下架！");
        return returnValue.toString();
    }

    @RequestMapping(value= "/checkedPass",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    public String checkedPass(HttpSession session,@RequestParam("username") String username,@RequestParam("comicName") String comicName,@RequestParam("chapter") String chapter){

        JSONObject returnValue = new JSONObject();

        //将pathFrom的文件转移到pathTo的文件
        String startChapterPath = session.getServletContext().getRealPath("/upload/"+username+"/"+comicName+"/"+chapter);
        String endChapterPath = session.getServletContext().getRealPath("/comics/"+username+"/"+comicName+"/"+chapter);
        String startComicPath = session.getServletContext().getRealPath("/upload/"+username+"/"+comicName);
        String endComicsPath = session.getServletContext().getRealPath("/comics/"+username+"/"+comicName);
        File startChapterFiles = new File(startChapterPath);
        File endChapterFiles = new File(endChapterPath);
        File startComicFile = new File(startComicPath);

        if (!endChapterFiles.exists())
            endChapterFiles.mkdirs();
        for (File startChapterFile : startChapterFiles.listFiles())
        {
            File endChapterFile = new File(endChapterPath+"/"+startChapterFile.getName());
            try {
                if (!endChapterFile.exists()) {
                    Files.copy(startChapterFile.toPath(), endChapterFile.toPath());
                    System.out.println("文件移动成功！起始路径：" + startChapterFile.toPath());
                } else {
                    endChapterFile.delete();
                    Files.copy(startChapterFile.toPath(), endChapterFile.toPath());
                    System.out.println("文件移动成功！起始路径：" + startChapterFile.toPath());
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        deleteDirect(startChapterFiles);
        //从审核数据库中删除
        System.out.println(startComicFile.listFiles()==null);
        if (startComicFile.listFiles()==null)
            pendingComicService.deletePendingComic(comicName);
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","审核漫画已通过！");
        return returnValue.toString();
    }

    @RequestMapping(value = "/addTag",produces = { "application/json;charset=UTF-8" })
    public String addTag(String newTag)
    {
        JSONObject returnValue = new JSONObject();

        if (tags.contains(newTag))
        {
            returnValue.put("status",FAIL);
            returnValue.put("msg","该标签已存在");
            return returnValue.toString();
        }
        tags.add(newTag);
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","添加标签成功！");
        System.out.println("添加标签成功！");
        return returnValue.toString();
    }

    @RequestMapping(value = "/queryTags",produces = { "application/json;charset=UTF-8" })
    public String queryTags (HttpServletResponse response)
    {
        JSONObject returnValue = new JSONObject();
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","查询所有标签成功！");
        returnValue.put("tags",tags);
        System.out.println("查询所有标签成功！");
        response.setStatus(200);
        return returnValue.toString();
    }

    @RequestMapping("/updateTag")
    public String updateTag (String oldTag,String newTag)
    {
        JSONObject returnValue = new JSONObject();

        if (tags.contains(newTag)&&(!tags.contains(oldTag)))
        {
            tags.remove(oldTag);
            tags.add(newTag);
            returnValue.put("status",SUCCESS);
            returnValue.put("msg","更新标签成功！");
            return returnValue.toString();
        }
        returnValue.put("status",FAIL);
        returnValue.put("msg","更新标签失败！旧标签不存在或者新标签已存在");
        return returnValue.toString();
    }

    private static void deleteDirect(File fileDir) {

        // 如果是目录
        if (fileDir.exists() && fileDir.isDirectory()) {
            File[] listFiles = fileDir.listFiles();

            for (File file : listFiles) {
                deleteDirect(file);
            }
        }
        fileDir.delete();
    }


}
