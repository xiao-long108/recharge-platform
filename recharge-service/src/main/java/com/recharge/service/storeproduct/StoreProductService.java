package com.recharge.service.storeproduct;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recharge.model.dto.request.StoreProductRequest;
import com.recharge.model.entity.StoreProduct;

import java.util.List;

/**
 * 店铺商品服务接口
 */
public interface StoreProductService {

    /**
     * 添加商品
     *
     * @param storeId 店铺ID
     * @param request 商品信息
     * @return 商品ID
     */
    Long addProduct(Long storeId, StoreProductRequest request);

    /**
     * 更新商品
     *
     * @param storeId 店铺ID
     * @param request 商品信息
     */
    void updateProduct(Long storeId, StoreProductRequest request);

    /**
     * 删除商品
     *
     * @param storeId   店铺ID
     * @param productId 商品ID
     */
    void deleteProduct(Long storeId, Long productId);

    /**
     * 获取商品详情
     *
     * @param productId 商品ID
     * @return 商品信息
     */
    StoreProduct getProduct(Long productId);

    /**
     * 分页获取店铺商品列表
     *
     * @param storeId 店铺ID
     * @param status  状态
     * @param page    页码
     * @param size    每页数量
     * @return 商品列表
     */
    Page<StoreProduct> pageProducts(Long storeId, Integer status, Integer page, Integer size);

    /**
     * 获取店铺上架商品列表
     *
     * @param storeId 店铺ID
     * @return 商品列表
     */
    List<StoreProduct> listOnSaleProducts(Long storeId);

    /**
     * 更新商品状态
     *
     * @param storeId   店铺ID
     * @param productId 商品ID
     * @param status    状态
     */
    void updateStatus(Long storeId, Long productId, Integer status);

    /**
     * 增加商品销量
     *
     * @param productId 商品ID
     * @param count     数量
     */
    void increaseSales(Long productId, Integer count);
}
