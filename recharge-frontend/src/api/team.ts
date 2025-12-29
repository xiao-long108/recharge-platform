import { get } from '@/utils/request'
import type { TeamMember, CommissionRecord } from '@/types'

// 获取团队成员
export function getTeamMembers(level?: number): Promise<TeamMember[]> {
  return get('/v1/team/members', { params: { level } })
}

// 获取团队统计
export function getTeamStats(): Promise<{
  level1Count: number
  level2Count: number
  level3Count: number
  totalCount: number
}> {
  return get('/v1/team/stats')
}

// 获取佣金记录
export function getCommissionRecords(params: {
  pageNum?: number
  pageSize?: number
}): Promise<CommissionRecord[]> {
  return get('/v1/team/commission/records', { params })
}
