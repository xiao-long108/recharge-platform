package com.recharge.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.exception.BusinessException;
import com.recharge.common.result.PageResult;
import com.recharge.common.result.Result;
import com.recharge.model.dto.request.StoreProductRequest;
import com.recharge.model.entity.Store;
import com.recharge.model.entity.StoreProduct;
import com.recharge.service.store.StoreService;
import com.recharge.service.storeproduct.StoreProductService;
import com.recharge.web.config.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 店铺商品控制器
 */
@Tag(name = "店铺商品管理", description = "店铺商品的增删改查")
@RestController
@RequestMapping("/api/store/products")
@RequiredArgsConstructor
public class StoreProductController {

    private final StoreProductService storeProductService;
    private final StoreService storeService;

    @Operation(summary = "添加商品")
    @PostMapping
    public Result<Long> addProduct(@CurrentUser Long userId,
                                   @Valid @RequestBody StoreProductRequest request) {
        Store store = getMyStore(userId);
        Long productId = storeProductService.addProduct(store.getId(), request);
        return Result.success(productId);
    }

    @Operation(summary = "更新商品")
    @PutMapping
    public Result<Void> updateProduct(@CurrentUser Long userId,
                                       @Valid @RequestBody StoreProductRequest request) {
        Store store = getMyStore(userId);
        storeProductService.updateProduct(store.getId(), request);
        return Result.success();
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{productId}")
    public Result<Void> deleteProduct(@CurrentUser Long userId,
                                       @PathVariable Long productId) {
        Store store = getMyStore(userId);
        storeProductService.deleteProduct(store.getId(), productId);
        return Result.success();
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{productId}")
    public Result<StoreProduct> getProduct(@PathVariable Long productId) {
        return Result.success(storeProductService.getProduct(productId));
    }

    @Operation(summary = "分页获取店铺商品列表")
    @GetMapping
    public Result<PageResult<StoreProduct>> pageProducts(
            @CurrentUser Long userId,
            @Parameter(description = "状态: 0-下架 1-上架") @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Store store = getMyStore(userId);
        Page<StoreProduct> result = storeProductService.pageProducts(store.getId(), status, page, size);
        return Result.success(PageResult.of(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent()));
    }

    @Operation(summary = "获取店铺上架商品列表（店铺详情页）")
    @GetMapping("/store/{storeId}")
    public Result<List<StoreProduct>> listOnSaleProducts(@PathVariable Long storeId) {
        return Result.success(storeProductService.listOnSaleProducts(storeId));
    }

    @Operation(summary = "更新商品状态")
    @PutMapping("/{productId}/status")
    public Result<Void> updateStatus(@CurrentUser Long userId,
                                      @PathVariable Long productId,
                                      @RequestParam Integer status) {
        Store store = getMyStore(userId);
        storeProductService.updateStatus(store.getId(), productId, status);
        return Result.success();
    }

    /**
     * 获取当前用户的店铺
     */
    private Store getMyStore(Long userId) {
        Store store = storeService.getMyStore(userId);
        if (store == null) {
            throw new BusinessException("您还未开通店铺");
        }
        return store;
    }
}
