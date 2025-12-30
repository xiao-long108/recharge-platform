package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产品实体 - 对应原表 cloud_times_api_huafei
 */
@Data
@TableName("cloud_times_api_huafei")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 产品名称
     */
    private String title;

    /**
     * 面值 (用户支付金额)
     */
    private Integer price;

    /**
     * 实际到账金额
     */
    @TableField("real_price")
    private Integer realPrice;

    /**
     * 状态: 0-禁用 1-启用
     */
    private Integer disable;

    /**
     * 添加时间
     */
    private LocalDateTime addtime;

    // ========== 兼容方法 ==========

    /**
     * 获取面值 (兼容新代码)
     */
    public BigDecimal getFaceValue() {
        return price != null ? BigDecimal.valueOf(price) : null;
    }

    /**
     * 设置面值 (兼容新代码)
     */
    public void setFaceValue(BigDecimal faceValue) {
        this.price = faceValue != null ? faceValue.intValue() : null;
    }

    /**
     * 获取售价 (兼容新代码)
     */
    public BigDecimal getSalePrice() {
        return realPrice != null ? BigDecimal.valueOf(realPrice) : null;
    }

    /**
     * 设置售价 (兼容新代码)
     */
    public void setSalePrice(BigDecimal salePrice) {
        this.realPrice = salePrice != null ? salePrice.intValue() : null;
    }

    /**
     * 获取状态 (兼容新代码: disable=1表示启用, 对应status=1)
     */
    public Integer getStatus() {
        return disable;
    }

    /**
     * 设置状态 (兼容新代码)
     */
    public void setStatus(Integer status) {
        this.disable = status;
    }

    /**
     * 获取删除状态 (老表没有删除字段,返回0)
     */
    public Integer getDeleted() {
        return 0;
    }

    /**
     * 设置删除状态 (老表没有删除字段,忽略)
     */
    public void setDeleted(Integer deleted) {
        // 老表没有deleted字段,忽略
    }
}
