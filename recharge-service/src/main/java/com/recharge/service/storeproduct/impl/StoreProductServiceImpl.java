package com.recharge.service.storeproduct.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.exception.BusinessException;
import com.recharge.mapper.StoreProductMapper;
import com.recharge.model.dto.request.StoreProductRequest;
import com.recharge.model.entity.StoreProduct;
import com.recharge.service.storeproduct.StoreProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 店铺商品服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StoreProductServiceImpl implements StoreProductService {

    private final StoreProductMapper storeProductMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addProduct(Long storeId, StoreProductRequest request) {
        StoreProduct product = new StoreProduct();
        product.setStoreId(storeId);
        product.setProductId(request.getProductId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setImage(request.getImage());
        product.setFaceValue(request.getFaceValue());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock() != null ? request.getStock() : -1);
        product.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        product.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        product.setSales(0);

        storeProductMapper.insert(product);
        log.info("店铺{}添加商品{}成功", storeId, product.getId());

        return product.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Long storeId, StoreProductRequest request) {
        if (request.getId() == null) {
            throw new BusinessException("商品ID不能为空");
        }

        StoreProduct product = storeProductMapper.selectById(request.getId());
        if (product == null || product.getDeleted() == 1) {
            throw new BusinessException("商品不存在");
        }
        if (!product.getStoreId().equals(storeId)) {
            throw new BusinessException("无权操作该商品");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setImage(request.getImage());
        product.setFaceValue(request.getFaceValue());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setPrice(request.getPrice());
        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }
        if (request.getSortOrder() != null) {
            product.setSortOrder(request.getSortOrder());
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }

        storeProductMapper.updateById(product);
        log.info("店铺{}更新商品{}成功", storeId, product.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long storeId, Long productId) {
        StoreProduct product = storeProductMapper.selectById(productId);
        if (product == null || product.getDeleted() == 1) {
            throw new BusinessException("商品不存在");
        }
        if (!product.getStoreId().equals(storeId)) {
            throw new BusinessException("无权操作该商品");
        }

        storeProductMapper.deleteById(productId);
        log.info("店铺{}删除商品{}成功", storeId, productId);
    }

    @Override
    public StoreProduct getProduct(Long productId) {
        StoreProduct product = storeProductMapper.selectById(productId);
        if (product == null || product.getDeleted() == 1) {
            throw new BusinessException("商品不存在");
        }
        return product;
    }

    @Override
    public Page<StoreProduct> pageProducts(Long storeId, Integer status, Integer page, Integer size) {
        Page<StoreProduct> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<StoreProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StoreProduct::getStoreId, storeId)
                .eq(StoreProduct::getDeleted, 0);

        if (status != null) {
            wrapper.eq(StoreProduct::getStatus, status);
        }

        wrapper.orderByAsc(StoreProduct::getSortOrder)
                .orderByDesc(StoreProduct::getCreatedTime);

        return storeProductMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public List<StoreProduct> listOnSaleProducts(Long storeId) {
        LambdaQueryWrapper<StoreProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StoreProduct::getStoreId, storeId)
                .eq(StoreProduct::getStatus, 1)
                .eq(StoreProduct::getDeleted, 0)
                .orderByAsc(StoreProduct::getSortOrder)
                .orderByDesc(StoreProduct::getSales);

        return storeProductMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long storeId, Long productId, Integer status) {
        StoreProduct product = storeProductMapper.selectById(productId);
        if (product == null || product.getDeleted() == 1) {
            throw new BusinessException("商品不存在");
        }
        if (!product.getStoreId().equals(storeId)) {
            throw new BusinessException("无权操作该商品");
        }

        product.setStatus(status);
        storeProductMapper.updateById(product);
        log.info("店铺{}更新商品{}状态为{}", storeId, productId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseSales(Long productId, Integer count) {
        LambdaUpdateWrapper<StoreProduct> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(StoreProduct::getId, productId)
                .setSql("sales = sales + " + count);
        storeProductMapper.update(null, wrapper);
    }
}
