package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 团队关系实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_team_relation")
public class TeamRelation extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 直属上级ID
     */
    private Long parentId;

    /**
     * 祖先路径（如：1,5,10）
     */
    private String ancestorPath;

    /**
     * 层级深度
     */
    private Integer level;
}
