package com.community.community.controller;

import com.community.community.entity.Message;
import com.community.community.entity.Page;
import com.community.community.entity.User;
import com.community.community.service.MessageService;
import com.community.community.service.UserService;
import com.community.community.util.CommunityUtil;
import com.community.community.util.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MessageController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(MessageController.class);

    @RequestMapping("/letter")
    public String letter() {
        return "/site/letter";
    }

    @RequestMapping("/letter/list")
    public String getLetterList(Model model, Page page) {

        User user = hostHolder.getUser();
        //分页信息
        page.setLimit(6);
        page.setPath("/letter/list");
        page.setRows(messageService.getConversationCount(user.getId()));
        System.out.println("ROWS:" + page.getRows());
        //会话列表
        List<Message> conversationList = messageService.getConversations(user.getId(), page.getOffset(), page.getLimit());

        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("unreadCount", messageService.getLetterUnReadCount(user.getId(), message.getConversationId()));
                map.put("letterCount", messageService.getLettersCount(message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));
                conversations.add(map);
            }
        }

        model.addAttribute("conversations", conversations);
        //查询用户未读消息数量
        int letterUnReadCount = messageService.getLetterUnReadCount(user.getId(), null);
        model.addAttribute("letterUnReadCount", letterUnReadCount);
        return "/site/letter";
    }

    @RequestMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {

        //分页信息
        page.setLimit(6);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.getLettersCount(conversationId));

        //私信列表
        List<Message> letterList = messageService.getLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }

        model.addAttribute("letters", letters);
        model.addAttribute("target", getLetterTarget(conversationId));
        //将未读设置为已读
        List<Integer> letterIds = getLetterIds(letterList);
        if (!letterIds.isEmpty()) {
            messageService.updateStatus(letterIds);
        }
        return "/site/letter-detail";
    }

    //获取未读消息id列表
    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();
        if (letterList != null) {
            Integer userId = hostHolder.getUser().getId();
            for (Message message : letterList) {
                if (Objects.equals(userId, message.getToId()) && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    @RequestMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content) {

        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtil.getJsonString(1, "目标用户不存在");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setStatus(0);
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.insertMessage(message);

        return CommunityUtil.getJsonString(0);
    }

    private User getLetterTarget(String conversationId) {
        String[] split = conversationId.split("_");
        int id0 = Integer.parseInt(split[0]);
        int id1 = Integer.parseInt(split[1]);
        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }
}
