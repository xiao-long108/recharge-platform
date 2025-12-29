package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 银行账户实体 - 对应原表 cloud_times_api_account
 */
@Data
@TableName("cloud_times_api_account")
public class BankAccount {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 支付方式
     */
    private String type;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 开户名称
     */
    @TableField("account_name")
    private String accountName;

    /**
     * 开户账号
     */
    @TableField("account_number")
    private String accountNumber;

    /**
     * 银行名称
     */
    @TableField("bank_name")
    private String bankName;

    /**
     * 开户支行
     */
    @TableField("bank_branch")
    private String bankBranch;

    /**
     * 是否默认 1:默认; 2:非默认
     */
    @TableField("is_default")
    private Integer isDefault;

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
