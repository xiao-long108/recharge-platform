import { get } from '@/utils/request'
import type { Banner } from '@/types'

// 获取首页轮播图
export function getHomeBanners(): Promise<Banner[]> {
  return get('/v1/banners', { position: 1 })
}

// 获取店铺轮播图
export function getStoreBanners(): Promise<Banner[]> {
  return get('/v1/banners', { position: 2 })
}
