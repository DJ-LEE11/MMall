package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse updateOrAddProduct(Product product) {
        if (product == null) {
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_AREUMENT.getCode(), ResponseCode.ILLEGAL_AREUMENT.getDesc());
        }
        if (!StringUtils.isBlank(product.getSubImages())) {
            product.setMainImage(product.getSubImages().split(",")[0]);
        }
        if (product.getId() == null) {
            int resultCount = productMapper.insert(product);
            if (resultCount > 0) {
                return ServerResponse.createByErrorMsg("添加成功");
            } else {
                return ServerResponse.createByErrorMsg("添加失败");

            }
        } else {
            int resultCount = productMapper.updateByPrimaryKeySelective(product);
            if (resultCount > 0) {
                return ServerResponse.createByErrorMsg("更新成功");
            } else {
                return ServerResponse.createByErrorMsg("更新失败");
            }
        }
    }

    @Override
    public ServerResponse updateProductStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByErrorMsg("参数错误");
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int resultCount = productMapper.updateByPrimaryKeySelective(product);
        if (resultCount > 0) {
            return ServerResponse.createBySuccessMsg("更新成功");
        }
        return ServerResponse.createByErrorMsg("更新失败");
    }

    @Override
    public ServerResponse getProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorMsg("参数错误");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMsg("获取失败");
        }
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setParentId(product.getId());
        productDetailVo.setName(product.getName());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setStatus(product.getStatus());
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category != null && category.getParentId() != null) {
            productDetailVo.setParentId(category.getParentId());
            productDetailVo.setCategoryName(category.getName());
        } else {
            productDetailVo.setParentId(0);
        }
        return ServerResponse.createBySuccess(productDetailVo);
    }

    @Override
    public ServerResponse getList(Integer pageIndex, Integer pageSize) {
        PageHelper.startPage(pageIndex, pageSize);
        List<Product> ProductList = productMapper.getList();
        List<ProductListVo> productListVos = new ArrayList<>();
        for (Product product : ProductList) {
            productListVos.add(exchangeProductListVo(product));
        }
        PageInfo pageResult = new PageInfo(ProductList);
        pageResult.setList(productListVos);
        return ServerResponse.createBySuccess(pageResult);
    }

    private ProductListVo exchangeProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setDetail(product.getDetail());
        return productListVo;
    }

    @Override
    public ServerResponse searchProduct(Integer productId,String productName , Integer pageIndex, Integer pageSize) {
        PageHelper.startPage(pageIndex,pageSize);
        if (!StringUtils.isBlank(productName)){
            productName = new StringBuffer().append("%").append(productName).append("%").toString();
        }
        List<Product> products = productMapper.searchProduct(productId, productName);
        List<ProductListVo> productListVos = new ArrayList<>();
        for (Product product : products){
            productListVos.add(exchangeProductListVo(product));
        }
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListVos);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
