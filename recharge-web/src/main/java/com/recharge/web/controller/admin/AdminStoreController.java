package com.recharge.web.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.result.Result;
import com.recharge.mapper.StoreMapper;
import com.recharge.model.entity.Store;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理后台 - 店铺管理控制器
 */
@Tag(name = "管理后台-店铺管理")
@RestController
@RequestMapping("/api/admin/stores")
@RequiredArgsConstructor
public class AdminStoreController {

    private final StoreMapper storeMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Operation(summary = "店铺列表")
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "storeName", required = false) String storeName,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "openStatus", required = false) Integer openStatus) {

        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();

        // 支持 keyword 或 storeName 搜索
        String searchKey = keyword != null ? keyword : storeName;
        if (searchKey != null && !searchKey.isEmpty()) {
            wrapper.and(w -> w
                    .like(Store::getSName, searchKey)
                    .or()
                    .like(Store::getSMobile, searchKey));
        }

        // 审核状态筛选
        if (status != null) {
            wrapper.eq(Store::getSStatus, status);
        }

        // 开通状态筛选
        if (openStatus != null) {
            wrapper.eq(Store::getSOpenStatus, openStatus);
        }

        wrapper.orderByDesc(Store::getCreateTime);

        Page<Store> page = storeMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        // 转换为前端格式
        List<Map<String, Object>> records = page.getRecords().stream()
                .map(this::convertToFrontendFormat)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", page.getTotal());
        result.put("size", page.getSize());
        result.put("current", page.getCurrent());

        return Result.success(result);
    }

    /**
     * 转换为前端格式
     */
    private Map<String, Object> convertToFrontendFormat(Store store) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", store.getId());
        map.put("userId", store.getUserId());
        map.put("sName", store.getSName());
        map.put("sMobile", store.getSMobile());
        map.put("sAddress", store.getSAddress());
        map.put("sInfo", store.getSInfo());
        map.put("sLogo", store.getSLogo());
        map.put("sLevel", store.getSLevel());
        map.put("sPrice", store.getSPrice());
        map.put("sSales", store.getSSales());
        map.put("sRating", store.getSRating());
        map.put("sOpenStatus", store.getSOpenStatus());
        map.put("sStatus", store.getSStatus());
        map.put("createTime", store.getCreateTime() != null ? store.getCreateTime().format(FORMATTER) : null);
        map.put("updateTime", store.getUpdateTime() != null ? store.getUpdateTime().format(FORMATTER) : null);
        return map;
    }

    @Operation(summary = "店铺详情")
    @GetMapping("/{storeId}")
    public Result<Map<String, Object>> detail(@PathVariable("storeId") Long storeId) {
        Store store = storeMapper.selectById(storeId);
        if (store == null) {
            return Result.fail("店铺不存在");
        }
        return Result.success(convertToFrontendFormat(store));
    }

    @Operation(summary = "店铺统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();

        // 总店铺数
        Long total = storeMapper.selectCount(null);
        stats.put("total", total);

        // === 审核状态统计 (s_status) ===
        // 待审核 (s_status = 0)
        Long pending = storeMapper.selectCount(
            new LambdaQueryWrapper<Store>().eq(Store::getSStatus, 0)
        );
        stats.put("pending", pending);

        // 审核通过/正常营业 (s_status = 1)
        Long approved = storeMapper.selectCount(
            new LambdaQueryWrapper<Store>().eq(Store::getSStatus, 1)
        );
        stats.put("approved", approved);

        // 冻结 (s_status = 2)
        Long frozen = storeMapper.selectCount(
            new LambdaQueryWrapper<Store>().eq(Store::getSStatus, 2)
        );
        stats.put("frozen", frozen);

        // 审核拒绝 (s_status = 3)
        Long rejected = storeMapper.selectCount(
            new LambdaQueryWrapper<Store>().eq(Store::getSStatus, 3)
        );
        stats.put("rejected", rejected);

        // === 开通状态统计 (s_open_status) ===
        // 正常开通 (s_open_status = 1)
        Long open = storeMapper.selectCount(
            new LambdaQueryWrapper<Store>().eq(Store::getSOpenStatus, 1)
        );
        stats.put("open", open);

        // 已关闭 (s_open_status = 2)
        Long closed = storeMapper.selectCount(
            new LambdaQueryWrapper<Store>().eq(Store::getSOpenStatus, 2)
        );
        stats.put("closed", closed);

        // 本月销售额 (暂时返回0，需要关联订单表计算)
        stats.put("monthSales", 0);

        return Result.success(stats);
    }

    @Operation(summary = "更新审核状态 (s_status: 0=待审核, 1=通过, 2=冻结, 3=拒绝)")
    @PutMapping("/{storeId}/status")
    public Result<Void> updateStatus(@PathVariable("storeId") Long storeId,
                                      @RequestBody StatusRequest request) {
        Store store = storeMapper.selectById(storeId);
        if (store == null) {
            return Result.fail("店铺不存在");
        }

        store.setSStatus(request.getStatus());
        storeMapper.updateById(store);
        return Result.success();
    }

    @Operation(summary = "更新开通状态 (s_open_status: 1=正常开通, 2=关闭)")
    @PutMapping("/{storeId}/open-status")
    public Result<Void> updateOpenStatus(@PathVariable("storeId") Long storeId,
                                          @RequestBody StatusRequest request) {
        Store store = storeMapper.selectById(storeId);
        if (store == null) {
            return Result.fail("店铺不存在");
        }

        store.setSOpenStatus(request.getStatus());
        storeMapper.updateById(store);
        return Result.success();
    }

    @Operation(summary = "审核店铺")
    @PutMapping("/{storeId}/audit")
    public Result<Void> audit(@PathVariable("storeId") Long storeId,
                              @RequestBody AuditRequest request) {
        Store store = storeMapper.selectById(storeId);
        if (store == null) {
            return Result.fail("店铺不存在");
        }

        // 1=通过, 2=拒绝
        store.setSStatus(request.getApproved() ? 1 : 3);
        storeMapper.updateById(store);
        return Result.success();
    }

    @Data
    public static class StatusRequest {
        private Integer status;
    }

    @Data
    public static class AuditRequest {
        private Boolean approved;
        private String reason;
    }
}
