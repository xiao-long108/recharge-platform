package com.recharge.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 店铺商品请求
 */
@Data
public class StoreProductRequest {

    /**
     * 商品ID（编辑时必填）
     */
    private Long id;

    /**
     * 关联系统产品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
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
    @NotNull(message = "面值不能为空")
    @DecimalMin(value = "0.01", message = "面值必须大于0")
    private BigDecimal faceValue;

    /**
     * 原价
     */
    @NotNull(message = "原价不能为空")
    @DecimalMin(value = "0.01", message = "原价必须大于0")
    private BigDecimal originalPrice;

    /**
     * 售价
     */
    @NotNull(message = "售价不能为空")
    @DecimalMin(value = "0.01", message = "售价必须大于0")
    private BigDecimal price;

    /**
     * 库存（-1表示无限）
     */
    private Integer stock = -1;

    /**
     * 排序
     */
    private Integer sortOrder = 0;

    /**
     * 状态: 0-下架 1-上架
     */
    private Integer status = 1;
}
