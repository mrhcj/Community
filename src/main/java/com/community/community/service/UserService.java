package com.community.community.service;

import com.community.community.entity.LoginTicket;
import com.community.community.entity.User;

import java.util.Map;

public interface UserService {

    public User findUserById(int userId);

    public Map<String,Object> register(User user);

    public int activation(int userId, String code);

    public Map<String,Object> login(String username,String password,int expiredSeconds);

    public LoginTicket findLoginTicket(String ticket);

    public void logout(String ticket);

    public int updateHeader(int userId,String headerUrl);

    public int updatePassword(User user,String newPassword);

    public User findUserByName(String username);
}
