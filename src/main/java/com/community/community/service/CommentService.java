package com.community.community.service;

import com.community.community.entity.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> getCommentByEntity(int entityType,int entityId,int offset,int limit);

    int getCounts(int entityType,int entityId);

    int insertComment(Comment comment);
}
