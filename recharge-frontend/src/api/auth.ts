import { post } from '@/utils/request'

export interface LoginRequest {
  phone: string
  password: string
}

export interface RegisterRequest {
  phone: string
  password: string
  confirmPassword: string
  inviteCode?: string
}

export interface LoginResponse {
  token: string
  userId: number
  phone: string
  nickname: string
}

// 登录
export function login(data: LoginRequest): Promise<LoginResponse> {
  return post('/v1/auth/login', data)
}

// 注册
export function register(data: RegisterRequest): Promise<LoginResponse> {
  return post('/v1/auth/register', data)
}

// 退出登录
export function logout(): Promise<void> {
  return post('/v1/auth/logout')
}
