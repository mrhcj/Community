package com.community.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.community.community.entity.Comment;
import com.community.community.entity.Page;
import com.community.community.entity.Post;
import com.community.community.entity.User;
import com.community.community.service.CommentService;
import com.community.community.service.PostService;
import com.community.community.service.UserService;
import com.community.community.util.CommunityConstant;
import com.community.community.util.CommunityUtil;
import com.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping("/add")
    @ResponseBody
    public String addPost(String title,String content){
        User user = hostHolder.getUser();
        //判断登录状态
        if(user == null){
            return CommunityUtil.getJsonString(403,"需要登录");
        }
        Post post = new Post();
        post.setUserId(user.getId().toString());
        post.setTitle(title);
        post.setContent(content);
        post.setType(0);
        post.setStatus(0);
        post.setCommentCount(0);
        post.setCreateTime(new Date());
        postService.addPost(post);

        return CommunityUtil.getJsonString(0,"发布成功");
    }

    @RequestMapping("/detail/{postId}")
    public String getPostDetail(@PathVariable("postId") int postId, Model model, Page page){
        //贴子
        Post post = postService.getPostById(postId);
        model.addAttribute("post",post);
        //作者
        User user = userService.findUserById(Integer.parseInt(post.getUserId()));
        model.addAttribute("user",user);

        //评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/"+postId);
        page.setRows(post.getCommentCount());

        List<Map<String,Object>> commentList = new ArrayList<>();
        List<Comment> comments = commentService
                .getCommentByEntity(CommunityConstant.ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());

        //评论列表
        if(comments!=null){
            for (Comment comment: comments) {
                Map<String,Object> commentVo = new HashMap<>();
                commentVo.put("comment",comment);
                commentVo.put("user",userService.findUserById(comment.getUserId()));
                //回复列表
                List<Map<String,Object>> replyList = new ArrayList<>();
                List<Comment> replyComments = commentService.getCommentByEntity(CommunityConstant.ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                if(replyComments!=null){
                    for (Comment reply: replyComments) {
                        Map<String,Object> replyVo = new HashMap<>();
                        replyVo.put("reply",reply);
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        //回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);
                        replyList.add(replyVo);
                    }
                }
                commentVo.put("replys",replyList);
                //回复数量
                int counts = commentService.getCounts(CommunityConstant.ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount",counts);
                commentList.add(commentVo);
            }
        }

        model.addAttribute("comments",commentList);
        return "/site/discuss-detail";
    }
}
