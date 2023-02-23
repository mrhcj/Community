package com.community.community.service.impl;

import com.community.community.entity.LoginTicket;
import com.community.community.entity.LoginTicketExample;
import com.community.community.entity.User;
import com.community.community.entity.UserExample;
import com.community.community.mapper.LoginTicketMapper;
import com.community.community.mapper.UserMapper;
import com.community.community.service.UserService;
import com.community.community.util.CommunityConstant;
import com.community.community.util.CommunityUtil;
import com.community.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public User findUserById(int userId) {

        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    public Map<String, Object> register(User user) {

        Map<String, Object> map = new HashMap<>();
        if(user==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        //验证账号是否已注册
        UserExample example = new UserExample();
        example.createCriteria()
                .andUsernameEqualTo(user.getUsername());
        if(!userMapper.selectByExample(example).isEmpty()){
            map.put("usernameMsg","该账号已存在");
            return map;
        }
        //邮箱验证
        UserExample example1 = new UserExample();
        example1.createCriteria()
                .andEmailEqualTo(user.getEmail());
        if(!userMapper.selectByExample(example1).isEmpty()){
            map.put("emailMsg","该邮箱已注册");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl("http://images.nowcoder.com/head/"+new Random().nextInt(1000)+"t.png");
        user.setCreateTime(new Date());

        userMapper.insert(user);

        //发送邮件验证
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        //激活链接 http://localhost:8080/community/activation/userId/code
        example.createCriteria().andUsernameEqualTo(user.getUsername());
        List<User> users = userMapper.selectByExample(example);
        Integer userId = users.get(0).getId();
        String url = domain + contextPath + "/activation/" + userId + "/" + user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }

    //激活逻辑处理
    public int activation(int userId, String code){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user.getStatus() == 1){
            return CommunityConstant.ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            UserExample example = new UserExample();
            example.createCriteria().andIdEqualTo(userId);
            User record = new User();
            record.setStatus(1);
            userMapper.updateByExampleSelective(record,example);
            return CommunityConstant.ACTIVATION_SUCCESS;
        }else{
            return CommunityConstant.ACTIVATION_FAILURE;
        }
    }

    @Override
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String,Object> map = new HashMap<>();
        //数据验证
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空！");
            return map;
        }
        //验证账号
        UserExample example = new UserExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<User> users = userMapper.selectByExample(example);
        if(users.isEmpty()){
            map.put("usernameMsg","账号不存在！");
            return map;
        }
        User user = users.get(0);
        if(user.getStatus()==0){
            map.put("usernameMsg","该账号未激活！");
            return map;
        }
        //验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码错误！");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+ expiredSeconds* 1000L));
        loginTicketMapper.insert(loginTicket);

        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    @Override
    public LoginTicket findLoginTicket(String ticket) {
        LoginTicketExample loginTicketExample =  new LoginTicketExample();
        loginTicketExample.createCriteria().andTicketEqualTo(ticket);
        List<LoginTicket> loginTickets = loginTicketMapper.selectByExample(loginTicketExample);
        if(!loginTickets.isEmpty()){
            return loginTickets.get(0);
        }
        return null;
    }

    @Override
    public void logout(String ticket) {
        LoginTicketExample loginTicketExample = new LoginTicketExample();
        loginTicketExample.createCriteria().andTicketEqualTo(ticket);
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setStatus(1);
        loginTicketMapper.updateByExampleSelective(loginTicket,loginTicketExample);
    }

    @Override
    public int updateHeader(int userId, String headerUrl) {
        UserExample example = new UserExample();
        example.createCriteria().andIdEqualTo(userId);
        User user = new User();
        user.setHeaderUrl(headerUrl);
        return userMapper.updateByExampleSelective(user, example);
    }

    @Override
    public int updatePassword(User user, String newPassword) {
        UserExample example = new UserExample();
        example.createCriteria().andIdEqualTo(user.getId());
        User record = new User();
        record.setPassword(CommunityUtil.md5(newPassword+user.getSalt()));
        return userMapper.updateByExampleSelective(record,example);
    }

    @Override
    public User findUserByName(String username) {
        UserExample example = new UserExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<User> users = userMapper.selectByExample(example);
        return users.isEmpty() ? null : users.get(0);
    }
}
