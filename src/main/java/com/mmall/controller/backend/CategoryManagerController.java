package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller()
@RequestMapping("/manage/category")
public class CategoryManagerController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCategory(HttpSession session,String categoryName,@RequestParam(value = "parentId",defaultValue = "0") Integer parentId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"用户没登录");
        }
        ServerResponse<String> checkAdminResponse = iUserService.checkAdmin(user);
        if (checkAdminResponse.isSuccess()){
            return iCategoryService.addCategory(categoryName,parentId);
        }else {
            return checkAdminResponse;
        }
    }

    @RequestMapping(value = "update_category_name.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse updateCategoryName(HttpSession session,Integer categoryId,String categoryName){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"用户没登录");
        }
        ServerResponse<String> checkAdminResponse = iUserService.checkAdmin(user);
        if (checkAdminResponse.isSuccess()){
            return iCategoryService.updateCategoryName(categoryId,categoryName);
        }else {
            return checkAdminResponse;
        }
    }

    @RequestMapping(value = "get_category_list.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getCategoryListById(HttpSession session,Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"用户没登录");
        }
        ServerResponse<String> checkAdminResponse = iUserService.checkAdmin(user);
        if (checkAdminResponse.isSuccess()){
            return iCategoryService.getCategoryListById(categoryId);
        }else {
            return checkAdminResponse;
        }
    }

    @RequestMapping(value = "get_category_list_child.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getCategoryListChildById(HttpSession session,Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"用户没登录");
        }
        ServerResponse<String> checkAdminResponse = iUserService.checkAdmin(user);
        if (checkAdminResponse.isSuccess()){
            return iCategoryService.getCategoryListChildById(categoryId);
        }else {
            return checkAdminResponse;
        }
    }
}
