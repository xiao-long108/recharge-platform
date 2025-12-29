import { get, post, put } from '@/utils/request'
import type { Store } from '@/types'

// 获取我的店铺
export function getMyStore(): Promise<Store> {
  return get('/v1/store/my')
}

// 开通店铺
export function openStore(data: {
  storeName: string
  storeDescription?: string
  payPassword: string
}): Promise<Store> {
  return post('/v1/store/open', data)
}

// 更新店铺信息
export function updateStore(data: {
  storeName: string
  storeDescription?: string
}): Promise<void> {
  return put('/v1/store/update', data)
}

// 获取店铺列表
export function getStores(params: {
  pageNum?: number
  pageSize?: number
}): Promise<Store[]> {
  return get('/v1/store/list', { params })
}

// 获取店铺详情
export function getStoreDetail(storeId: number): Promise<Store> {
  return get(`/v1/store/${storeId}`)
}
