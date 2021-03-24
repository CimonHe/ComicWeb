package com.CimonHe.controller;

import com.CimonHe.pojo.Comic;
import com.CimonHe.pojo.LikeComic;
import com.CimonHe.pojo.PendingComic;
import com.CimonHe.pojo.User;
import com.CimonHe.service.*;
import com.CimonHe.utils.SendEmailTask;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import org.apache.commons.io.IOUtils;
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

import javax.imageio.ImageIO;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.OutputKeys;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


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
        System.err.println(email+" "+verifyCode+" "+username+" "+password+" ");
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
        System.err.println(verificationCode);
        System.err.println(verifyCode);
        System.out.println(String.valueOf(verificationCode).equals(verifyCode));
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

    @RequestMapping(value ="/deleteComic",produces = { "application/json;charset=UTF-8" })
    public String deleteComic(@RequestParam("comicName") String comicName,String username, HttpSession session){
        JSONObject returnValue = new JSONObject();

        Comic comic = comicService.queryComicByName(comicName);

        if (comic==null||(comic.getUsername()!=username)){
            returnValue.put("status",FAIL);
            returnValue.put("msg","用户"+username+"不存在"+comicName+"漫画");
            return returnValue.toString();
        }

        String path = session.getServletContext().getRealPath("/comics/"+username+"/"+comicName);
        String comicPostPath = session.getServletContext().getRealPath("/upload/"+username+"/"+comicName+".jpg");
        System.err.println("comicPostPath:"+comicPostPath);
        System.out.println("comicPostPath:"+comicPostPath);
        comicService.deleteComicByComicName(comicName);
        File comicFile = new File(path);
        File comicPostFile = new File(comicPostPath);
        deleteDirect(comicFile);
        deleteDirect(comicPostFile);
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

    @RequestMapping(value = "/download")
    public void plistDownLoad(HttpSession session,HttpServletResponse response ,String username,String comicName,String chapter, HttpServletRequest request) throws Exception {
        // 此处模拟处理ids,拿到文件下载url
        System.out.println("$$$$$$$$$$$$");
        List<String> paths = new ArrayList<>();
        String chapterPath = session.getServletContext().getRealPath("/comics/"+username+"/"+comicName+"/"+chapter);
        System.out.println(chapterPath);
        File file=new File(chapterPath);
        File[] tempList = file.listFiles();
        System.out.println(tempList);
        for (int i = 0; i < tempList.length; i++) {
            paths.add(chapterPath+"/"+tempList[i].getName());
            System.out.println(chapterPath+"/"+tempList[i].getName());
        }
        if (paths.size() != 0) {
            // 创建临时路径,存放压缩文件
            String zipFilePath = "D:\\web\\我的zip.zip";
            // 压缩输出流,包装流,将临时文件输出流包装成压缩流,将所有文件输出到这里,打成zip包
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFilePath));
            // 循环调用压缩文件方法,将一个一个需要下载的文件打入压缩文件包
            for (String path : paths) {
                // 该方法在下面定义
                fileToZip(path, zipOut);
            }
            // 压缩完成后,关闭压缩流
            zipOut.close();

            //拼接下载默认名称并转为ISO-8859-1格式
            String fileName = new String((comicName+" "+chapter+".zip").getBytes(),"ISO-8859-1");
            response.setHeader("Content-Disposition", "attchment;filename="+fileName);

            //该流不可以手动关闭,手动关闭下载会出问题,下载完成后会自动关闭
            ServletOutputStream outputStream = response.getOutputStream();
            FileInputStream inputStream = new FileInputStream(zipFilePath);
            // 如果是SpringBoot框架,在这个路径
            // org.apache.tomcat.util.http.fileupload.IOUtils产品
            // 否则需要自主引入apache的 commons-io依赖
            // copy方法为文件复制,在这里直接实现了下载效果
            IOUtils.copy(inputStream, outputStream);

            // 关闭输入流
            inputStream.close();

            //下载完成之后，删掉这个zip包
            File fileTempZip = new File(zipFilePath);
            fileTempZip.delete();
        }
    }

    public static void fileToZip(String filePath,ZipOutputStream zipOut) throws IOException {
        // 需要压缩的文件
        File file = new File(filePath);
        // 获取文件名称,如果有特殊命名需求,可以将参数列表拓展,传fileName
        String fileName = file.getName();
        FileInputStream fileInput = new FileInputStream(filePath);
        // 缓冲
        byte[] bufferArea = new byte[1024 * 10];
        BufferedInputStream bufferStream = new BufferedInputStream(fileInput, 1024 * 10);
        // 将当前文件作为一个zip实体写入压缩流,fileName代表压缩文件中的文件名称
        zipOut.putNextEntry(new ZipEntry(fileName));
        int length = 0;
        // 最常规IO操作,不必紧张
        while ((length = bufferStream.read(bufferArea, 0, 1024 * 10)) != -1) {
            zipOut.write(bufferArea, 0, length);
        }
        //关闭流
        fileInput.close();
        // 需要注意的是缓冲流必须要关闭流,否则输出无效
        bufferStream.close();
        // 压缩流不必关闭,使用完后再关
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
        {
            hasALike = true;
            returnValue.put("status",SUCCESS);
            returnValue.put("msg","已经点过赞！");
            returnValue.put("hasALike",hasALike);
            return returnValue.toString();
        }
        else{
            hasALike = false;
            returnValue.put("status",SUCCESS);
            returnValue.put("msg","还未点过赞！");
            returnValue.put("hasALike",hasALike);
            return returnValue.toString();
        }
    }

    @RequestMapping(value = "pressLike",produces = { "application/json;charset=UTF-8" })
    public String pressLike (LikeComic likeComic){
        if (likeComicService.hasLike(likeComic)!=null)
        {
           return deleteComicLike(likeComic);
        }
        else
        {
            return addComicLike(likeComic);
        }

    }

    @RequestMapping(value = "addComicLike",produces = { "application/json;charset=UTF-8" })
    public String addComicLike (LikeComic likeComic) {
        JSONObject returnValue = new JSONObject();

        if (likeComic.getComicName()==null||likeComic.getLikeUser()==null)
        {
            returnValue.put("status", FAIL);
            returnValue.put("msg", "输入的点赞用户或者漫画不应该为空");
            return returnValue.toString();
        }
        if (userService.queryUserByName(likeComic.getLikeUser())==null)
        {
            returnValue.put("status", FAIL);
            returnValue.put("msg", "输入的点赞用户不存在");
            return returnValue.toString();
        }
        if (comicService.queryComicByName(likeComic.getComicName())==null)
        {
            returnValue.put("status", FAIL);
            returnValue.put("msg", "要点赞的漫画不存在");
            return returnValue.toString();
        }
        if (likeComicService.hasLike(likeComic) != null) {
            returnValue.put("status", FAIL);
            returnValue.put("msg", "已经为该漫画点过赞");
            returnValue.put("hasALike",true);
            returnValue.put("comicLike",likeComicService.queryComicLike(likeComic.getComicName()));
            return returnValue.toString();
        }
        likeComicService.addComicLike(likeComic);
        returnValue.put("status", SUCCESS);
        returnValue.put("msg", "点赞成功！");
        returnValue.put("hasALike",true);
        returnValue.put("comicLike",likeComicService.queryComicLike(likeComic.getComicName()));
        return returnValue.toString();
    }

    @RequestMapping(value = "deleteComicLike",produces = { "application/json;charset=UTF-8" })
    public String deleteComicLike (LikeComic likeComic){
        JSONObject returnValue = new JSONObject();

        if (likeComicService.hasLike(likeComic)==null)
        {
            returnValue.put("status",FAIL);
            returnValue.put("msg","无点赞无需撤销点赞！");
            returnValue.put("hasALike",false);
            returnValue.put("comicLike",likeComicService.queryComicLike(likeComic.getComicName()));
            return returnValue.toString();
        }
        likeComicService.deleteComicLike(likeComic);
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","取消点赞成功！");
        returnValue.put("hasALike",false);
        returnValue.put("comicLike",likeComicService.queryComicLike(likeComic.getComicName()));
        return returnValue.toString();
    }

    @RequestMapping(value = "queryComicLike",produces = { "application/json;charset=UTF-8" })
    public String queryComicLike(String comicName){
        JSONObject returnValue = new JSONObject();
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","查询漫画点赞数成功！");
        returnValue.put("comicLike",likeComicService.queryComicLike(comicName));
        return returnValue.toString();
    }

    @RequestMapping(value = "searchUser",produces = { "application/json;charset=UTF-8" })
    public String searchUser (String name){
        JSONObject returnValue = new JSONObject();
        List<User> users = userService.queryUserByNameLike(name);
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","查询用户成功！");
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
        List <String> tags = new ArrayList<>();
        for (Comic comic: comics)
        {
            imgPaths.add("/upload/"+comic.getUsername()+"/"+comic.getComicName()+".jpg");
            //将图片文件返回前端
            usernames.add(comic.getUsername());
            comicNames.add(comic.getComicName());
            tags.add(comic.getTag());
        }
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","返回所有漫画！");
        returnValue.put("imgPaths",imgPaths);
        returnValue.put("username",usernames);
        returnValue.put("comicNames",comicNames);
        returnValue.put("tags",tags);
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
        List <String> tags = new ArrayList<>();
        for (Comic comic: comics)
        {
            imgPaths.add("/upload/"+comic.getUsername()+"/"+comic.getComicName()+".jpg");
            //将图片文件返回前端
            usernames.add(comic.getUsername());
            comicNames.add(comic.getComicName());
            tags.add(comic.getTag());
        }
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","返回该用户所有漫画！");
        returnValue.put("imgPaths",imgPaths);
        returnValue.put("username",usernames);
        returnValue.put("comicNames",comicNames);
        returnValue.put("tags",tags);
        return returnValue.toString();
    }

    @RequestMapping(value = "searchComic",produces = { "application/json;charset=UTF-8" })
    public String searchComic (String tag,int pageNum, int pageSize){
        System.out.println("测试搜索漫画功能searchComic");
        System.out.println("tag:"+tag+" pageNum:"+pageNum+" pageSize"+pageSize);
        JSONObject returnValue = new JSONObject();

        PageHelper.startPage(pageNum, pageSize);
        //分页查询
        List<Comic> comics= comicService.queryComicByTag(tag);
        List <String> imgPaths = new ArrayList<>();
        List <String> usernames = new ArrayList<>();
        List <String> comicNames = new ArrayList<>();
        List <String> tags = new ArrayList<>();
        for (Comic comic: comics)
        {
            imgPaths.add("/upload/"+comic.getUsername()+"/"+comic.getComicName()+".jpg");
            //将图片文件返回前端
            usernames.add(comic.getUsername());
            comicNames.add(comic.getComicName());
            tags.add(comic.getTag());
        }
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","返回根据标签查询的漫画！");
        System.out.println("返回根据标签查询的漫画！");
        returnValue.put("imgPaths",imgPaths);
        returnValue.put("username",usernames);
        returnValue.put("comicNames",comicNames);
        returnValue.put("tags",tags);
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
        List<String> chapters =new  ArrayList<String>();
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
    public String  readChapter(HttpSession session,String username,String comicName,String chapter){

        JSONObject returnValue = new JSONObject();

        String path = "/comics/"+username+"/"+comicName+"/"+chapter;
        System.out.println(path);
        //将章节的所有图片传给前端
        List<String> imgPaths = new ArrayList<>();
        File file=new File(session.getServletContext().getRealPath(path));
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            imgPaths.add(path+"/"+tempList[i].getName());
            System.out.println(path+"/"+tempList[i].getName());
            System.out.println(tempList[i]);
            System.out.println(tempList[i].getName());
        }
        returnValue.put("status",SUCCESS);
        returnValue.put("msg","返回此章节的所有图片！");
        returnValue.put("imgPaths",imgPaths);
        return returnValue.toString();
    }


    //高并发处理
    public void sendMail(int verificationCode,String receiver) throws Exception
        {
            Thread thread = new SendEmailTask(javaMailSender,verificationCode,receiver);
            thread.start();
        }



}
