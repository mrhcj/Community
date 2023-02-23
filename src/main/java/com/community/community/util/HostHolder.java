package com.community.community.util;

import com.community.community.entity.User;
import org.springframework.stereotype.Component;


/**
 * 代替Session存储数据
 */
@Component
public class HostHolder {

    private final ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
