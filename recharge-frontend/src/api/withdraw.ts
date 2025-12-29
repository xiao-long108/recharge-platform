import { get, post, put } from '@/utils/request'
import type { WithdrawRecord } from '@/types'

// 申请提现
export function applyWithdraw(data: {
  bankAccountId: number
  amount: number
  withdrawType: number
  payPassword: string
}): Promise<WithdrawRecord> {
  return post('/v1/withdraw/apply', data)
}

// 获取提现记录
export function getWithdrawRecords(params: {
  pageNum?: number
  pageSize?: number
}): Promise<WithdrawRecord[]> {
  return get('/v1/withdraw/records', { params })
}

// 获取提现详情
export function getWithdrawDetail(withdrawId: number): Promise<WithdrawRecord> {
  return get(`/v1/withdraw/${withdrawId}`)
}

// 取消提现
export function cancelWithdraw(withdrawId: number): Promise<void> {
  return put(`/v1/withdraw/${withdrawId}/cancel`)
}
