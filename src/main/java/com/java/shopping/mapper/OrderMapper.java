package com.java.shopping.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java.shopping.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Select("SELECT * FROM orders WHERE status = 1 AND create_time < DATE_SUB(NOW(), INTERVAL 30 MINUTE)")
    List<Order> selectTimeoutOrders();

    @Update("UPDATE orders SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Select("SELECT * FROM orders WHERE order_no = #{orderNo} AND user_id = #{userId}")
    Order selectByOrderNo(@Param("orderNo") String orderNo, @Param("userId") Long userId);

    @Update("UPDATE orders SET status = #{status} WHERE order_no = #{orderNo} AND status = 1")
    int updateOrderStatus(@Param("orderNo") String orderNo, @Param("status") Integer status);
}