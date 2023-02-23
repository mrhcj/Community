package com.community.community.util;

//注册激活
public class CommunityConstant {

    private CommunityConstant(){};

    //激活成功
    public static int ACTIVATION_SUCCESS = 0;

    //重复激活
    public static int ACTIVATION_REPEAT = 1;

    //激活失败
    public static int ACTIVATION_FAILURE = 2;

    //默认状态登录凭证超时时间
    public static int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    //勾选记住我 登录凭证超时时间
    public static int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    /**
     * 实体类型 帖子
     */
    public static int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型 评论
     */
    public static int ENTITY_TYPE_COMMENT = 2;

}
