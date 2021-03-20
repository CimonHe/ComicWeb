package com.CimonHe.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Data
@AllArgsConstructor
public class SendEmailTask extends Thread {

    JavaMailSender javaMailSender;
    int verificationCode;
    String receiver;
    private void sendMail(JavaMailSender javaMailSender, int verificationCode, String receiver) throws Exception
    {

        MimeMessage mMessage = javaMailSender.createMimeMessage();//创建邮件对象
        MimeMessageHelper mMessageHelper;
        Properties prop = new Properties();
        String from;
        //从配置文件中拿到发件人邮箱地址
        prop.load(this.getClass().getResourceAsStream("/email.properties"));
        from = prop.get("mail.smtp.username") + "";
        mMessageHelper = new MimeMessageHelper(mMessage, true);
        mMessageHelper.setFrom(from);//发件人邮箱
        mMessageHelper.setTo(receiver);//收件人邮箱
        mMessageHelper.setSubject("西漫网注册验证码");//邮件的主题
        mMessageHelper.setText("<p>"+verificationCode+"</p>", true);//邮件的文本内容，true表示文本以html格式打开
        javaMailSender.send(mMessage);//发送邮件
    }


    @Override
    public void run() {
        try {
            sendMail(javaMailSender,verificationCode,receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
