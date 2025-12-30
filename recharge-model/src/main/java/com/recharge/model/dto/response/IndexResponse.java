package com.recharge.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 首页数据响应
 */
@Data
public class IndexResponse {

    /**
     * 轮播图列表
     */
    private List<BannerItem> banners;

    /**
     * 公告列表
     */
    private List<NoticeItem> notices;

    /**
     * 热门产品列表
     */
    private List<ProductItem> hotProducts;

    /**
     * 用户信息（已登录时返回）
     */
    private UserBrief userInfo;

    /**
     * 系统配置
     */
    private ConfigInfo config;

    @Data
    public static class BannerItem {
        private Long id;
        private String title;
        private String image;
        private Integer linkType;
        private String linkUrl;
        private Long linkId;
    }

    @Data
    public static class NoticeItem {
        private Long id;
        private String title;
        private String content;
        private Integer noticeType;
    }

    @Data
    public static class ProductItem {
        private Long id;
        private String title;
        private String description;
        private BigDecimal faceValue;
        private BigDecimal price;
        private String discountTag;
        private Integer sales;
    }

    @Data
    public static class UserBrief {
        private Long id;
        private String nickname;
        private String avatar;
        private BigDecimal balance;
        private Integer couponCount;
        private Boolean signedToday;
    }

    @Data
    public static class ConfigInfo {
        private String customerServiceUrl;
        private String shareTitle;
        private String shareDesc;
        private String shareImage;
    }
}
