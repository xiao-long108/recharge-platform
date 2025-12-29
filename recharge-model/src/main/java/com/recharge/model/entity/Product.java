package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 话费产品实体 - 对应原表 cloud_times_api_huafei
 */
@Data
@TableName("cloud_times_api_huafei")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    private String title;

    /**
     * 卖价(面值)
     */
    private Integer price;

    /**
     * 实际到账
     */
    @TableField("real_price")
    private Integer realPrice;

    /**
     * 禁用状态 1:启用
     */
    private Integer disable;

    /**
     * 编辑时间
     */
    private LocalDateTime addtime;
}
