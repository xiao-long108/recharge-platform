import { get, post } from '@/utils/request'
import type { YuebaoAccount, YuebaoRecord } from '@/types'

// 获取余额宝账户
export function getYuebaoAccount(): Promise<YuebaoAccount> {
  return get('/v1/yuebao/account')
}

// 转入余额宝
export function transferIn(data: {
  amount: number
  payPassword: string
}): Promise<void> {
  return post('/v1/yuebao/transfer-in', data)
}

// 转出余额宝
export function transferOut(data: {
  amount: number
  payPassword: string
}): Promise<void> {
  return post('/v1/yuebao/transfer-out', data)
}

// 获取余额宝记录
export function getYuebaoRecords(params: {
  pageNum?: number
  pageSize?: number
}): Promise<YuebaoRecord[]> {
  return get('/v1/yuebao/records', { params })
}
