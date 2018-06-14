package com.mmall.common;

public class Const {

    public static final String CURRENT_USER = "CURRENT_USER";

    public static final String EMAIL = "EMAIL";
    public static final String USER_NAME = "USER_NAME";

    public static  final String FORGET_TOKEN = "FORGET_TOKEN";

    public interface Role{
        int ROLE_CUSTOMER = 0;//普通用户
        int ROLE_ADMIN = 1;//管理员
    }
}
