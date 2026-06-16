package com.java.shopping.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java.shopping.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Update("UPDATE product SET stock = stock - 1 WHERE id = #{productId} AND stock > 0")
    int decreaseStock(@Param("productId") Long productId);

    @Update("UPDATE product SET stock = stock + 1 WHERE id = #{productId}")
    int increaseStock(@Param("productId") Long productId);
}