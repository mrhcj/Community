package com.community.community.controller;

import com.community.community.entity.Comment;
import com.community.community.entity.User;
import com.community.community.service.CommentService;
import com.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping("/add/{postId}")
    public String getComments(@PathVariable("postId") int postId,Comment comment){
        User user = hostHolder.getUser();
        if(comment.getTargetId()==null){
            comment.setTargetId(0);
        }
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.insertComment(comment);
        return "redirect:/discuss/detail/"+postId;
    }
}
