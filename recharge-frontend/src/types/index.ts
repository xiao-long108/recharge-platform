// 用户信息
export interface UserInfo {
  id: number
  phone: string
  nickname: string
  avatar: string
  balance: number
  commissionBalance: number
  totalCommission: number
  inviteCode: string
  level: number
  hasPayPassword: boolean
  createdTime: string
}

// 商品
export interface Product {
  id: number
  productName: string
  faceValue: number
  salePrice: number
  discount: number
  productType: number
  sortOrder: number
  status: number
}

// 订单
export interface Order {
  id: number
  orderNo: string
  userId: number
  productId: number
  phoneNumber: string
  faceValue: number
  payAmount: number
  discountAmount: number
  orderStatus: number
  payStatus: number
  payType: number
  payTime: string
  rechargeStatus: number
  createdTime: string
  product?: Product
}

// 提现记录
export interface WithdrawRecord {
  id: number
  withdrawNo: string
  userId: number
  bankAccountId: number
  amount: number
  fee: number
  actualAmount: number
  withdrawType: number
  status: number
  remark: string
  createdTime: string
}

// 银行账户
export interface BankAccount {
  id: number
  userId: number
  bankName: string
  accountHolder: string
  accountNumber: string
  branchName: string
  isDefault: number
  status: number
}

// 余额宝账户
export interface YuebaoAccount {
  id: number
  userId: number
  balance: number
  totalIncome: number
  yesterdayIncome: number
  annualRate: number
  status: number
}

// 余额宝记录
export interface YuebaoRecord {
  id: number
  userId: number
  recordType: number
  amount: number
  balanceBefore: number
  balanceAfter: number
  remark: string
  createdTime: string
}

// 店铺
export interface Store {
  id: number
  userId: number
  storeName: string
  storeDescription: string
  storeQrcode: string
  totalSales: number
  totalOrders: number
  status: number
}

// 团队成员
export interface TeamMember {
  id: number
  phone: string
  nickname: string
  avatar: string
  level: number
  createdTime: string
}

// 佣金记录
export interface CommissionRecord {
  id: number
  userId: number
  sourceUserId: number
  orderId: number
  orderAmount: number
  commissionRate: number
  commissionAmount: number
  level: number
  status: number
  createdTime: string
}

// API响应
export interface Result<T> {
  code: number
  message: string
  data: T
}
