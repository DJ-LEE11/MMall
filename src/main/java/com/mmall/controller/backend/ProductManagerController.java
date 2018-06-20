package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/product")
public class ProductManagerController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @RequestMapping(value = "update_or_add_product.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse updateOrAddProduct(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMsg("用户未登录");
        }
        if (iUserService.checkAdmin(user).isSuccess()) {
            return iProductService.updateOrAddProduct(product);
        } else {
            return ServerResponse.createByErrorMsg("无权限操作");
        }
    }

    @RequestMapping(value = "update_product_status.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse updateProductStatus(HttpSession session, Integer productId, Integer status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMsg("用户未登录");
        }
        if (iUserService.checkAdmin(user).isSuccess()) {
            return iProductService.updateProductStatus(productId, status);
        } else {
            return ServerResponse.createByErrorMsg("无权限操作");
        }
    }

    @RequestMapping(value = "detail.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMsg("用户未登录");
        }
        if (iUserService.checkAdmin(user).isSuccess()) {
            return iProductService.getProductDetail(productId);
        } else {
            return ServerResponse.createByErrorMsg("无权限操作");
        }
    }

    @RequestMapping(value = "get_list.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMsg("用户未登录");
        }
        if (iUserService.checkAdmin(user).isSuccess()) {
            return iProductService.getList(pageIndex, pageSize);
        } else {
            return ServerResponse.createByErrorMsg("无权限操作");
        }
    }

    @RequestMapping(value = "search_product.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse searchProduct(HttpSession session,
                                        Integer productId,
                                        String productName,
                                        @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMsg("用户未登录");
        }
        if (iUserService.checkAdmin(user).isSuccess()) {
            return iProductService.searchProduct(productId,productName,pageIndex,pageSize);
        } else {
            return ServerResponse.createByErrorMsg("无权限操作");
        }
    }
}
