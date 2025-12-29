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
 * 店铺实体 - 对应原表 cloud_times_api_store
 */
@Data
@TableName("cloud_times_api_store")
public class Store {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 店铺名称
     */
    @TableField("s_name")
    private String sName;

    /**
     * 手机号
     */
    @TableField("s_mobile")
    private String sMobile;

    /**
     * 地址
     */
    @TableField("s_address")
    private String sAddress;

    /**
     * 描述
     */
    @TableField("s_info")
    private String sInfo;

    /**
     * Logo
     */
    @TableField("s_logo")
    private String sLogo;

    /**
     * 等级
     */
    @TableField("s_level")
    private Integer sLevel;

    /**
     * 店铺余额
     */
    @TableField("s_price")
    private BigDecimal sPrice;

    /**
     * 销量
     */
    @TableField("s_sales")
    private Integer sSales;

    /**
     * 评分
     */
    @TableField("s_rating")
    private BigDecimal sRating;

    /**
     * 店铺积分
     */
    @TableField("s_integral")
    private Integer sIntegral;

    /**
     * 余额宝余额
     */
    @TableField("s_yuebao_price")
    private BigDecimal sYuebaoPrice;

    /**
     * 余额宝转入总额
     */
    @TableField("s_yuebao_total")
    private BigDecimal sYuebaoTotal;

    /**
     * 余额宝累计收益
     */
    @TableField("s_yuebao_income")
    private BigDecimal sYuebaoIncome;

    /**
     * 身份证号
     */
    @TableField("s_idcard")
    private String sIdcard;

    /**
     * 身份证正面
     */
    @TableField("s_front_image")
    private String sFrontImage;

    /**
     * 身份证背面
     */
    @TableField("s_back_image")
    private String sBackImage;

    /**
     * 开通状态
     */
    @TableField("s_open_status")
    private Integer sOpenStatus;

    /**
     * 审核状态
     */
    @TableField("s_status")
    private Integer sStatus;

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
