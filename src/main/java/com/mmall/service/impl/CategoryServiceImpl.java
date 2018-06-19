package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService{

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse addCategory(String categoryName,Integer parentId){
        if (parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMsg("参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        int resultCount = categoryMapper.insert(category);
        if (resultCount>0){
            return ServerResponse.createBySuccessMsg("分类添加成功");
        }
        return ServerResponse.createByErrorMsg("分类添加失败");
    }

    @Override
    public ServerResponse updateCategoryName(Integer categoryId,String categoryName){
        if (categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMsg("参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int resultCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (resultCount>0){
            return ServerResponse.createBySuccessMsg("修改成功");
        }
        return ServerResponse.createByErrorMsg("修改失败");
    }

    @Override
    public ServerResponse<List<Category>> getCategoryListById(Integer categoryId){
        if (categoryId == null){
            return ServerResponse.createByErrorMsg("参数错误");
        }
        List<Category> categoryList = categoryMapper.getCategoryListById(categoryId);
        if (CollectionUtils.isEmpty(categoryList)){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    @Override
    public ServerResponse getCategoryListChildById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet,categoryId);
        List<Integer> categoryIdList = Lists.newArrayList();
        if (categoryId !=null){
            for (Category category : categorySet){
                categoryIdList.add(category.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    private Set<Category> findChildCategory(Set<Category> categorySet,int categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category !=null){
            categorySet.add(category);
        }
        List<Category> categoryList = categoryMapper.getCategoryListById(categoryId);
        for (Category categoryItem : categoryList){
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }}
