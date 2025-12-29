package com.recharge.web.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.result.Result;
import com.recharge.mapper.ProductMapper;
import com.recharge.model.entity.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 管理后台 - 产品管理控制器
 */
@Tag(name = "管理后台-产品管理")
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductMapper productMapper;

    @Operation(summary = "产品列表")
    @GetMapping
    public Result<Page<Product>> list(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "disable", required = false) Integer disable) {

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(disable != null, Product::getDisable, disable);
        wrapper.orderByAsc(Product::getPrice);

        Page<Product> page = productMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }

    @Operation(summary = "产品详情")
    @GetMapping("/{productId}")
    public Result<Product> detail(@PathVariable Long productId) {
        Product product = productMapper.selectById(productId);
        return Result.success(product);
    }

    @Operation(summary = "添加产品")
    @PostMapping
    public Result<Void> add(@RequestBody ProductRequest request) {
        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setPrice(request.getPrice());
        product.setRealPrice(request.getRealPrice());
        product.setDisable(request.getDisable() != null ? request.getDisable() : 1);
        product.setAddtime(LocalDateTime.now());

        productMapper.insert(product);
        return Result.success();
    }

    @Operation(summary = "修改产品")
    @PutMapping("/{productId}")
    public Result<Void> update(@PathVariable Long productId,
                               @RequestBody ProductRequest request) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return Result.fail("产品不存在");
        }

        if (request.getTitle() != null) {
            product.setTitle(request.getTitle());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getRealPrice() != null) {
            product.setRealPrice(request.getRealPrice());
        }
        if (request.getDisable() != null) {
            product.setDisable(request.getDisable());
        }
        product.setAddtime(LocalDateTime.now());

        productMapper.updateById(product);
        return Result.success();
    }

    @Operation(summary = "删除产品")
    @DeleteMapping("/{productId}")
    public Result<Void> delete(@PathVariable Long productId) {
        productMapper.deleteById(productId);
        return Result.success();
    }

    @Operation(summary = "启用/禁用产品")
    @PutMapping("/{productId}/toggle")
    public Result<Void> toggle(@PathVariable Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return Result.fail("产品不存在");
        }

        // 切换状态: 1=启用, 0=禁用
        product.setDisable(product.getDisable() == 1 ? 0 : 1);
        product.setAddtime(LocalDateTime.now());
        productMapper.updateById(product);
        return Result.success();
    }

    @Data
    public static class ProductRequest {
        private String title;
        private Integer price;
        private Integer realPrice;
        private Integer disable;
    }
}
