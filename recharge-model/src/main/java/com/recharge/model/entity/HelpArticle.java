package com.recharge.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 帮助文章实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_help_article")
public class HelpArticle extends BaseEntity {

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态: 0-隐藏 1-显示
     */
    private Integer status;
}
