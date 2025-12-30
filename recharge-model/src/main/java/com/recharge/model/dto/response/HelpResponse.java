package com.recharge.model.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 帮助中心响应
 */
@Data
public class HelpResponse {

    /**
     * 分类列表
     */
    private List<CategoryItem> categories;

    @Data
    public static class CategoryItem {
        private Long id;
        private String name;
        private String icon;
        private List<ArticleItem> articles;
    }

    @Data
    public static class ArticleItem {
        private Long id;
        private String title;
        private String content;
        private Integer viewCount;
    }
}
