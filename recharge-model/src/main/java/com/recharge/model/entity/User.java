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
 * 用户实体 - 对应原表 cloud_times_api_user
 */
@Data
@TableName("cloud_times_api_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String head;

    /**
     * 密码
     */
    private String password;

    /**
     * 支付密码
     */
    @TableField("pay_password")
    private String payPassword;

    /**
     * 性别 0:男 1:女
     */
    private Integer sex;

    /**
     * 上级用户ID
     */
    @TableField("parent_user_id")
    private Long parentUserId;

    /**
     * 邀请码
     */
    @TableField("invite_code")
    private String inviteCode;

    /**
     * 店铺ID
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 可用金额(余额)
     */
    private BigDecimal price;

    /**
     * 总佣金
     */
    @TableField("income_price")
    private BigDecimal incomePrice;

    /**
     * 可提余额
     */
    @TableField("cash_price")
    private BigDecimal cashPrice;

    /**
     * 用户等级
     */
    private Integer level;

    /**
     * 邀请人数
     */
    private Integer invitees;

    /**
     * 真实姓名
     */
    private String username;

    /**
     * 身份证号
     */
    @TableField("id_card")
    private String idCard;

    /**
     * 身份证正面
     */
    @TableField("id_card_front")
    private String idCardFront;

    /**
     * 身份证反面
     */
    @TableField("id_card_back")
    private String idCardBack;

    /**
     * 状态 0:未实名 1:正常 2:冻结 3:审核中 4:审核不通过
     */
    private Integer status;

    /**
     * Token
     */
    private String token;

    /**
     * 登录IP
     */
    @TableField("login_ip")
    private String loginIp;

    /**
     * 登录时间(时间戳)
     */
    @TableField("login_time")
    private Integer loginTime;

    /**
     * 错误次数
     */
    @TableField("error_times")
    private Integer errorTimes;

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
