package com.community.community.service.impl;

import com.community.community.entity.Comment;
import com.community.community.entity.CommentExample;
import com.community.community.entity.Post;
import com.community.community.entity.PostExample;
import com.community.community.mapper.CommentMapper;
import com.community.community.mapper.PostMapper;
import com.community.community.service.CommentService;
import com.community.community.util.CommunityConstant;
import com.community.community.util.SensitiveFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentMapper  commentMapper;

    @Autowired
    SensitiveFilter sensitiveFilter;

    @Autowired
    PostMapper postMapper;

    @Override
    public List<Comment> getCommentByEntity(int entityType, int entityId, int offset, int limit) {
        List<Comment> comments = commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
        System.out.println(comments.toString());
        return comments;
    }

    @Override
    public int getCounts(int entityType, int entityId) {
        CommentExample example = new CommentExample();
        example.createCriteria()
                .andEntityTypeEqualTo(entityType)
                .andEntityIdEqualTo(entityId);
        return commentMapper.countByExample(example);
    }

    //设置事务隔离
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    @Override
    public int insertComment(Comment comment) {

        if(comment==null){
            throw new IllegalArgumentException("参数不能为空");
        }

        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));

        //添加评论
        int rows = commentMapper.insert(comment);

        //更新post表中对应的评论数量
        if(comment.getEntityType() == CommunityConstant.ENTITY_TYPE_POST){
            //查询最新评论数量
            CommentExample example = new CommentExample();
            example.createCriteria()
                    .andEntityIdEqualTo(comment.getEntityId())
                    .andEntityTypeEqualTo(comment.getEntityType());
            int counts = commentMapper.countByExample(example);
            //更新评论数量
            Post post = new Post();
            post.setId(comment.getEntityId());
            post.setCommentCount(counts);
            postMapper.updateByPrimaryKeySelective(post);
        }
        return rows;
    }
}
