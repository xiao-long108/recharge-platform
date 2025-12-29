package com.recharge.service.store;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.model.entity.Store;

/**
 * 店铺服务接口
 */
public interface StoreService {

    /**
     * 开通店铺
     *
     * @param userId    用户ID
     * @param storeName 店铺名称
     * @param storeLogo 店铺Logo
     * @param storeDesc 店铺描述
     * @return 店铺
     */
    Store openStore(Long userId, String storeName, String storeLogo, String storeDesc);

    /**
     * 获取我的店铺信息
     *
     * @param userId 用户ID
     * @return 店铺
     */
    Store getMyStore(Long userId);

    /**
     * 更新店铺信息
     *
     * @param userId    用户ID
     * @param storeName 店铺名称
     * @param storeLogo 店铺Logo
     * @param storeDesc 店铺描述
     */
    void updateStore(Long userId, String storeName, String storeLogo, String storeDesc);

    /**
     * 获取店铺详情
     *
     * @param storeId 店铺ID
     * @return 店铺
     */
    Store getStoreDetail(Long storeId);

    /**
     * 分页查询店铺列表
     *
     * @param keyword  关键词
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<Store> pageStores(String keyword, Integer pageNum, Integer pageSize);
}
