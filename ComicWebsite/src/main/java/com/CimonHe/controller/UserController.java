package com.CimonHe.controller;

import com.CimonHe.pojo.Comic;
import com.CimonHe.pojo.LikeComic;
import com.CimonHe.pojo.PendingComic;
import com.CimonHe.pojo.User;
import com.CimonHe.service.*;
import com.CimonHe.utils.SendEmailTask;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.OutputKeys;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;


@RestController
@RequestMapping("/user")
public class UserController {

    public final int SUCCESS = 20;

    public final int FAIL = 30;

    public static int verificationCode = -1 ;


    @Autowired
    @Qualifier("UserServiceImpl")
    private UserService userService;

    @Autowired
    @Qualifier("javaMailSender")
    private JavaMailSender javaMailSender;//在spring中配置的邮件发送的bean

    @Autowired
    @Qualifier("PendingComicServiceImpl")
    private PendingComicService pendingComicService;

    @Autowired
    @Qualifier("LikeComicServiceImpl")
    private LikeComicService likeComicService;

    @Autowired
    @Qualifier("ComicServiceImpl")
    private ComicService comicService;


    @RequestMapping(value = "loginByPwd",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    public String loginByPwd(HttpSession session,@RequestParam("userInf") String userInf,@RequestParam("password") String password)
    {
        JSONObject returnValue = new JSONObject();

        System.out.println(userInf+password);
        User user = null;
        if (userService.queryUserByNameAndPwd(userInf,password)!=null)
            user = userService.queryUserByNameAndPwd(userInf,password);
        if (userService.queryUserByEmailAndPwd(userInf,password)!=null)
            user = userService.queryUserByEmailAndPwd(userInf,password);
        if (user != null)
        {
            session.setAttribute("user", user.getUsername());
            System.out.println("session"+session.getAttribute("user"));
            System.out.println("success");
            returnValue.put("status",SUCCESS);
            returnValue.put("msg","登录成功");
            returnValue.put("username",user.getUsername());
            returnValue.put("email",user.getEmail());
            return returnValue.toString();
        }
        else
        {
            returnValue.put("status",FAIL);
            returnValue.put("msg","邮箱/用户名和密码不匹配");
            returnValue.put("username",null);
            returnValue.put("email",null);
            System.out.println("fail");
            return returnValue.toString();
        }

    }


    @RequestMapping(value = "loginByEmail",produces = { "application/json;charset=UTF-8" })
    public String loginByEmail(HttpSession session ,@RequestParam("email") String email,@RequestParam("verifyCode") String verifyCode)
    {

        JSONObject returnValue = new JSONObject();

        User user = userService.queryUserByEmail(email);
        if (user==null)
        {
            returnValue.put("status",FAIL);
            returnValue.put("msg","该邮箱还未注册");
            System.out.println("失败一");//
            return returnValue.toString();

        }
        else if (String.valueOf(verificationCode).equals(verifyCode)) //接受用户输入的验证码并判断是否成功
        {
            session.setAttribute("user", user.getUsername());
            verificationCode=-1;
            System.out.println("session"+session.getAttribute("user"));//
            System.out.println("success");//
            returnValue.put("status",SUCCESS);
            returnValue.put("msg","登录成功");
            returnValue.put("username",user.getUsername());
            returnValue.put("email",user.getEmail());
            return returnValue.toString();
        }
        else
        {
            System.out.println("失败");//
            returnValue.put("status",FAIL);
            returnValue.put("msg","登录失败，验证码不存在");
            return returnValue.toString();
        }
    }

    @RequestMapping(value = "sendVerifyCode",produces = { "application/json;charset=UTF-8" })
    public String sendMailTest(@RequestParam("receiver") String receiver ) {
        JSONObject returnValue = new JSONObject();
        System.out.println(receiver);
        verificationCode = (new Random()).nextInt(1000000);
        try
        {
            sendMail(verificationCode,receiver);
        }
        catch (Exception e){
            e.printStackTrace();
            returnValue.put("status",FAIL);
            returnValue.put("msg","邮件发送失败，服务器出错");
            return returnValue.toString();
        }
        System.out.println(verificationCode);
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","邮件发送成功，请接收");
        return returnValue.toString();
    }

    @RequestMapping(value = "register",produces = { "application/json;charset=UTF-8" })
    public String register(@RequestParam("email") String email,@RequestParam("verifyCode") String verifyCode,@RequestParam("username") String username,@RequestParam("password") String password)
    {
        JSONObject returnValue = new JSONObject();
        System.out.println(email+" "+verifyCode+" "+username+" "+password+" ");
        if (userService.queryUserByName(username)!=null)
        {
            returnValue.put("status",FAIL);
            returnValue.put("msg","该用户名已被他人注册，请尝试其他的名字");
            return returnValue.toString();
        }
        if (userService.queryUserByEmail(email)!=null)
        {
            returnValue.put("status",FAIL);
            returnValue.put("msg","该邮件已被他人注册，请尝试其他的邮件");
            return returnValue.toString();
        }
        if (password==null || password.equals(""))
        {
            returnValue.put("status",FAIL);
            returnValue.put("msg","密码不应为空");
            return returnValue.toString();
        }
        System.out.println(verificationCode);
        if (String.valueOf(verificationCode).equals(verifyCode)) //接受用户输入的验证码并判断是否成功
        {
            userService.addUser(new User(username,password,email));
            verificationCode=-1;
            System.out.println("成功");
            returnValue.put("status",SUCCESS);
            returnValue.put("msg","注册成功！");
            return returnValue.toString();
        }
        else
        {
            System.out.println("失败");
            returnValue.put("status",FAIL);
            returnValue.put("msg","验证码输入错误");
            return returnValue.toString();
        }
    }


    @RequestMapping(value = "cancelUser",produces = { "application/json;charset=UTF-8" })
    public String cancelUser(HttpSession session,String username,String password,String email,String verifyCode){

        JSONObject returnValue = new JSONObject();

        User user = userService.queryUserByNameAndPwd(username,password);
        if (user==null)
        {
            System.out.println("fail");
            userService.queryUserByName(username);
            returnValue.put("status",FAIL);
            returnValue.put("msg","该用户密码输入错误，不需要注销");
            return returnValue.toString();
        }
        try {
            sendMail(verificationCode,email);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (String.valueOf(verificationCode).equals(verifyCode)) //接受用户输入的验证码并判断是否成功
        {
            userService.deleteUserByName(username);
            verificationCode=-1;
            System.out.println("login");
            session.removeAttribute("user");
            returnValue.put("status",SUCCESS);
            returnValue.put("msg","注销成功！");
            return returnValue.toString();
        }
        else
        {
            returnValue.put("status",FAIL);
            returnValue.put("msg","验证码输入错误");
            return returnValue.toString();
        }
    }

    @RequestMapping(value = "uploadComic",produces = { "application/json;charset=UTF-8" })
    public ResponseEntity uploadComic(@RequestParam("file") CommonsMultipartFile file, @RequestParam("comicName") String comicName, @RequestParam("tag") String tag, HttpSession session) throws IOException {

//        JSONObject returnValue = new JSONObject();

        String fileName = file.getOriginalFilename();
        System.out.println(file.getOriginalFilename());
        String username = session.getAttribute("user").toString();
        if (!checkFile(file.getOriginalFilename()))
        {
//            System.out.println("失败");
//            returnValue.put("status",FAIL);
//            returnValue.put("msg","上传漫画格式不正确，应该为jpg,gif,png,ico,bmp,jpeg");
//            return returnValue.toString();
            return new  ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }
        System.out.println("$$$$1");
        System.out.println("session"+session.getAttribute("user"));
        System.out.println("####2");
        //上传路径保存设置
        String path = session.getServletContext().getRealPath("/upload/"+username);

        System.out.println(path);
        File realPath = new File(path);
        if (!realPath.exists()){
            realPath.mkdirs();
        }
        //上传文件地址
        System.out.println("上传文件保存地址："+realPath);

        //通过CommonsMultipartFile的方法直接写文件（注意这个时候）
        file.transferTo(new File(path +"/"+comicName+".jpg"));
        userService.addComic(username,comicName,tag);

//        returnValue.put("status",SUCCESS);
//        returnValue.put("msg","上传漫画封面成功！");
//        return returnValue.toString();
        return new  ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "uploadComicChapter",produces = { "application/json;charset=UTF-8" })
    public ResponseEntity  uploadComicChapter(@RequestParam("files") CommonsMultipartFile[] files, @RequestParam("comicName") String comicName, @RequestParam("chapter") String chapter, HttpServletRequest request, HttpSession session) throws IOException {
//        JSONObject returnValue = new JSONObject();
        int no=0;
        String username = session.getAttribute("user").toString();
        String path = session.getServletContext().getRealPath("/upload/"+username+"/"+comicName+"/"+chapter);
        System.out.println(path);
        File realPath = new File(path);
        if (realPath.exists()){
            deleteDirect(realPath);
        }
        System.out.println("realpath"+realPath.toString());
        realPath.mkdirs();
        realPath.mkdirs();

        if (pendingComicService.queryPendingComic(username,comicName)==null)
            pendingComicService.addPendingComic(username,comicName);
        for (CommonsMultipartFile file:files){
            String fileName = file.getOriginalFilename();
            System.out.println(file.getOriginalFilename());
            if (!checkFile(file.getOriginalFilename()))
            {

//                System.out.println("失败");
//                returnValue.put("status",FAIL);
//                returnValue.put("msg","上传漫画格式不正确，应该为jpg,gif,png,ico,bmp,jpeg");
//                return returnValue.toString();

                return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
            }
            System.out.println("$$$$1");
            System.out.println("session"+session.getAttribute("user"));
            System.out.println("####2");
            //上传路径保存设置

            //上传文件地址
            System.out.println("上传文件保存地址："+realPath);

            //通过CommonsMultipartFile的方法直接写文件（注意这个时候）
            file.transferTo(new File(path+"/"+String.valueOf(++no)+fileName.substring(fileName.lastIndexOf(".") , fileName.length())));

        }
//        returnValue.put("status",SUCCESS);
//        returnValue.put("msg","上传漫画成功！待审核");
//        return returnValue.toString();

        return new ResponseEntity(HttpStatus.OK);

    }

    @RequestMapping("/deleteComic")
    public String deleteComic(@RequestParam("comicName") String comic,String username, HttpSession session){
        JSONObject returnValue = new JSONObject();
        String path = session.getServletContext().getRealPath("/comics/"+username+"/"+comic);
        comicService.deleteComicByComicName(comic);
        File realPath = new File(path);
        deleteDirect(realPath);
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","删除漫画成功！");
        return returnValue.toString();
    }

    @Test
    public void test1(){
        String path = "D:\\ComicWebsite\\uploadComic\\安生\\星际牛仔\\第一章";
        File realPath = new File(path);
        if (realPath.exists()){
            deleteDirect(realPath);
        }
        realPath.mkdir();
        realPath.mkdir();

    }

    private static void deleteDirect(File filedir) {

        // 如果是目录
        if (filedir.exists() && filedir.isDirectory()) {
            File[] listFiles = filedir.listFiles();

            for (File file : listFiles) {
                deleteDirect(file);
            }

            filedir.delete();
        } else {
            filedir.delete();
        }
    }


    private boolean checkFile(String fileName) {
        //设置允许上传文件类型
        String suffixList = "jpg,gif,png,ico,bmp,jpeg";
        // 获取文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if (suffixList.contains(suffix.trim().toLowerCase())) {
            return true;
        }
        return false;
    }


    @RequestMapping(value="/download")
    public ResponseEntity downloads(HttpSession session,HttpServletResponse response ,String username,String comicNames[], HttpServletRequest request) throws Exception{
        //要下载的图片地址
        for (String comicName : comicNames) {
            String  path = session.getServletContext().getRealPath("/comics/"+username+"/"+comicName);
            File file1=new File(path);
            File[] tempList = file1.listFiles();
            for (int i = 0; i < tempList.length; i++) {
                System.out.println("文     件："+tempList[i]);
                String  fileName = tempList[i].getName();
                System.out.println();

                //1、设置response 响应头
                response.reset(); //设置页面不缓存,清空buffer
                response.setCharacterEncoding("UTF-8"); //字符编码
                response.setContentType("multipart/form-data"); //二进制传输数据
                //设置响应头
                response.setHeader("Content-Disposition",
                        "attachment;fileName="+ URLEncoder.encode(fileName, "UTF-8"));

                File file = new File(path,fileName);
                //2、 读取文件--输入流
                InputStream input=new FileInputStream(file);
                //3、 写出文件--输出流
                OutputStream out = response.getOutputStream();

                byte[] buff =new byte[1024];
                int index=0;
                //4、执行 写出操作
                while((index= input.read(buff))!= -1){
                    out.write(buff, 0, index);
                    out.flush();
                }
                out.close();
                input.close();

            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }


    @RequestMapping(value = "modifyInf",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    public String modifyInf(User user,String verifyCode){
        JSONObject returnValue = new JSONObject();

        if (String.valueOf(verificationCode).equals(verifyCode)) //接受用户输入的验证码并判断是否成功
        {
            userService.updateUser(user);
            System.out.println("成功");
            returnValue.put("status",SUCCESS);
            returnValue.put("msg","修改用户信息成功！");
            return returnValue.toString();
        }
        else
        {
            returnValue.put("status",FAIL);
            returnValue.put("msg","验证码输入错误");
            return returnValue.toString();
        }
    }

    @RequestMapping(value = "hasLike",produces = { "application/json;charset=UTF-8" })
    public String hasLike (LikeComic likeComic){
        JSONObject returnValue = new JSONObject();
        boolean hasALike = false;
        if (likeComicService.hasLike(likeComic)!=null)
            hasALike = true;
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","已经点过赞！");
        returnValue.put("hasALike",hasALike);
        return returnValue.toString();
    }

    @RequestMapping(value = "addComicLike",produces = { "application/json;charset=UTF-8" })
    public String addComicLike (LikeComic likeComic) {
        JSONObject returnValue = new JSONObject();

        if (likeComicService.hasLike(likeComic) != null) {
            returnValue.put("status", FAIL);
            returnValue.put("msg", "已经为该漫画点过赞");
            return returnValue.toString();
        }
        likeComicService.addComicLike(likeComic);
        returnValue.put("status", SUCCESS);
        returnValue.put("msg", "点赞成功！");
        return returnValue.toString();
    }

    @RequestMapping(value = "deleteComicLike",produces = { "application/json;charset=UTF-8" })
    public String deleteComicLike (LikeComic likeComic){
        JSONObject returnValue = new JSONObject();

        if (likeComicService.hasLike(likeComic)==null)
        {
            returnValue.put("status",FAIL);
            returnValue.put("msg","无点赞无需撤销点赞！");
            return returnValue.toString();
        }
        likeComicService.deleteComicLike(likeComic);
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","取消点赞成功！");
        return returnValue.toString();
    }

    @RequestMapping(value = "queryComicLike",produces = { "application/json;charset=UTF-8" })
    public String queryComicLike(String comicName){
        JSONObject returnValue = new JSONObject();
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","查询漫画点赞数成功！");
        returnValue.put("ComicLike",likeComicService.queryComicLike(comicName));
        return returnValue.toString();
    }

    @RequestMapping(value = "searchUser",produces = { "application/json;charset=UTF-8" })
    public String searchUser (String name){
        JSONObject returnValue = new JSONObject();
        List<User> users = userService.queryUserByNameLike(name);
        returnValue.put("users",users);
        return returnValue.toString();
    }


    @RequestMapping(value = "countAllComic",produces = { "application/json;charset=UTF-8" })
    public String countAllComic(int pageSize){
        JSONObject returnValue = new JSONObject();
        System.out.println(comicService.countAllComic());
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","返回总页数成功！");
        returnValue.put("allPages",(comicService.countAllComic()+pageSize-1)/pageSize);
        return returnValue.toString();
    }

    @RequestMapping(value = "getAllComic",produces = { "application/json;charset=UTF-8" })
    public String getAllComic (int pageNum, int pageSize){
        JSONObject returnValue = new JSONObject();


        PageHelper.startPage(pageNum, pageSize);
        //分页查询
        List<Comic> comics= comicService.queryAllComic();
        List <String> imgPaths = new ArrayList<>();
        List <String> usernames = new ArrayList<>();
        List <String> comicNames = new ArrayList<>();
        for (Comic comic: comics)
        {
            imgPaths.add("/comics/"+comic.getUsername()+"/"+comic.getComicName()+".jpg");
            //将图片文件返回前端
            usernames.add(comic.getUsername());
            comicNames.add(comic.getComicName());
        }
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","返回所有漫画！");
        returnValue.put("imgPaths",imgPaths);
        returnValue.put("username",usernames);
        returnValue.put("comicNames",comicNames);
        return returnValue.toString();
    }

    @RequestMapping(value = "countComicByTag",produces = { "application/json;charset=UTF-8" })
    public String countComicByTag(String tag,int pageSize){
        JSONObject returnValue = new JSONObject();
        System.out.println(comicService.countComicByTag(tag));
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","返回总页数成功！");
        returnValue.put("allPages",(comicService.countComicByTag(tag)+pageSize-1)/pageSize);
        return returnValue.toString();
    }

    @RequestMapping(value = "getAllUserComic",produces = { "application/json;charset=UTF-8" })
    public String getAllUserComic (String username,int pageNum, int pageSize){
        JSONObject returnValue = new JSONObject();


        PageHelper.startPage(pageNum, pageSize);
        //分页查询
        List<Comic> comics= comicService.getAllUserComic(username);
        List <String> imgPaths = new ArrayList<>();
        List <String> usernames = new ArrayList<>();
        List <String> comicNames = new ArrayList<>();
        for (Comic comic: comics)
        {
            imgPaths.add("/comics/"+comic.getUsername()+"/"+comic.getComicName()+".jpg");
            //将图片文件返回前端
            usernames.add(comic.getUsername());
            comicNames.add(comic.getComicName());
        }
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","返回该用户所有漫画！");
        returnValue.put("imgPaths",imgPaths);
        returnValue.put("username",usernames);
        returnValue.put("comicNames",comicNames);
        return returnValue.toString();
    }

    @RequestMapping(value = "searchComic",produces = { "application/json;charset=UTF-8" })
    public String searchComic (String tag,int pageNum, int pageSize){
        JSONObject returnValue = new JSONObject();


        PageHelper.startPage(pageNum, pageSize);
        //分页查询
        List<Comic> comics= comicService.queryComicByTag(tag);
        List <String> imgPaths = new ArrayList<>();
        List <String> usernames = new ArrayList<>();
        List <String> comicNames = new ArrayList<>();
        for (Comic comic: comics)
        {
            imgPaths.add("/comics/"+comic.getUsername()+"/"+comic.getComicName()+".jpg");
            //将图片文件返回前端
            usernames.add(comic.getUsername());
            comicNames.add(comic.getComicName());
        }
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","返回根据标签查询的漫画！");
        returnValue.put("imgPaths",imgPaths);
        returnValue.put("username",usernames);
        returnValue.put("comicNames",comicNames);
        return returnValue.toString();
    }


    @RequestMapping(value = "countComicByUsername",produces = { "application/json;charset=UTF-8" })
    public String countComicByUsername(int pageSize ,String username){
        JSONObject returnValue = new JSONObject();
        System.out.println(comicService.countComicByUsername(username));
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","返回用户漫画总页数成功！");
        returnValue.put("allPages",(comicService.countComicByUsername(username)+pageSize-1)/pageSize);
        return returnValue.toString();
    }




    @RequestMapping(value = "/comicChapter",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    public String comicChapter(HttpSession session,String username,String comicName){

        JSONObject returnValue = new JSONObject();

        String path = session.getServletContext().getRealPath("/comics/"+username+"/"+comicName+"/");
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
        returnValue.put("msg","返回所有漫画章节！");
        returnValue.put("chapters",chapters);
        return returnValue.toString();
    }

    @RequestMapping(value = "/readChapter",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    public String  readCheckChapter(HttpSession session,String username,String comicName,String chapter){

        JSONObject returnValue = new JSONObject();

        String path = "/comics/"+username+"/"+comicName+"/"+chapter;
        System.out.println(path);
        //将章节的所有图片传给前端
        List<String> imgPaths = new ArrayList<>();
        File file=new File(path);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            imgPaths.add(path+"\\"+tempList[i].getName());
            System.out.println(tempList[i]);
            System.out.println(tempList[i].getName());
        }
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","返回此章节的所有图片！");
        returnValue.put("imgPaths",imgPaths);
        return returnValue.toString();
    }

    @RequestMapping(value = "/testMail",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    public void testMail(){
        try {
            sendMail(verificationCode,"2895325697@qq.com");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //高并发处理
    public void sendMail(int verificationCode,String receiver) throws Exception
        {
            Thread thread = new SendEmailTask(javaMailSender,verificationCode,receiver);
            thread.start();
        }



}
