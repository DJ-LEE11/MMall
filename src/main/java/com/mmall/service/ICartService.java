package com.mmall.service;

import com.mmall.common.ServerResponse;

public interface ICartService {

    ServerResponse add(Integer userId, Integer productId, Integer addCount);

    ServerResponse delete(Integer userId,String productIds);

    ServerResponse cartCount(Integer userId);
}
