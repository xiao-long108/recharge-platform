<template>
  <div class="dashboard p-6 bg-gray-50 min-h-screen">
    <!-- 页面标题 -->
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-800">数据概览</h1>
      <p class="text-gray-500 mt-1">欢迎回来，这是您的业务数据统计</p>
    </div>

    <!-- 核心指标卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
      <!-- 总用户数 -->
      <div class="stat-card bg-gradient-to-br from-blue-500 to-blue-600 text-white">
        <div class="flex justify-between items-start">
          <div>
            <p class="text-blue-100 text-sm font-medium">总用户数</p>
            <p class="text-3xl font-bold mt-2">{{ formatNumber(stats.totalUsers) }}</p>
            <p class="text-blue-100 text-xs mt-2">
              <span class="bg-blue-400/30 px-2 py-0.5 rounded">
                今日 +{{ stats.todayUsers || 0 }}
              </span>
            </p>
          </div>
          <div class="stat-icon bg-blue-400/30">
            <UserOutlined class="text-2xl" />
          </div>
        </div>
      </div>

      <!-- 总订单数 -->
      <div class="stat-card bg-gradient-to-br from-green-500 to-green-600 text-white">
        <div class="flex justify-between items-start">
          <div>
            <p class="text-green-100 text-sm font-medium">总订单数</p>
            <p class="text-3xl font-bold mt-2">{{ formatNumber(stats.totalOrders) }}</p>
            <p class="text-green-100 text-xs mt-2">
              <span class="bg-green-400/30 px-2 py-0.5 rounded">
                今日 +{{ stats.todayOrders || 0 }}
              </span>
            </p>
          </div>
          <div class="stat-icon bg-green-400/30">
            <FileTextOutlined class="text-2xl" />
          </div>
        </div>
      </div>

      <!-- 待处理订单 -->
      <div class="stat-card bg-gradient-to-br from-orange-500 to-orange-600 text-white">
        <div class="flex justify-between items-start">
          <div>
            <p class="text-orange-100 text-sm font-medium">待处理订单</p>
            <p class="text-3xl font-bold mt-2">{{ formatNumber(stats.pendingOrders) }}</p>
            <p class="text-orange-100 text-xs mt-2">
              <span class="bg-orange-400/30 px-2 py-0.5 rounded">
                需要及时处理
              </span>
            </p>
          </div>
          <div class="stat-icon bg-orange-400/30">
            <ClockCircleOutlined class="text-2xl" />
          </div>
        </div>
      </div>

      <!-- 待审核提现 -->
      <div class="stat-card bg-gradient-to-br from-red-500 to-red-600 text-white">
        <div class="flex justify-between items-start">
          <div>
            <p class="text-red-100 text-sm font-medium">待审核提现</p>
            <p class="text-3xl font-bold mt-2">{{ formatNumber(stats.pendingWithdraws) }}</p>
            <p class="text-red-100 text-xs mt-2">
              <span class="bg-red-400/30 px-2 py-0.5 rounded">
                待处理申请
              </span>
            </p>
          </div>
          <div class="stat-icon bg-red-400/30">
            <WalletOutlined class="text-2xl" />
          </div>
        </div>
      </div>
    </div>

    <!-- 第二行：业务数据 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6">
      <!-- 订单状态分布 -->
      <div class="bg-white rounded-xl shadow-sm p-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">订单状态分布</h3>
        <div class="space-y-4">
          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <div class="w-3 h-3 rounded-full bg-green-500 mr-3"></div>
              <span class="text-gray-600">成功订单</span>
            </div>
            <div class="flex items-center">
              <span class="font-semibold text-gray-800 mr-2">{{ orderStats.success || 0 }}</span>
              <span class="text-xs text-gray-400">{{ getOrderPercent('success') }}%</span>
            </div>
          </div>
          <div class="w-full bg-gray-200 rounded-full h-2">
            <div class="bg-green-500 h-2 rounded-full transition-all duration-500" :style="{ width: getOrderPercent('success') + '%' }"></div>
          </div>

          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <div class="w-3 h-3 rounded-full bg-yellow-500 mr-3"></div>
              <span class="text-gray-600">处理中</span>
            </div>
            <div class="flex items-center">
              <span class="font-semibold text-gray-800 mr-2">{{ orderStats.processing || 0 }}</span>
              <span class="text-xs text-gray-400">{{ getOrderPercent('processing') }}%</span>
            </div>
          </div>
          <div class="w-full bg-gray-200 rounded-full h-2">
            <div class="bg-yellow-500 h-2 rounded-full transition-all duration-500" :style="{ width: getOrderPercent('processing') + '%' }"></div>
          </div>

          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <div class="w-3 h-3 rounded-full bg-blue-500 mr-3"></div>
              <span class="text-gray-600">待处理</span>
            </div>
            <div class="flex items-center">
              <span class="font-semibold text-gray-800 mr-2">{{ orderStats.pending || 0 }}</span>
              <span class="text-xs text-gray-400">{{ getOrderPercent('pending') }}%</span>
            </div>
          </div>
          <div class="w-full bg-gray-200 rounded-full h-2">
            <div class="bg-blue-500 h-2 rounded-full transition-all duration-500" :style="{ width: getOrderPercent('pending') + '%' }"></div>
          </div>

          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <div class="w-3 h-3 rounded-full bg-red-500 mr-3"></div>
              <span class="text-gray-600">失败订单</span>
            </div>
            <div class="flex items-center">
              <span class="font-semibold text-gray-800 mr-2">{{ orderStats.failed || 0 }}</span>
              <span class="text-xs text-gray-400">{{ getOrderPercent('failed') }}%</span>
            </div>
          </div>
          <div class="w-full bg-gray-200 rounded-full h-2">
            <div class="bg-red-500 h-2 rounded-full transition-all duration-500" :style="{ width: getOrderPercent('failed') + '%' }"></div>
          </div>
        </div>
      </div>

      <!-- 产品统计 -->
      <div class="bg-white rounded-xl shadow-sm p-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">产品统计</h3>
        <div class="flex items-center justify-center mb-6">
          <div class="relative">
            <svg class="w-32 h-32 transform -rotate-90">
              <circle cx="64" cy="64" r="56" stroke="#e5e7eb" stroke-width="12" fill="none" />
              <circle 
                cx="64" cy="64" r="56" 
                stroke="#10b981" 
                stroke-width="12" 
                fill="none"
                :stroke-dasharray="getProductCircle()"
                stroke-linecap="round"
              />
            </svg>
            <div class="absolute inset-0 flex flex-col items-center justify-center">
              <span class="text-2xl font-bold text-gray-800">{{ productStats.total || 0 }}</span>
              <span class="text-xs text-gray-500">总产品数</span>
            </div>
          </div>
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div class="text-center p-3 bg-green-50 rounded-lg">
            <p class="text-2xl font-bold text-green-600">{{ productStats.enabled || 0 }}</p>
            <p class="text-sm text-gray-500">已启用</p>
          </div>
          <div class="text-center p-3 bg-gray-100 rounded-lg">
            <p class="text-2xl font-bold text-gray-600">{{ productStats.disabled || 0 }}</p>
            <p class="text-sm text-gray-500">已禁用</p>
          </div>
        </div>
      </div>

      <!-- 提现统计 -->
      <div class="bg-white rounded-xl shadow-sm p-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">提现统计</h3>
        <div class="space-y-3">
          <div class="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
            <div class="flex items-center">
              <div class="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center mr-3">
                <FileTextOutlined class="text-blue-600" />
              </div>
              <span class="text-gray-600">总申请</span>
            </div>
            <span class="text-xl font-bold text-gray-800">{{ withdrawStats.total || 0 }}</span>
          </div>
          <div class="flex items-center justify-between p-3 bg-yellow-50 rounded-lg">
            <div class="flex items-center">
              <div class="w-10 h-10 rounded-full bg-yellow-100 flex items-center justify-center mr-3">
                <ClockCircleOutlined class="text-yellow-600" />
              </div>
              <span class="text-gray-600">待审核</span>
            </div>
            <span class="text-xl font-bold text-yellow-600">{{ withdrawStats.pending || 0 }}</span>
          </div>
          <div class="flex items-center justify-between p-3 bg-green-50 rounded-lg">
            <div class="flex items-center">
              <div class="w-10 h-10 rounded-full bg-green-100 flex items-center justify-center mr-3">
                <CheckCircleOutlined class="text-green-600" />
              </div>
              <span class="text-gray-600">已通过</span>
            </div>
            <span class="text-xl font-bold text-green-600">{{ withdrawStats.success || 0 }}</span>
          </div>
          <div class="flex items-center justify-between p-3 bg-red-50 rounded-lg">
            <div class="flex items-center">
              <div class="w-10 h-10 rounded-full bg-red-100 flex items-center justify-center mr-3">
                <CloseCircleOutlined class="text-red-600" />
              </div>
              <span class="text-gray-600">已拒绝</span>
            </div>
            <span class="text-xl font-bold text-red-600">{{ withdrawStats.failed || 0 }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 第三行：用户和快捷操作 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- 用户统计 -->
      <div class="bg-white rounded-xl shadow-sm p-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">用户统计</h3>
        <div class="grid grid-cols-3 gap-4">
          <div class="text-center p-4 bg-gradient-to-br from-blue-50 to-blue-100 rounded-xl">
            <div class="w-12 h-12 rounded-full bg-blue-500 flex items-center justify-center mx-auto mb-3">
              <UserOutlined class="text-white text-xl" />
            </div>
            <p class="text-2xl font-bold text-blue-600">{{ userStats.total || 0 }}</p>
            <p class="text-sm text-gray-500">总用户</p>
          </div>
          <div class="text-center p-4 bg-gradient-to-br from-green-50 to-green-100 rounded-xl">
            <div class="w-12 h-12 rounded-full bg-green-500 flex items-center justify-center mx-auto mb-3">
              <CheckCircleOutlined class="text-white text-xl" />
            </div>
            <p class="text-2xl font-bold text-green-600">{{ userStats.normal || 0 }}</p>
            <p class="text-sm text-gray-500">正常用户</p>
          </div>
          <div class="text-center p-4 bg-gradient-to-br from-red-50 to-red-100 rounded-xl">
            <div class="w-12 h-12 rounded-full bg-red-500 flex items-center justify-center mx-auto mb-3">
              <StopOutlined class="text-white text-xl" />
            </div>
            <p class="text-2xl font-bold text-red-600">{{ userStats.frozen || 0 }}</p>
            <p class="text-sm text-gray-500">冻结用户</p>
          </div>
        </div>
      </div>

      <!-- 快捷操作 -->
      <div class="bg-white rounded-xl shadow-sm p-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">快捷操作</h3>
        <div class="grid grid-cols-2 gap-4">
          <router-link to="/orders" class="quick-action-btn bg-blue-50 hover:bg-blue-100 text-blue-600">
            <FileTextOutlined class="text-2xl mb-2" />
            <span>订单管理</span>
          </router-link>
          <router-link to="/users" class="quick-action-btn bg-green-50 hover:bg-green-100 text-green-600">
            <UserOutlined class="text-2xl mb-2" />
            <span>用户管理</span>
          </router-link>
          <router-link to="/products" class="quick-action-btn bg-purple-50 hover:bg-purple-100 text-purple-600">
            <AppstoreOutlined class="text-2xl mb-2" />
            <span>产品管理</span>
          </router-link>
          <router-link to="/withdraws" class="quick-action-btn bg-orange-50 hover:bg-orange-100 text-orange-600">
            <WalletOutlined class="text-2xl mb-2" />
            <span>提现审核</span>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import {
  UserOutlined,
  FileTextOutlined,
  ClockCircleOutlined,
  WalletOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  StopOutlined,
  AppstoreOutlined,
} from '@ant-design/icons-vue'
import request from '@/utils/request'

const stats = ref<any>({})
const orderStats = ref<any>({})
const productStats = ref<any>({})
const withdrawStats = ref<any>({})
const userStats = ref<any>({})

// 格式化数字
const formatNumber = (num: number | undefined) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toLocaleString()
}

// 计算订单百分比
const getOrderPercent = (type: string) => {
  const total = orderStats.value.total || 0
  if (total === 0) return 0
  const value = orderStats.value[type] || 0
  return Math.round((value / total) * 100)
}

// 计算产品圆环
const getProductCircle = () => {
  const total = productStats.value.total || 0
  const enabled = productStats.value.enabled || 0
  if (total === 0) return '0 352'
  const percent = enabled / total
  const circumference = 2 * Math.PI * 56
  return `${percent * circumference} ${circumference}`
}

const fetchStats = async () => {
  try {
    const [dashboardRes, ordersRes, productsRes, withdrawsRes, usersRes] = await Promise.all([
      request.get('/api/admin/stats/dashboard'),
      request.get('/api/admin/stats/orders'),
      request.get('/api/admin/stats/products'),
      request.get('/api/admin/stats/withdraws'),
      request.get('/api/admin/stats/users'),
    ])
    
    if (dashboardRes.data.code === 200) {
      stats.value = dashboardRes.data.data
    }
    if (ordersRes.data.code === 200) {
      orderStats.value = ordersRes.data.data
    }
    if (productsRes.data.code === 200) {
      productStats.value = productsRes.data.data
    }
    if (withdrawsRes.data.code === 200) {
      withdrawStats.value = withdrawsRes.data.data
    }
    if (usersRes.data.code === 200) {
      userStats.value = usersRes.data.data
    }
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.stat-card {
  @apply rounded-xl p-6 shadow-lg transition-transform duration-300 hover:scale-105 cursor-pointer;
}

.stat-icon {
  @apply w-12 h-12 rounded-full flex items-center justify-center;
}

.quick-action-btn {
  @apply flex flex-col items-center justify-center p-6 rounded-xl transition-all duration-300 cursor-pointer;
}

.quick-action-btn:hover {
  @apply transform scale-105 shadow-md;
}
</style>
