import { get } from '@/utils/request'
import type { Notice } from '@/types'

// 获取公告列表
export function getNotices(): Promise<Notice[]> {
  return get('/v1/notices')
}

// 获取最新公告（首页展示）
export function getLatestNotices(limit = 5): Promise<Notice[]> {
  return get('/v1/notices/latest', { limit })
}

// 获取公告详情
export function getNoticeDetail(id: number): Promise<Notice> {
  return get(`/v1/notices/${id}`)
}
