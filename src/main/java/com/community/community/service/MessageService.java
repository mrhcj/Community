package com.community.community.service;

import com.community.community.entity.Message;

import java.util.List;

public interface MessageService {

    //查询用户会话列表
    List<Message> getConversations(int userId, int offset, int limit);

    //查询用户会话数量
    int getConversationCount(int userId);

    //查询会话私信列表
    List<Message> getLetters(String conversationId, int offset, int limit);

    //查询会话私信数量
    int getLettersCount(String conversationId);

    //查询未读私信数量
    int getLetterUnReadCount(int userId, String conversationId);

    int insertMessage(Message message);

    int updateStatus(List<Integer> ids);
}
