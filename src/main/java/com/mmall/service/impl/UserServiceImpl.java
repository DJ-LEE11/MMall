package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.UUID;

import static com.mmall.common.Const.FORGET_TOKEN;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String userName, String password) {
        int resultCount = userMapper.checkUserName(userName);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMsg("用户不存在");
        }
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(userName, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMsg("密码不存在");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);
    }

    @Override
    public ServerResponse<String> register(User user){
        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USER_NAME);
        if (!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if (!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0){
            return ServerResponse.createByErrorMsg("注册失败");
        }
        return ServerResponse.createBySuccess("注册成功");

    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            if (Const.USER_NAME.equals(type)) {
                int resultCount = userMapper.checkUserName(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMsg("用户名已存在");
                }
            }
            if (Const.EMAIL.equals(type)) {
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMsg("email已存在");
                }
            }
        }else {
            return ServerResponse.createByErrorMsg("参数错误");
        }
        return ServerResponse.createBySuccess("检验成功");
    }

    @Override
    public ServerResponse<String> selectQuestion(String userName){
        ServerResponse validResponse = this.checkValid(userName,Const.USER_NAME);
        if (validResponse.isSuccess()){
            return ServerResponse.createByErrorMsg("用户不存在");
        }
        String question = userMapper.selectQuestionByUserName(userName);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMsg("找回密码的问题为空");
    }

    @Override
    public ServerResponse<String> checkAnswer(String userName,String question,String answer){
        int resultCount = userMapper.checkAnswer(userName,question,answer);
        if (resultCount>0){
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(FORGET_TOKEN+userName,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMsg("问题的答案错误");
    }

    @Override
    public ServerResponse<String> forgetRestPassword(String userName,String passwordNew,String forgetToken){
        if (StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMsg("token参数错误");
        }
        ServerResponse validResponse = this.checkValid(userName,Const.USER_NAME);
        if (validResponse.isSuccess()){
            return ServerResponse.createByErrorMsg("用户不存在");
        }
        String token = TokenCache.getKey(FORGET_TOKEN+userName);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMsg("token无效");
        }
        if (StringUtils.equals(token,forgetToken)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int resultCount = userMapper.updatePasswordByUserName(userName,md5Password);
            if (resultCount>0){
                return ServerResponse.createBySuccess("修改密码成功");
            }else {
                return ServerResponse.createByErrorMsg("token错误，请重亲获取");
            }
        }
        return ServerResponse.createByErrorMsg("修改密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if (resultCount== 0){
            return ServerResponse.createByErrorMsg("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        resultCount = userMapper.updateByPrimaryKeySelective(user);
        if (resultCount>0){
            return ServerResponse.createBySuccessMsg("密码更新成功");
        }else {
            return ServerResponse.createByErrorMsg("密码更新失败");
        }
    }

    @Override
    public ServerResponse<User> updateInformation(User user){
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if (resultCount>0){
            return ServerResponse.createByErrorMsg("邮箱已存在");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount>0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMsg("更新个人信息失败");
    }

    @Override
    public ServerResponse<User> getUserInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null){
            return ServerResponse.createByErrorMsg("当前用户不存在");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    @Override
    public ServerResponse<String> checkAdmin(User user){
        if (user == null){
            return ServerResponse.createByErrorMsg("用户未登录");
        }
        if (user.getRole() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccessMsg("该用户是管理者");
        }
        return ServerResponse.createByErrorMsg("该用户不是管理者");
    }

}
