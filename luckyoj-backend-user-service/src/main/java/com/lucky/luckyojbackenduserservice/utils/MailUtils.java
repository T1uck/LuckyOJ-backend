package com.lucky.luckyojbackenduserservice.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * @author: 小飞的电脑
 * @Date: 2023/6/10 - 06 - 10 - 17:40
 * @Description: com.itheima.reggie.utils
 * @version: 1.0
 */
public class MailUtils {

    @Autowired
    JavaMailSender javaMailSender;

//    // 发送邮件验证码
//    public static void sendTestMail(String email, String code) {
//        // 1.创建Properties 用于记录邮箱的一些操作
//        Properties pros = new Properties();
//        // 1.1 表示SMTP发送邮件，必须进行身份验证
//        pros.put("mail.smtp.auth", "true");
//        // 1.2 此处填写SMTP服务器
//        pros.put("mail.smtp.host", "smtp.qq.com");
//        // 1.3 端口号，QQ邮箱端口25
//        pros.put("mail.smtp.port", "25");
//        // 1.4 此处填写，写信人的账号
//        pros.put("mail.user", "815845992@qq.com");
//        // 1.5 此处填写16位STMP口令
//        pros.put("mail.password", "jtptljlmzrqybdff");
//
//        // 2.构建授权信息，用于进行SMTP进行身份验证
//        Authenticator authenticator = new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                // 2.1 用户名
//                String userName = pros.getProperty("mail.user");
//                // 2.2 16位STMP口令
//                String password = pros.getProperty("mail.password");
//                return new javax.mail.PasswordAuthentication(userName, password);
//            }
//        };
//
//        // 3.使用环境属性和授权信息，创建邮件会话
//        Session mailSession = Session.getInstance(pros, authenticator);
//        // 4.创建邮件消息对象
//        MimeMessage message = new MimeMessage(mailSession);
//        // 4.1 设置发件人
//        InternetAddress from = new InternetAddress(pros.getProperty("mail.user"));
//        message.setFrom(from);
//        // 4.2 设置收件人
//        InternetAddress to = new InternetAddress(email);
//        message.setRecipient(Message.RecipientType.TO, to);
//        // 4.3 设置邮件标题
//        message.setSubject("【LuckyOJ判题系统】邮箱登录验证码");
//        // 4.4 设置邮件的正文
//        message.setContent("尊敬的用户：您好！\r\n您的登录验证码为：" + code + "（有效期为一分钟，请勿告知他人）", "text/html;charset=UTF-8");
//
//        // 5.最后，发送邮件
//        Transport.send(message);
//    }

    // 获取六位随机验证码
    public static String getCode() {
        // 由于数字 1 、 0 和字母 O 、l 有时分不清楚，所以，没有数字 1 、 0
        String[] beforeShuffle = {"2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F",
                "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a",
                "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
                "w", "x", "y", "z"};
        // 将数组转换成集合
        List<String> list = Arrays.asList(beforeShuffle);
        // 打乱集合顺序，以达到随机的效果
        Collections.shuffle(list);
        // 创建StringBuilder，不是线程安全的
        StringBuilder sb = new StringBuilder();
        // 将集合转变成StringBuilder字符串
        for (String s : list) {
            sb.append(s);
        }
        // 返回sb字符串中第10~17位的5位验证码，这个区间其实随便设的
        return sb.substring(10, 16);
    }
}
