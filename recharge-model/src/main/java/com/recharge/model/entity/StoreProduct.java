package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 店铺商品实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_store_product")
public class StoreProduct extends BaseEntity {

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 关联系统产品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 面值
     */
    private BigDecimal faceValue;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 售价
     */
    private BigDecimal price;

    /**
     * 库存（-1表示无限）
     */
    private Integer stock;

    /**
     * 销量
     */
    private Integer sales;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态: 0-下架 1-上架
     */
    private Integer status;
}
