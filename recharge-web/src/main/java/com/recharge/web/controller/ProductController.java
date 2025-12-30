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
 * 产品控制器 - 用户端产品接口
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
        // 适配老表: disable=1表示启用, 按price(面值)升序
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getDisable, 1)
                .orderByAsc(Product::getPrice);
        return Result.success(productMapper.selectList(wrapper));
    }

    @Operation(summary = "产品详情")
    @GetMapping("/{productId}")
    public Result<Product> getProduct(@PathVariable Long productId) {
        Product product = productMapper.selectById(productId);
        // 适配老表: disable=1表示启用
        if (product == null || product.getDisable() != 1) {
            return Result.fail("产品不存在或已下架");
        }
        return Result.success(product);
    }
}
