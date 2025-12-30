<template>
  <div class="page-container !pb-20">
    <!-- 页面头部 -->
    <div class="flex items-center bg-primary text-white p-4">
      <LeftOutlined class="text-xl cursor-pointer" @click="router.back()" />
      <span class="flex-1 text-center text-lg font-bold">优惠券</span>
      <span class="w-5"></span>
    </div>

    <!-- Tab切换 -->
    <div class="flex bg-card p-1 m-3 rounded-lg">
      <div
        v-for="tab in tabs"
        :key="tab.value"
        class="flex-1 text-center py-3 rounded-md cursor-pointer text-sm transition-all"
        :class="activeTab === tab.value ? 'bg-primary text-white' : 'text-foreground'"
        @click="activeTab = tab.value"
      >
        {{ tab.label }}
      </div>
    </div>

    <!-- 可领取优惠券 -->
    <div v-if="activeTab === 'available'" class="px-3">
      <div v-if="availableCoupons.length === 0" class="empty-state">
        <GiftOutlined class="text-5xl text-muted-foreground" />
        <p class="text-muted-foreground mt-3">暂无可领取的优惠券</p>
      </div>
      <div v-else class="space-y-3">
        <div v-for="coupon in availableCoupons" :key="coupon.id" class="coupon-card">
          <div class="coupon-left" :class="getCouponClass(coupon.type)">
            <div class="coupon-value">
              <template v-if="coupon.type === 2">
                <span class="text-2xl font-bold">{{ (coupon.discountRate / 10).toFixed(1) }}</span>
                <span class="text-sm">折</span>
              </template>
              <template v-else>
                <span class="text-sm">R$</span>
                <span class="text-2xl font-bold">{{ coupon.discountAmount }}</span>
              </template>
            </div>
            <div class="text-xs opacity-80 mt-1">
              {{ coupon.minAmount > 0 ? `满${coupon.minAmount}可用` : '无门槛' }}
            </div>
          </div>
          <div class="coupon-right">
            <div class="coupon-name">{{ coupon.name }}</div>
            <div class="coupon-time">{{ formatDate(coupon.endTime) }} 到期</div>
            <a-button
              type="primary"
              size="small"
              class="claim-btn"
              @click="handleClaim(coupon)"
            >
              立即领取
            </a-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 我的优惠券 -->
    <div v-else class="px-3">
      <div v-if="myCoupons.length === 0" class="empty-state">
        <GiftOutlined class="text-5xl text-muted-foreground" />
        <p class="text-muted-foreground mt-3">暂无{{ activeTab === 'unused' ? '可用' : activeTab === 'used' ? '已使用的' : '已过期的' }}优惠券</p>
      </div>
      <div v-else class="space-y-3">
        <div
          v-for="coupon in myCoupons"
          :key="coupon.id"
          class="coupon-card"
          :class="{ 'opacity-60': activeTab !== 'unused' }"
        >
          <div class="coupon-left" :class="getCouponClass(coupon.type)">
            <div class="coupon-value">
              <template v-if="coupon.type === 2">
                <span class="text-2xl font-bold">{{ (coupon.discountRate / 10).toFixed(1) }}</span>
                <span class="text-sm">折</span>
              </template>
              <template v-else>
                <span class="text-sm">R$</span>
                <span class="text-2xl font-bold">{{ coupon.discountAmount }}</span>
              </template>
            </div>
            <div class="text-xs opacity-80 mt-1">
              {{ coupon.minAmount > 0 ? `满${coupon.minAmount}可用` : '无门槛' }}
            </div>
          </div>
          <div class="coupon-right">
            <div class="coupon-name">{{ coupon.couponName }}</div>
            <div class="coupon-time">{{ formatDate(coupon.expireTime) }} 到期</div>
            <div v-if="activeTab === 'unused'" class="coupon-status text-primary">可使用</div>
            <div v-else-if="activeTab === 'used'" class="coupon-status text-muted-foreground">已使用</div>
            <div v-else class="coupon-status text-muted-foreground">已过期</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { LeftOutlined, GiftOutlined } from '@ant-design/icons-vue'
import { getAvailableCoupons, getMyCoupons, claimCoupon } from '@/api/coupon'
import type { Coupon, UserCoupon } from '@/types'

const router = useRouter()

const tabs = [
  { label: '领券中心', value: 'available' },
  { label: '可使用', value: 'unused' },
  { label: '已使用', value: 'used' },
  { label: '已过期', value: 'expired' }
]

const activeTab = ref('available')
const availableCoupons = ref<Coupon[]>([])
const myCoupons = ref<UserCoupon[]>([])

const getCouponClass = (type: number) => {
  if (type === 1) return 'type-1'
  if (type === 2) return 'type-2'
  return 'type-3'
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  return dateStr.slice(0, 10)
}

const loadAvailableCoupons = async () => {
  try {
    availableCoupons.value = await getAvailableCoupons()
  } catch {
    // 错误处理
  }
}

const loadMyCoupons = async (status: number) => {
  try {
    myCoupons.value = await getMyCoupons(status)
  } catch {
    // 错误处理
  }
}

const handleClaim = async (coupon: Coupon) => {
  try {
    await claimCoupon(coupon.id)
    message.success('领取成功')
    loadAvailableCoupons()
  } catch {
    // 错误处理
  }
}

watch(activeTab, (val) => {
  if (val === 'available') {
    loadAvailableCoupons()
  } else {
    const statusMap: Record<string, number> = { unused: 0, used: 1, expired: 2 }
    loadMyCoupons(statusMap[val])
  }
})

onMounted(() => {
  loadAvailableCoupons()
})
</script>

<style scoped lang="scss">
.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.coupon-card {
  display: flex;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.coupon-left {
  width: 100px;
  padding: 16px 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  position: relative;

  &.type-1 {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }
  &.type-2 {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  }
  &.type-3 {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  }

  &::after {
    content: '';
    position: absolute;
    right: -6px;
    top: 50%;
    transform: translateY(-50%);
    width: 12px;
    height: 12px;
    background: white;
    border-radius: 50%;
  }
}

.coupon-value {
  display: flex;
  align-items: baseline;
}

.coupon-right {
  flex: 1;
  padding: 16px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
}

.coupon-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.coupon-time {
  font-size: 12px;
  color: #999;
  margin-top: 6px;
}

.claim-btn {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
}

.coupon-status {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 12px;
}
</style>
