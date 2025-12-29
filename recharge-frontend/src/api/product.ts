import { get } from '@/utils/request'
import type { Product } from '@/types'

// 获取商品列表
export function getProducts(): Promise<Product[]> {
  return get('/v1/products')
}

// 获取商品详情
export function getProductDetail(productId: number): Promise<Product> {
  return get(`/v1/products/${productId}`)
}
