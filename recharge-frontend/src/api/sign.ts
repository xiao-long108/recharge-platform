import { get, post } from '@/utils/request'

export interface SignInfo {
  isSigned: boolean
  continuousDays: number
  totalDays: number
  todayReward: number
  signRecords: { date: string; signed: boolean }[]
}

// 获取签到信息
export function getSignInfo(): Promise<SignInfo> {
  return get('/v1/sign/info')
}

// 签到
export function doSign(): Promise<{ reward: number; continuousDays: number }> {
  return post('/v1/sign')
}

// 获取签到记录
export function getSignRecords(month?: string): Promise<{ date: string; reward: number }[]> {
  return get('/v1/sign/records', { month })
}
