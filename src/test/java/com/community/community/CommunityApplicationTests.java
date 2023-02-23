package com.community.community;

import com.community.community.entity.Message;
import com.community.community.mapper.MessageMapper;
import com.community.community.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@SpringBootTest
class CommunityApplicationTests {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessageService messageService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Test
    public void redisTest(){
        redisTemplate.opsForValue().set("jch","cct");
        System.out.println(redisTemplate.opsForValue().get("hcj"));
    }

    @Test
    void contextLoads() {
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        for (Message m :
                messages) {
            System.out.println(m.toString());
        }
        System.out.println("=================================================================");
        int i = messageMapper.selectConversationCount(111);
        System.out.println(i);
        System.out.println("=================================================================");
        List<Message> messages1 = messageMapper.selectLetters("111_112", 0, 20);
        for (Message m :
                messages1) {
            System.out.println(m.toString());
        }
        System.out.println("=================================================================");
        System.out.println(messageService.getLettersCount("111_112"));
        System.out.println("=================================================================");
        System.out.println(messageService.getLetterUnReadCount(131,"111_131"));
    }

}
