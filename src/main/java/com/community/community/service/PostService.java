package com.community.community.service;

import com.community.community.entity.Post;
import javafx.geometry.Pos;

import java.util.List;

public interface PostService {

    public Post getPost();

    public List<Post> findDiscussPosts(int userId, int offset, int limit);

    public int getCountsByUserId(int userId);

    public int getCounts();

    public int addPost(Post post);

    public Post getPostById(int postId);

    public int updateCommentCount(int postId,int commentCount);
}
