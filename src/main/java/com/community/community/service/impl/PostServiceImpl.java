package com.community.community.service.impl;

import com.community.community.entity.Post;
import com.community.community.entity.PostExample;
import com.community.community.mapper.PostMapper;
import com.community.community.service.PostService;
import com.community.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    PostMapper postMapper;

    @Autowired
    SensitiveFilter sensitiveFilter;

    @Override
    public Post getPost() {
        return postMapper.selectByPrimaryKey(109);
    }

    @Override
    public List<Post> findDiscussPosts(int userId, int offset, int limit) {
        return postMapper.findDiscussPosts(userId,offset,limit);
    }

    @Override
    public int getCountsByUserId(int userId) {
        PostExample example = new PostExample();
        PostExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(String.valueOf(userId));
        return postMapper.countByExample(example);
    }

    @Override
    public int getCounts() {
        PostExample example = new PostExample();
        PostExample.Criteria criteria = example.createCriteria();
        return postMapper.countByExample(example);
    }

    @Override
    public int addPost(Post post) {

        if(post==null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        //转义html标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return postMapper.insert(post);
    }

    @Override
    public Post getPostById(int postId) {
        return postMapper.selectByPrimaryKey(postId);
    }

    @Override
    public int updateCommentCount(int postId, int commentCount) {
        PostExample example = new PostExample();
        example.createCriteria().andIdEqualTo(postId);
        Post post = new Post();
        post.setCommentCount(commentCount);
        return postMapper.updateByExampleSelective(post,example);
    }
}
