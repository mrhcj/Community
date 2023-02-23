package com.community.community.controller;

import com.community.community.entity.Post;
import com.community.community.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/test")
@RestController
public class testController {

    @Autowired
    PostService postService;

    @RequestMapping("/test01")
    public String test01(){
        List<Post> posts = postService.findDiscussPosts(101,1,4);
        return posts.toString();
    }

    @RequestMapping("/test02")
    public int test02(){
        return postService.getCountsByUserId(101);
    }
    
}
