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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理后台 - 产品管理控制器
 * 适配老表 cloud_times_api_huafei
 */
@Tag(name = "管理后台-产品管理")
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductMapper productMapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Operation(summary = "产品列表")
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "disable", required = false) Integer disable) {

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(title != null && !title.isEmpty(), Product::getTitle, title);

        // 筛选状态: disable=1表示启用, disable=0表示禁用
        if (disable != null) {
            wrapper.eq(Product::getDisable, disable);
        } else if (status != null) {
            wrapper.eq(Product::getDisable, status);
        }

        wrapper.orderByAsc(Product::getPrice);

        Page<Product> page = productMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        // 转换为前端兼容格式
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

    @Operation(summary = "产品详情")
    @GetMapping("/{productId}")
    public Result<Map<String, Object>> detail(@PathVariable("productId") Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return Result.fail("产品不存在");
        }
        return Result.success(convertToFrontendFormat(product));
    }

    @Operation(summary = "添加产品")
    @PostMapping
    public Result<Void> add(@RequestBody ProductRequest request) {
        Product product = new Product();
        product.setTitle(request.getTitle());

        // 设置价格: 优先使用前端传来的price, 否则用faceValue
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice().intValue());
        } else if (request.getFaceValue() != null) {
            product.setPrice(request.getFaceValue().intValue());
        }

        // 设置实际价格: 优先使用realPrice, 否则用salePrice
        if (request.getRealPrice() != null) {
            product.setRealPrice(request.getRealPrice().intValue());
        } else if (request.getSalePrice() != null) {
            product.setRealPrice(request.getSalePrice().intValue());
        } else if (request.getPrice() != null) {
            product.setRealPrice(request.getPrice().intValue());
        }

        // 设置状态: disable=1表示启用
        if (request.getDisable() != null) {
            product.setDisable(request.getDisable());
        } else if (request.getStatus() != null) {
            product.setDisable(request.getStatus());
        } else {
            product.setDisable(1);
        }

        // 设置添加时间为当前时间
        product.setAddtime(LocalDateTime.now());

        productMapper.insert(product);
        return Result.success();
    }

    @Operation(summary = "修改产品")
    @PutMapping("/{productId}")
    public Result<Void> update(@PathVariable("productId") Long productId,
                               @RequestBody ProductRequest request) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return Result.fail("产品不存在");
        }

        if (request.getTitle() != null) {
            product.setTitle(request.getTitle());
        }

        // 更新价格
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice().intValue());
        } else if (request.getFaceValue() != null) {
            product.setPrice(request.getFaceValue().intValue());
        }

        if (request.getRealPrice() != null) {
            product.setRealPrice(request.getRealPrice().intValue());
        } else if (request.getSalePrice() != null) {
            product.setRealPrice(request.getSalePrice().intValue());
        }

        // 更新状态
        if (request.getDisable() != null) {
            product.setDisable(request.getDisable());
        } else if (request.getStatus() != null) {
            product.setDisable(request.getStatus());
        }

        // 更新时间
        product.setAddtime(LocalDateTime.now());

        productMapper.updateById(product);
        return Result.success();
    }

    @Operation(summary = "删除产品")
    @DeleteMapping("/{productId}")
    public Result<Void> delete(@PathVariable("productId") Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return Result.fail("产品不存在");
        }
        // 老表没有deleted字段,直接物理删除
        productMapper.deleteById(productId);
        return Result.success();
    }

    @Operation(summary = "启用/禁用产品")
    @PutMapping("/{productId}/toggle")
    public Result<Void> toggle(@PathVariable("productId") Long productId) {
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

    /**
     * 转换为前端兼容格式
     */
    private Map<String, Object> convertToFrontendFormat(Product product) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", product.getId());
        map.put("title", product.getTitle());

        // 前端期望的字段
        map.put("price", product.getPrice());
        map.put("realPrice", product.getRealPrice());
        map.put("disable", product.getDisable());

        // 新格式字段 (兼容)
        map.put("faceValue", product.getPrice() != null ? BigDecimal.valueOf(product.getPrice()) : null);
        map.put("salePrice", product.getRealPrice() != null ? BigDecimal.valueOf(product.getRealPrice()) : null);
        map.put("status", product.getDisable());

        // 时间格式化 - addtime 现在是 LocalDateTime 类型
        if (product.getAddtime() != null) {
            String formattedTime = product.getAddtime().format(FORMATTER);
            map.put("addtime", formattedTime);
            map.put("createdTime", formattedTime);
            map.put("updatedTime", formattedTime);
        } else {
            map.put("addtime", "");
            map.put("createdTime", null);
            map.put("updatedTime", null);
        }

        return map;
    }

    @Data
    public static class ProductRequest {
        private String title;
        // 新格式
        private BigDecimal faceValue;
        private BigDecimal salePrice;
        private Integer status;
        // 旧格式 (兼容)
        private BigDecimal price;      // 对应 faceValue
        private BigDecimal realPrice;  // 对应 salePrice
        private Integer disable;       // 对应 status
    }
}
