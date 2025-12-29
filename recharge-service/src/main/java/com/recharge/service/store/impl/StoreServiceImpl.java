package com.recharge.service.store.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.common.enums.ResultCodeEnum;
import com.recharge.common.exception.BusinessException;
import com.recharge.mapper.StoreMapper;
import com.recharge.model.entity.Store;
import com.recharge.service.store.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 店铺服务实现 - 适配原数据库 cloud_times_api_store
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreMapper storeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Store openStore(Long userId, String storeName, String storeLogo, String storeDesc) {
        // 检查是否已开店
        Store existStore = storeMapper.selectByUserId(userId);
        if (existStore != null) {
            throw new BusinessException(ResultCodeEnum.STORE_ALREADY_EXIST);
        }

        // 创建店铺 - 使用原数据库字段
        Store store = new Store();
        store.setUserId(userId);
        store.setSName(storeName);
        store.setSLogo(storeLogo);
        store.setSInfo(storeDesc);
        store.setSPrice(BigDecimal.ZERO);          // 店铺余额
        store.setSYuebaoPrice(BigDecimal.ZERO);    // 余额宝余额
        store.setSSales(0);                         // 销量
        store.setSLevel(1);                         // 等级
        store.setSStatus(1);                        // 审核状态: 1=通过
        store.setSOpenStatus(1);                    // 开通状态
        store.setCreateTime(LocalDateTime.now());

        storeMapper.insert(store);

        log.info("开通店铺成功: userId={}, storeId={}", userId, store.getId());
        return store;
    }

    @Override
    public Store getMyStore(Long userId) {
        Store store = storeMapper.selectByUserId(userId);
        if (store == null) {
            throw new BusinessException(ResultCodeEnum.STORE_NOT_EXIST);
        }
        return store;
    }

    @Override
    public void updateStore(Long userId, String storeName, String storeLogo, String storeDesc) {
        Store store = getMyStore(userId);

        if (StringUtils.hasText(storeName)) {
            store.setSName(storeName);
        }
        if (StringUtils.hasText(storeLogo)) {
            store.setSLogo(storeLogo);
        }
        if (StringUtils.hasText(storeDesc)) {
            store.setSInfo(storeDesc);
        }
        store.setUpdateTime(LocalDateTime.now());

        storeMapper.updateById(store);
        log.info("更新店铺信息: storeId={}", store.getId());
    }

    @Override
    public Store getStoreDetail(Long storeId) {
        Store store = storeMapper.selectById(storeId);
        if (store == null) {
            throw new BusinessException(ResultCodeEnum.STORE_NOT_EXIST);
        }
        return store;
    }

    @Override
    public Page<Store> pageStores(String keyword, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<Store>()
                .eq(Store::getSStatus, 1)  // 审核通过
                .like(StringUtils.hasText(keyword), Store::getSName, keyword)
                .orderByDesc(Store::getSSales);

        return storeMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }
}
