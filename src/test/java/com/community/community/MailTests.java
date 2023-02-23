package com.community.community;

import com.community.community.util.MailClient;
import com.community.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    //普通文本
    @Test
    public void MailSendTest(){
        //发送邮箱 标题 内容
        mailClient.sendMail("1937026980@qq.com","test","hello success!");
    }

    //html文本
    @Test
    public void HtmlMailTest(){
        Context context = new Context();
        context.setVariable("username","sunday");
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail("1937026980@qq.com","Test",content);
    }

    //敏感词过滤测试
    @Test
    public void KeyWordTest(){
        String text = sensitiveFilter.filter("吸&&毒护肤会发生贩毒富商大贾|嫖||娼|");
        System.out.println(text);
    }
}
