import com.CimonHe.dao.*;
import com.CimonHe.pojo.*;

import com.github.pagehelper.PageHelper;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTest {

    @Test
    public void test() {

        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        UserMapper mapper = context.getBean("userMapper", UserMapper.class);
        Map<String, String> map = new HashMap<String, String>();
        PageHelper.startPage(2, 3);
        List<User> list = mapper.queryAllUser();
        for (User user : list)
        {
            System.out.println(user);
        }
    }

    @Test
    public void test2() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        UserMapper mapper = context.getBean("userMapper", UserMapper.class);
        mapper.addUser(new User("白白", "123456", "cimonhe@163.com"));
        System.out.println("$$￥￥");
    }

    @Test
    public void test3() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        UserMapper mapper = context.getBean("userMapper", UserMapper.class);
        mapper.deleteUserByName("大大");
        System.out.println("$$￥￥");
    }

    @Test
    public void test4() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        UserMapper mapper = context.getBean("userMapper", UserMapper.class);
        System.out.println(mapper.queryUserByEmail("cimonhe@163.com"));
    }

    @Test
    public void test5() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        UserMapper mapper = context.getBean("userMapper", UserMapper.class);
        Map<String, String> map = new HashMap<>();
        map.put("email", "cimonhe@163.com");
        map.put("password", "123456");
        System.out.println(mapper.queryUserByEmailAndPwd(map));
    }

    @Test
    public void test6() {
        String path = "D:\\ComicWebsite\\uploadComic";
        File file = new File(path);
        File[] tempList = file.listFiles();
        System.out.println("该目录下对象个数：" + tempList.length);
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                System.out.println("文     件：" + tempList[i].getName());
            }
            if (tempList[i].isDirectory()) {
                System.out.println("文件夹：" + tempList[i].getName());
            }
        }
    }

    @Test
    public void test7() {
        String FileName = "D:\\ComicWebsite\\uploadComic\\安生\\bilibili.png";
        File file = new File(FileName);//根据指定的文件名创建File对象


        if (!file.exists()) {  //要删除的文件不存在
            System.out.println("文件" + FileName + "不存在，删除失败！");
        } else { //要删除的文件存在

            deleteFile(FileName);

        }
    }

    @Test
    public void deleteComic() {
        String deleteFileName = "bg.png";


    }

    public static boolean deleteFile(String fileName) {


        File file = new File(fileName);//根据指定的文件名创建File对象

        if (file.exists() && file.isFile()) { //要删除的文件存在且是文件

            if (file.delete()) {
                System.out.println("文件" + fileName + "删除成功！");
                return true;
            } else {
                System.out.println("文件" + fileName + "删除失败！");
                return false;
            }
        } else {

            System.out.println("文件" + fileName + "不存在，删除失败！");
            return false;
        }


    }

    @Test
    public void test8() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        UserMapper mapper = context.getBean("userMapper", UserMapper.class);
        Map<String, Object> map = new HashMap<>();
        map.put("username", "安生");
        map.put("comicName", "星际牛仔");
        map.put("likeNum", 0);
        map.put("tag", "老片");
        System.out.println(mapper.addComic(map));
    }

    @Test
    public void test9() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        UserMapper mapper = context.getBean("userMapper", UserMapper.class);
        Map<String, Object> map = new HashMap<>();
        map.put("username", "安生");
        map.put("comicName", "星际牛仔");
        map.put("chapater", "第一章");
        mapper.addComicChapter(map);
    }

    @Test
    public void test10() {

        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AdminMapper mapper = context.getBean("adminMapper", AdminMapper.class);
        Map<String, String> map = new HashMap<String, String>();
        map.put("adminName", "管理员1");
        map.put("password", "123456");
        Admin admin = mapper.queryAdminByNameAndPwd(map);
        System.out.println(admin);
    }

    @Test
    public void test11() {

        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        PendingComicMapper mapper = context.getBean("pendingComicMapper", PendingComicMapper.class);
        Map<String, String> map = new HashMap<String, String>();
        map.put("adminName", "管理员1");
        map.put("password", "123456");
        PageHelper.startPage(1, 2);
        List<PendingComic> pendingComics = mapper.queryAllPendingComics();
        for (PendingComic pendingComic : pendingComics) {
            System.out.println(pendingComic);
        }
    }

    @Test
    public void test12() {

        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        PendingComicMapper mapper = context.getBean("pendingComicMapper", PendingComicMapper.class);
        PendingComic pendingComic = new PendingComic("安生", "漫画名");
        mapper.addPendingComic(pendingComic);
    }

    @Test
    public void test13() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        PendingComicMapper mapper = context.getBean("pendingComicMapper", PendingComicMapper.class);
        mapper.deletePendingComic("漫画名");
    }

    @Test
    public void test14()
    {
        String startPath = "C:\\Users\\28953\\Pictures\\Saved Pictures\\bilibili.png";
        String endPath = "C:\\Users\\28953\\Pictures\\java\\bilibili.png";
        File oldpaths = new File(startPath);
        File newpaths = new File(endPath);
        try {

            if (!newpaths.exists()) {
                Files.copy(oldpaths.toPath(), newpaths.toPath());
                System.out.println("文件移动成功！起始路径：" + startPath);
            } else {
                newpaths.delete();
                Files.copy(oldpaths.toPath(), newpaths.toPath());
                System.out.println("文件移动成功！起始路径：" + startPath);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test15(){
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        PendingComicMapper mapper = context.getBean("pendingComicMapper", PendingComicMapper.class);
        System.out.println(mapper.getCountAllPendingComics());
    }

    @Test
    public void test16(){
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        LikeComicMapper mapper = context.getBean("likeComicMapper", LikeComicMapper.class);
        System.out.println(mapper.addComicLike(new LikeComic("钱二","星际牛仔")));
        mapper.addComicLike(new LikeComic("白白","星际牛仔"));
        System.out.println(mapper.queryComicLike("星际牛仔"));
    }

    @Test
    public void test17(){
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        ComicMapper mapper = context.getBean("comicMapper", ComicMapper.class);
        mapper.addComic(new Comic("今敏","opus","奇幻"));
        for (Comic comic : mapper.queryAllComic()) {
            System.out.println(comic);
        }
        mapper.updateByComicName(new Comic("今敏","opus","神"));
        for (Comic comic : mapper.queryAllComic()) {
            System.out.println(comic);
        }
        mapper.deleteComicByComicName("opus");
        for (Comic comic : mapper.queryAllComic()) {
            System.out.println(comic);
        }
    }

    @Test
    public void test18(){
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        ComicMapper mapper = context.getBean("comicMapper", ComicMapper.class);
        Map<String,String> map= new HashMap<>();
        map.put("oldTag","奇幻");
        map.put("newTag","分镜炸裂");
        mapper.updateTag(map);
    }

    @Test
    public void test19(){
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        UserMapper mapper = context.getBean("userMapper", UserMapper.class);
        List<User> users =  mapper.queryUserByNameLike("安");
        for (User user : users) {
            System.out.println(user);
        }
    }
}


