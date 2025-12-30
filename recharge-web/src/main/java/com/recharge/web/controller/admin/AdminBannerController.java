package com.recharge.web.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.result.Result;
import com.recharge.mapper.BannerMapper;
import com.recharge.model.entity.Banner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理后台 - 轮播图管理控制器
 * 对应原表: cloud_times_api_image
 */
@Tag(name = "管理后台-轮播图管理")
@RestController
@RequestMapping("/api/admin/banners")
@RequiredArgsConstructor
public class AdminBannerController {

    private final BannerMapper bannerMapper;

    @Operation(summary = "轮播图列表")
    @GetMapping
    public Result<List<Map<String, Object>>> list(
            @RequestParam(name = "position", required = false) Integer position,
            @RequestParam(name = "status", required = false) Integer status) {

        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();

        // position: 1=首页轮播, 2=店铺轮播, 3=活动页 (对应数据库 type 字段)
        if (position != null) {
            wrapper.eq(Banner::getType, position);
        }

        // status: 1=启用, 0=禁用 (通过 deleteTime 判断)
        if (status != null) {
            if (status == 1) {
                wrapper.isNull(Banner::getDeleteTime);
            } else {
                wrapper.isNotNull(Banner::getDeleteTime);
            }
        } else {
            // 默认只显示启用的
            wrapper.isNull(Banner::getDeleteTime);
        }

        wrapper.orderByAsc(Banner::getSort)
                .orderByDesc(Banner::getCreateTime);

        List<Banner> banners = bannerMapper.selectList(wrapper);

        // 转换为前端格式（直接返回数组）
        List<Map<String, Object>> records = banners.stream()
                .map(this::convertToFrontendFormat)
                .collect(Collectors.toList());

        return Result.success(records);
    }

    @Operation(summary = "轮播图统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();

        Long total = bannerMapper.selectCount(null);
        stats.put("totalBanners", total);

        // 首页轮播图 (type=1)
        Long homeBanners = bannerMapper.selectCount(
            new LambdaQueryWrapper<Banner>().eq(Banner::getType, 1)
        );
        stats.put("homeBanners", homeBanners);
        stats.put("enabledBanners", total);
        stats.put("disabledBanners", 0L);

        return Result.success(stats);
    }

    @Operation(summary = "轮播图详情")
    @GetMapping("/{bannerId}")
    public Result<Map<String, Object>> detail(@PathVariable("bannerId") Long bannerId) {
        Banner banner = bannerMapper.selectById(bannerId);
        if (banner == null) {
            return Result.fail("轮播图不存在");
        }
        return Result.success(convertToFrontendFormat(banner));
    }

    @Operation(summary = "添加轮播图")
    @PostMapping
    public Result<Void> add(@RequestBody BannerRequest request) {
        Banner banner = new Banner();
        banner.setTitle(request.getTitle());
        banner.setImage(request.getImageValue());
        banner.setLinkurl(request.getLinkUrl());
        banner.setSort(request.getSortValue());
        // position 直接使用数字: 1=首页轮播, 2=店铺轮播, 3=活动页
        banner.setType(request.getPosition() != null ? request.getPosition() : 1);
        banner.setCreateTime(LocalDateTime.now());
        banner.setUpdateTime(LocalDateTime.now());

        bannerMapper.insert(banner);
        return Result.success();
    }

    @Operation(summary = "修改轮播图")
    @PutMapping("/{bannerId}")
    public Result<Void> update(@PathVariable("bannerId") Long bannerId,
                               @RequestBody BannerRequest request) {
        Banner banner = bannerMapper.selectById(bannerId);
        if (banner == null) {
            return Result.fail("轮播图不存在");
        }

        if (request.getTitle() != null) {
            banner.setTitle(request.getTitle());
        }
        // 支持 imageUrl 和 image
        String imageValue = request.getImageValue();
        if (imageValue != null) {
            banner.setImage(imageValue);
        }
        if (request.getLinkUrl() != null) {
            banner.setLinkurl(request.getLinkUrl());
        }
        // position 直接使用数字
        if (request.getPosition() != null) {
            banner.setType(request.getPosition());
        }
        // 支持 sort 和 sortOrder
        Integer sortValue = request.getSortValue();
        if (request.getSort() != null || request.getSortOrder() != null) {
            banner.setSort(sortValue);
        }
        banner.setUpdateTime(LocalDateTime.now());

        bannerMapper.updateById(banner);
        return Result.success();
    }

    @Operation(summary = "上架/下架轮播图")
    @PutMapping("/{bannerId}/toggle")
    public Result<Void> toggle(@PathVariable("bannerId") Long bannerId) {
        Banner banner = bannerMapper.selectById(bannerId);
        if (banner == null) {
            return Result.fail("轮播图不存在");
        }

        // 老表没有状态字段，使用软删除实现
        if (banner.getDeleteTime() == null) {
            banner.setDeleteTime(LocalDateTime.now());
        } else {
            banner.setDeleteTime(null);
        }
        banner.setUpdateTime(LocalDateTime.now());
        bannerMapper.updateById(banner);
        return Result.success();
    }

    @Operation(summary = "删除轮播图")
    @DeleteMapping("/{bannerId}")
    public Result<Void> delete(@PathVariable("bannerId") Long bannerId) {
        Banner banner = bannerMapper.selectById(bannerId);
        if (banner == null) {
            return Result.fail("轮播图不存在");
        }

        // 物理删除
        bannerMapper.deleteById(bannerId);
        return Result.success();
    }

    /**
     * 转换为前端格式
     */
    private Map<String, Object> convertToFrontendFormat(Banner banner) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", banner.getId());
        map.put("title", banner.getTitle());
        map.put("imageUrl", banner.getImage());  // 前端期望 imageUrl
        map.put("image", banner.getImage());  // 兼容
        map.put("linkType", banner.getLinkurl() != null && !banner.getLinkurl().isEmpty() ? 2 : 0);
        map.put("linkUrl", banner.getLinkurl());
        map.put("linkId", null);
        // 前端期望 position 为数字: 1=首页轮播, 2=店铺轮播, 3=活动页
        map.put("position", banner.getType() != null ? banner.getType() : 1);
        map.put("sort", banner.getSort());  // 前端期望 sort
        map.put("sortOrder", banner.getSort());  // 兼容
        map.put("status", banner.getDeleteTime() == null ? 1 : 0);
        map.put("createdTime", banner.getCreateTime());
        map.put("updatedTime", banner.getUpdateTime());
        return map;
    }

    @Data
    public static class BannerRequest {
        private String title;
        private String image;
        private String imageUrl;  // 前端发送 imageUrl
        private Integer linkType;
        private String linkUrl;
        private Long linkId;
        private Integer position;  // 前端发送数字: 1/2/3
        private Integer sort;  // 前端发送 sort
        private Integer sortOrder;  // 兼容
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer status;

        // 获取图片URL，优先使用 imageUrl
        public String getImageValue() {
            return imageUrl != null ? imageUrl : image;
        }

        // 获取排序值，优先使用 sort
        public Integer getSortValue() {
            return sort != null ? sort : (sortOrder != null ? sortOrder : 0);
        }
    }
}
