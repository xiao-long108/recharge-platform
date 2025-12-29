package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体 - 对应原表 cloud_times_api_orders
 */
@Data
@TableName("cloud_times_api_orders")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 类型：0店铺充值，1首页充值
     */
    private Integer type;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 店铺ID
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 铺货ID
     */
    @TableField("store_goods_id")
    private Long storeGoodsId;

    /**
     * 商品ID
     */
    @TableField("goods_id")
    private Long goodsId;

    /**
     * 优惠券ID
     */
    @TableField("coupon_id")
    private Integer couponId;

    /**
     * 销售编号
     */
    @TableField("sale_no")
    private String saleNo;

    /**
     * 充值手机号
     */
    private String mobile;

    /**
     * 成本价
     */
    private BigDecimal price;

    /**
     * 支付ID
     */
    @TableField("pay_id")
    private Integer payId;

    /**
     * 实付金额
     */
    @TableField("pay_amount")
    private BigDecimal payAmount;

    /**
     * 支付方式
     */
    @TableField("pay_method")
    private String payMethod;

    /**
     * 线下支付
     */
    private Integer offonline;

    /**
     * 支付状态 0未付款 1已付款
     */
    @TableField("pay_status")
    private Integer payStatus;

    /**
     * 利润
     */
    private BigDecimal profit;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 收货/完成时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 删除时间
     */
    @TableLogic
    @TableField("delete_time")
    private LocalDateTime deleteTime;
}
