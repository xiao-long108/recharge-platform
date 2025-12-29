import { get, post, put } from '@/utils/request'
import type { UserInfo, BankAccount } from '@/types'

// 获取用户信息
export function getUserInfo(): Promise<UserInfo> {
  return get('/v1/user/info')
}

// 修改昵称
export function updateNickname(nickname: string): Promise<void> {
  return put('/v1/user/nickname', { nickname })
}

// 修改头像
export function updateAvatar(avatar: string): Promise<void> {
  return put('/v1/user/avatar', { avatar })
}

// 修改登录密码
export function changePassword(oldPassword: string, newPassword: string): Promise<void> {
  return put('/v1/user/password', { oldPassword, newPassword })
}

// 设置支付密码
export function setPayPassword(payPassword: string): Promise<void> {
  return post('/v1/user/pay-password', { payPassword })
}

// 修改支付密码
export function changePayPassword(oldPayPassword: string, newPayPassword: string): Promise<void> {
  return put('/v1/user/pay-password', { oldPayPassword, newPayPassword })
}

// 获取银行账户列表
export function getBankAccounts(): Promise<BankAccount[]> {
  return get('/v1/bank-account/list')
}

// 添加银行账户
export function addBankAccount(data: {
  bankName: string
  accountHolder: string
  accountNumber: string
  branchName: string
}): Promise<BankAccount> {
  return post('/v1/bank-account/add', data)
}

// 删除银行账户
export function deleteBankAccount(accountId: number): Promise<void> {
  return post(`/v1/bank-account/${accountId}`)
}

// 设为默认银行账户
export function setDefaultBankAccount(accountId: number): Promise<void> {
  return put(`/v1/bank-account/${accountId}/default`)
}
