import { get, post } from '@/utils/request'
import type { Coupon, UserCoupon } from '@/types'

// 获取可领取的优惠券列表
export function getAvailableCoupons(): Promise<Coupon[]> {
  return get('/v1/coupons/available')
}

// 获取我的优惠券
export function getMyCoupons(status?: number): Promise<UserCoupon[]> {
  return get('/v1/coupons/mine', { status })
}

// 领取优惠券
export function claimCoupon(couponId: number): Promise<void> {
  return post(`/v1/coupons/${couponId}/claim`)
}

// 获取订单可用优惠券
export function getOrderCoupons(amount: number): Promise<UserCoupon[]> {
  return get('/v1/coupons/usable', { amount })
}
