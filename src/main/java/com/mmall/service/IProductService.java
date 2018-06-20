package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;

public interface IProductService {

    ServerResponse updateOrAddProduct(Product product);

    ServerResponse updateProductStatus(Integer productId,Integer status);

    ServerResponse getProductDetail(Integer productId);

    ServerResponse getList(Integer pageIndex, Integer pageSize);

    ServerResponse searchProduct(Integer productId,String productName , Integer pageIndex, Integer pageSize);
}
