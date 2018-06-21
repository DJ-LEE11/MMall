package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    public ServerResponse add(Integer userId, Integer productId, Integer addCount) {
        if (userId == null || productId == null) {
            return ServerResponse.createByErrorMsg("参数错误");
        }
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {//若没有更新到购物车
            Cart cartTemp = new Cart();
            cartTemp.setUserId(userId);
            cartTemp.setProductId(productId);
            cartTemp.setQuantity(addCount);
            cartTemp.setChecked(Const.Cart.CHECK);
            cartMapper.insert(cartTemp);
        } else {//若有更新数量
            addCount = cart.getQuantity() + addCount;
            cart.setQuantity(addCount);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return ServerResponse.createBySuccess(updateCart(userId));
    }

    private CartVo updateCart(Integer userId) {
        CartVo cartVo = new CartVo();
        BigDecimal totalPrice = new BigDecimal("0");
        List<CartProductVo> cartProductVoList = new ArrayList<>();

        List<Cart> carts = cartMapper.selectCartByUserId(userId);
        if (CollectionUtils.isNotEmpty(carts)) {
            for (Cart cartItem : carts) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setChecked(cartItem.getChecked());
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null) {
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyCount = 0;
                    if (product.getStock() >= cartItem.getQuantity()) {
                        buyCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_SUCCESS);
                    }else {
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_FAIL);
                        buyCount = product.getStock();
                        Cart cartTemp = new Cart();
                        cartItem.setId(cartItem.getId());
                        cartItem.setQuantity(buyCount);
                        cartMapper.updateByPrimaryKeySelective(cartTemp);
                    }
                    cartProductVo.setQuantity(buyCount);
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),buyCount));
                }
                if (cartItem.getChecked() == Const.Cart.CHECK){
                    totalPrice = BigDecimalUtil.add(totalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllPrice(totalPrice);
        cartVo.setAllCheck(checkAll(userId));
        return cartVo;
    }

    private boolean checkAll(Integer userId){
        if (userId == null){
            return false;
        }
        return cartMapper.checkAllByUserId(userId) == 0;
    }

    public ServerResponse delete(Integer userId,String productIds){
        List<String> productId = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productId)){
            return ServerResponse.createByErrorMsg("参数错误");
        }
        cartMapper.delete(userId,productId);
        return ServerResponse.createBySuccess(updateCart(userId));
    }

    public ServerResponse cartCount(Integer userId){
        if (userId == null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.cartCount(userId));
    }
}
