package com.recharge.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.result.Result;
import com.recharge.model.dto.request.OpenStoreRequest;
import com.recharge.model.entity.Store;
import com.recharge.service.store.StoreService;
import com.recharge.web.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 店铺控制器 - 适配原数据库 cloud_times_api_store
 */
@Tag(name = "店铺管理")
@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @Operation(summary = "获取我的店铺")
    @GetMapping("/my")
    public Result<Store> getMyStore(@CurrentUser Long userId) {
        Store store = storeService.getMyStore(userId);
        return Result.success(store);
    }

    @Operation(summary = "开通店铺")
    @PostMapping("/open")
    public Result<Store> openStore(@CurrentUser Long userId,
                                   @RequestBody OpenStoreRequest request) {
        // storeLogo 暂时传 null
        Store store = storeService.openStore(userId, request.getStoreName(),
                null, request.getStoreDescription());
        return Result.success(store);
    }

    @Operation(summary = "更新店铺信息")
    @PutMapping("/update")
    public Result<Void> updateStore(@CurrentUser Long userId,
                                    @RequestBody OpenStoreRequest request) {
        // storeLogo 暂时传 null
        storeService.updateStore(userId, request.getStoreName(), null, request.getStoreDescription());
        return Result.success();
    }

    @Operation(summary = "获取店铺列表")
    @GetMapping("/list")
    public Result<List<Store>> listStores(@RequestParam(required = false) String keyword,
                                          @RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Store> page = storeService.pageStores(keyword, pageNum, pageSize);
        return Result.success(page.getRecords());
    }

    @Operation(summary = "获取店铺详情")
    @GetMapping("/{storeId}")
    public Result<Store> getStoreDetail(@PathVariable Long storeId) {
        Store store = storeService.getStoreDetail(storeId);
        return Result.success(store);
    }
}
