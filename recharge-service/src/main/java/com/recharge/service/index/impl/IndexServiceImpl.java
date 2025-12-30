package com.recharge.service.index.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recharge.mapper.*;
import com.recharge.model.dto.response.IndexResponse;
import com.recharge.model.entity.*;
import com.recharge.service.coupon.CouponService;
import com.recharge.service.index.IndexService;
import com.recharge.service.sign.SignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 首页服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IndexServiceImpl implements IndexService {

    private final BannerMapper bannerMapper;
    private final NoticeMapper noticeMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    private final SystemConfigMapper systemConfigMapper;
    private final CouponService couponService;
    private final SignService signService;

    @Override
    public IndexResponse getIndexData(Long userId) {
        IndexResponse response = new IndexResponse();

        // 获取轮播图
        response.setBanners(getBanners());

        // 获取公告
        response.setNotices(getNotices());

        // 获取热门产品
        response.setHotProducts(getHotProducts());

        // 获取用户信息
        if (userId != null) {
            response.setUserInfo(getUserBrief(userId));
        }

        // 获取系统配置
        response.setConfig(getConfig());

        return response;
    }

    /**
     * 获取轮播图
     */
    private List<IndexResponse.BannerItem> getBanners() {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        // 使用真实的数据库字段：type=1为首页轮播，delete_time为null表示未删除
        wrapper.eq(Banner::getType, 1)
                .isNull(Banner::getDeleteTime)
                .orderByAsc(Banner::getSort);

        List<Banner> banners = bannerMapper.selectList(wrapper);

        return banners.stream().map(banner -> {
            IndexResponse.BannerItem item = new IndexResponse.BannerItem();
            item.setId(banner.getId());
            item.setTitle(banner.getTitle());
            item.setImage(banner.getImage());
            item.setLinkType(banner.getLinkType());
            item.setLinkUrl(banner.getLinkUrl());
            item.setLinkId(banner.getLinkId());
            return item;
        }).collect(Collectors.toList());
    }

    /**
     * 获取公告
     */
    private List<IndexResponse.NoticeItem> getNotices() {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        // 使用真实的数据库字段：delete_time为null表示未删除
        wrapper.isNull(Notice::getDeleteTime)
                .orderByDesc(Notice::getSort)
                .orderByDesc(Notice::getCreateTime)
                .last("LIMIT 5");

        List<Notice> notices = noticeMapper.selectList(wrapper);

        return notices.stream().map(notice -> {
            IndexResponse.NoticeItem item = new IndexResponse.NoticeItem();
            item.setId(notice.getId());
            item.setTitle(notice.getTitle());
            item.setContent(notice.getContent());
            item.setNoticeType(notice.getNoticeType());
            return item;
        }).collect(Collectors.toList());
    }

    /**
     * 获取热门产品
     * 适配老表 cloud_times_api_huafei
     */
    private List<IndexResponse.ProductItem> getHotProducts() {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getDisable, 1)  // disable=1 表示启用
                .orderByAsc(Product::getPrice)
                .last("LIMIT 8");

        List<Product> products = productMapper.selectList(wrapper);

        return products.stream().map(product -> {
            IndexResponse.ProductItem item = new IndexResponse.ProductItem();
            item.setId(product.getId());
            item.setTitle(product.getTitle());
            item.setDescription(null);  // 老表没有description字段
            item.setFaceValue(product.getFaceValue());
            item.setPrice(product.getSalePrice());  // realPrice -> salePrice
            item.setDiscountTag(null);  // 老表没有discountTag字段
            item.setSales(0);  // 老表没有sales字段
            return item;
        }).collect(Collectors.toList());
    }

    /**
     * 获取用户简要信息
     */
    private IndexResponse.UserBrief getUserBrief(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }

        IndexResponse.UserBrief userBrief = new IndexResponse.UserBrief();
        userBrief.setId(user.getId());
        userBrief.setNickname(user.getNickname());
        userBrief.setAvatar(user.getHead());       // 使用head字段作为头像
        userBrief.setBalance(user.getPrice());     // 使用price字段作为余额

        // 获取可用优惠券数量
        Integer couponCount = couponService.countAvailable(userId);
        userBrief.setCouponCount(couponCount);

        // 获取今日签到状态
        userBrief.setSignedToday(signService.hasSignedToday(userId));

        return userBrief;
    }

    /**
     * 获取系统配置
     */
    private IndexResponse.ConfigInfo getConfig() {
        IndexResponse.ConfigInfo config = new IndexResponse.ConfigInfo();

        config.setCustomerServiceUrl(getConfigValue("customer_service_url", ""));
        config.setShareTitle(getConfigValue("share_title", "话费充值平台"));
        config.setShareDesc(getConfigValue("share_desc", "话费充值低至9.5折"));
        config.setShareImage(getConfigValue("share_image", ""));

        return config;
    }

    /**
     * 获取配置值
     */
    private String getConfigValue(String key, String defaultValue) {
        SystemConfig config = systemConfigMapper.selectByKey(key);
        return config != null ? config.getConfigValue() : defaultValue;
    }
}
