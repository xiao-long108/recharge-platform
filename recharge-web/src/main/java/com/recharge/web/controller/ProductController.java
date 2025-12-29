package com.recharge.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recharge.common.result.Result;
import com.recharge.mapper.ProductMapper;
import com.recharge.model.entity.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品控制器 - 适配原数据库 cloud_times_api_huafei
 */
@Tag(name = "产品管理", description = "充值产品")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductMapper productMapper;

    @Operation(summary = "产品列表")
    @GetMapping
    public Result<List<Product>> listProducts() {
        // 原数据库字段: disable=1 表示启用
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getDisable, 1)
                .orderByAsc(Product::getPrice);
        return Result.success(productMapper.selectList(wrapper));
    }

    @Operation(summary = "产品详情")
    @GetMapping("/{productId}")
    public Result<Product> getProduct(@PathVariable Long productId) {
        return Result.success(productMapper.selectById(productId));
    }
}
