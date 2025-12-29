import { get, post, put } from '@/utils/request'
import type { Order } from '@/types'

// 创建订单
export function createOrder(data: {
  productId: number
  phoneNumber: string
  storeId?: number
}): Promise<Order> {
  return post('/v1/orders', data)
}

// 支付订单
export function payOrder(data: {
  orderId: number
  payType: number
  payPassword?: string
}): Promise<void> {
  return post('/v1/orders/pay', data)
}

// 取消订单
export function cancelOrder(orderId: number): Promise<void> {
  return put(`/v1/orders/${orderId}/cancel`)
}

// 获取订单列表
export function getOrders(params: {
  pageNum?: number
  pageSize?: number
  status?: number
}): Promise<Order[]> {
  return get('/v1/orders', { params })
}

// 获取订单详情
export function getOrderDetail(orderId: number): Promise<Order> {
  return get(`/v1/orders/${orderId}`)
}
