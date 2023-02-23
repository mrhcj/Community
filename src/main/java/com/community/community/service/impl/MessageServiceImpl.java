package com.community.community.service.impl;

import com.community.community.entity.Message;
import com.community.community.entity.MessageExample;
import com.community.community.mapper.MessageMapper;
import com.community.community.service.MessageService;
import com.community.community.util.SensitiveFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<Message> getConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId,offset,limit);
    }

    @Override
    public int getConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    @Override
    public List<Message> getLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId,offset,limit);
    }

    @Override
    public int getLettersCount(String conversationId) {
        MessageExample example = new MessageExample();
        example.createCriteria()
                .andStatusNotEqualTo(2)
                .andFromIdNotEqualTo(1)
                .andConversationIdEqualTo(conversationId);
        return messageMapper.countByExample(example);
    }

    @Override
    public int getLetterUnReadCount(int userId, String conversationId) {
        MessageExample example = new MessageExample();
        MessageExample.Criteria criteria = example.createCriteria()
                .andStatusEqualTo(0)
                .andFromIdNotEqualTo(1)
                .andToIdEqualTo(userId);
        if(!StringUtils.isBlank(conversationId)){
            criteria.andConversationIdEqualTo(conversationId);
        }
        return messageMapper.countByExample(example);
    }

    @Override
    public int insertMessage(Message message) {
        //敏感词过滤
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insert(message);
    }

    @Override
    public int updateStatus(List<Integer> ids) {
        return  messageMapper.updateStatus(ids,1);
    }
}
