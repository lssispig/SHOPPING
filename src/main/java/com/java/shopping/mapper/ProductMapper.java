package com.java.shopping.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java.shopping.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Update("UPDATE product SET stock = stock - 1 WHERE id = #{productId} AND stock > 0")
    int decreaseStock(@Param("productId") Long productId);

    @Update("UPDATE product SET stock = stock + 1 WHERE id = #{productId}")
    int increaseStock(@Param("productId") Long productId);

    @Update("UPDATE product SET stock = stock + #{amount} WHERE id = #{productId}")
    int increaseStockByAmount(@Param("productId") Long productId, @Param("amount") int amount);

    @Select("SELECT * FROM product WHERE end_time < NOW() AND seckill_stock > 0")
    List<Product> selectExpiredSeckill();

    @Update("UPDATE product SET seckill_stock = 0 WHERE id = #{productId}")
    int markSeckillEnded(@Param("productId") Long productId);
}