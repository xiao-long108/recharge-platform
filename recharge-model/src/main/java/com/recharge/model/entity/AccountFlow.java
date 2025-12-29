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
 * 资金流水实体 - 对应原表 cloud_times_api_fund_detail
 */
@Data
@TableName("cloud_times_api_fund_detail")
public class AccountFlow {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 店铺ID
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 订单号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 提现类型
     */
    private Integer type;

    /**
     * 数据类型 1:申请提款; 2:充值; 3:扣除; 4:转账支出; 5:转账收入; 等等
     */
    @TableField("data_type")
    private Integer dataType;

    /**
     * 充值类型
     */
    @TableField("recharge_type")
    private Integer rechargeType;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 来源用户ID
     */
    @TableField("from_user_id")
    private Long fromUserId;

    /**
     * 关联ID
     */
    @TableField("relation_id")
    private Long relationId;

    /**
     * 是否充值订单
     */
    @TableField("is_pay")
    private Integer isPay;

    /**
     * 金额
     */
    private BigDecimal price;

    /**
     * 实际金额
     */
    @TableField("pay_amount")
    private BigDecimal payAmount;

    /**
     * 消耗氧气
     */
    private BigDecimal oxygen;

    /**
     * 操作前金额 (SQL保留字，不查询)
     */
    @TableField(value = "`before`", select = false)
    private BigDecimal beforeAmount;

    /**
     * 操作后金额 (SQL保留字，不查询)
     */
    @TableField(value = "`after`", select = false)
    private BigDecimal afterAmount;

    /**
     * 状态 1:成功; 2:失败; 3:等待审核;
     */
    private Integer status;

    /**
     * 后台说明
     */
    private String node;

    /**
     * 转账凭证
     */
    private String img;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 拒绝原因
     */
    private String reason;

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
