package com.community.community.controller;

import com.community.community.annotation.LoginRequired;
import com.community.community.entity.User;
import com.community.community.service.UserService;
import com.community.community.util.CommunityUtil;
import com.community.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @LoginRequired
    @RequestMapping("/setting")
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping("/upload")
    public String uploadFile(MultipartFile headerImage, Model model){

        if(headerImage.isEmpty()){
            model.addAttribute("error","请选择图片");
        }
        String originalFilename = headerImage.getOriginalFilename();
        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确");
        }
        //随机文件名称
        String fileName = CommunityUtil.generateUUID() + suffix;
        File dest = new File(uploadPath+"/"+fileName);
        try {
            //上传文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("文件上传失败!文件："+e.getMessage());
            throw new RuntimeException("文件上传失败",e);
        }

        //更新用户头像路径 http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

    @RequestMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器文件路径
        fileName = uploadPath + "/" + fileName;
        //获取后缀名 向浏览器输出文件
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        response.setContentType("image/"+suffix);
        try (
                //自动关闭
                FileInputStream fis = new FileInputStream(fileName);
                ServletOutputStream os = response.getOutputStream();
            ) {
                byte[] buffer = new byte[1024];
                int b = 0;
                while((b = fis.read(buffer)) != -1){
                    os.write(buffer,0,b);
                }
            } catch (IOException e) {
                logger.error("读取头像文件失败"+e);
        }
    }

    @LoginRequired
    @RequestMapping("/updatePassword")
    public String updatePassword(String oldPassword,String newPassword,Model model){
        if(StringUtils.isBlank(oldPassword)){
            model.addAttribute("oldError","旧密码参数为空！");
            return "/site/setting";
        }
        if(StringUtils.isBlank(newPassword)){
            model.addAttribute("newError","新密码参数为空！");
            return "/site/setting";
        }
        //获取用户信息
        User user = hostHolder.getUser();
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if(oldPassword.equals(user.getPassword())){
            userService.updatePassword(user,newPassword);
        }else{
            model.addAttribute("oldError","密码错误！");
            return "/site/setting";
        }
        return "/site/login";
    }

}
