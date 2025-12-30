<template>
  <div class="page-container !pb-24">
    <!-- 页面头部 -->
    <div class="flex items-center bg-primary text-white p-4">
      <LeftOutlined class="text-xl cursor-pointer" @click="router.back()" />
      <span class="flex-1 text-center text-lg font-bold">订单详情</span>
      <span class="w-5"></span>
    </div>

    <!-- 状态卡片 -->
    <div :class="['p-8 text-center text-white', getStatusBgClass(order?.orderStatus)]" v-if="order">
      <CheckCircleOutlined v-if="order.orderStatus === 2" class="text-5xl mb-3" />
      <LoadingOutlined v-else-if="order.orderStatus === 1" class="text-5xl mb-3 animate-spin" />
      <ClockCircleOutlined v-else-if="order.orderStatus === 0" class="text-5xl mb-3" />
      <CloseCircleOutlined v-else class="text-5xl mb-3" />
      <div class="text-xl font-bold">{{ getStatusText(order.orderStatus) }}</div>
      <div class="text-sm opacity-80 mt-2" v-if="order.orderStatus === 1">预计1-5分钟内完成</div>
    </div>

    <!-- 进度时间线 -->
    <div class="card-box m-3" v-if="order">
      <div class="text-base font-bold text-foreground mb-4 pb-3 border-b border-border">订单进度</div>
      <div class="timeline">
        <div class="timeline-item" :class="{ active: true }">
          <div class="timeline-dot"></div>
          <div class="timeline-content">
            <div class="timeline-title">提交订单</div>
            <div class="timeline-time">{{ formatTime(order.createdTime) }}</div>
          </div>
        </div>
        <div class="timeline-item" :class="{ active: order.payStatus === 1 }">
          <div class="timeline-dot"></div>
          <div class="timeline-content">
            <div class="timeline-title">支付完成</div>
            <div class="timeline-time" v-if="order.payTime">{{ formatTime(order.payTime) }}</div>
            <div class="timeline-time" v-else>等待支付</div>
          </div>
        </div>
        <div class="timeline-item" :class="{ active: order.orderStatus === 1 || order.orderStatus === 2 }">
          <div class="timeline-dot"></div>
          <div class="timeline-content">
            <div class="timeline-title">充值处理</div>
            <div class="timeline-time" v-if="order.orderStatus === 2">充值成功</div>
            <div class="timeline-time" v-else-if="order.orderStatus === 1">处理中...</div>
            <div class="timeline-time" v-else>等待处理</div>
          </div>
        </div>
        <div class="timeline-item" :class="{ active: order.orderStatus === 2 }">
          <div class="timeline-dot"></div>
          <div class="timeline-content">
            <div class="timeline-title">订单完成</div>
            <div class="timeline-time" v-if="order.orderStatus === 2">已完成</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 订单信息 -->
    <div class="card-box m-3" v-if="order">
      <div class="text-base font-bold text-foreground mb-4 pb-3 border-b border-border">订单信息</div>
      <div class="list-item">
        <span class="text-muted-foreground">订单编号</span>
        <div class="flex items-center gap-2">
          <span class="text-foreground text-sm">{{ order.orderNo }}</span>
          <CopyOutlined class="text-primary cursor-pointer" @click="copyOrderNo" />
        </div>
      </div>
      <div class="list-item">
        <span class="text-muted-foreground">充值号码</span>
        <span class="text-foreground">{{ order.phoneNumber }}</span>
      </div>
      <div class="list-item">
        <span class="text-muted-foreground">充值面值</span>
        <span class="text-foreground">{{ order.faceValue }}元</span>
      </div>
      <div class="list-item" v-if="order.discountAmount > 0">
        <span class="text-muted-foreground">优惠金额</span>
        <span class="text-success">-¥{{ order.discountAmount.toFixed(2) }}</span>
      </div>
      <div class="list-item">
        <span class="text-muted-foreground">实付金额</span>
        <span class="text-destructive font-bold text-lg">¥{{ order.payAmount.toFixed(2) }}</span>
      </div>
    </div>

    <!-- 支付方式 -->
    <div class="card-box m-3" v-if="order && order.payStatus === 1">
      <div class="text-base font-bold text-foreground mb-4 pb-3 border-b border-border">支付信息</div>
      <div class="list-item">
        <span class="text-muted-foreground">支付方式</span>
        <span class="text-foreground">{{ getPayTypeText(order.payType) }}</span>
      </div>
      <div class="list-item">
        <span class="text-muted-foreground">支付时间</span>
        <span class="text-foreground">{{ formatTime(order.payTime) }}</span>
      </div>
    </div>

    <!-- 温馨提示 -->
    <div class="card-box m-3">
      <div class="text-base font-bold text-foreground mb-3">温馨提示</div>
      <div class="text-sm text-muted-foreground leading-relaxed">
        <p>1. 充值到账时间一般为1-5分钟，高峰期可能延长</p>
        <p class="mt-2">2. 如长时间未到账，请联系客服处理</p>
        <p class="mt-2">3. 充值成功后不支持退款</p>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="fixed bottom-0 left-0 right-0 bg-card p-4 shadow-[0_-2px_10px_rgba(0,0,0,0.05)]" v-if="order">
      <div class="flex gap-3" v-if="order.orderStatus === 0">
        <a-button size="large" class="flex-1 h-11" @click="handleCancel">取消订单</a-button>
        <a-button type="primary" size="large" class="flex-1 h-11" @click="goToPay">立即支付</a-button>
      </div>
      <div class="flex gap-3" v-else-if="order.orderStatus === 2">
        <a-button size="large" class="flex-1 h-11" @click="router.push('/order/list')">查看全部订单</a-button>
        <a-button type="primary" size="large" class="flex-1 h-11" @click="router.push('/recharge')">再充一单</a-button>
      </div>
      <div v-else>
        <a-button type="primary" size="large" block class="h-11" @click="router.push('/order/list')">返回订单列表</a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import {
  LeftOutlined,
  CheckCircleOutlined,
  LoadingOutlined,
  ClockCircleOutlined,
  CloseCircleOutlined,
  CopyOutlined
} from '@ant-design/icons-vue'
import { getOrderDetail, cancelOrder } from '@/api/order'
import dayjs from 'dayjs'
import type { Order } from '@/types'

const router = useRouter()
const route = useRoute()

const order = ref<Order | null>(null)

onMounted(async () => {
  const orderId = Number(route.params.id)
  if (orderId) {
    await loadOrder(orderId)
  }
})

async function loadOrder(orderId: number) {
  try {
    order.value = await getOrderDetail(orderId)
  } catch {
    router.back()
  }
}

function getStatusText(status: number): string {
  const map: Record<number, string> = {
    0: '待支付',
    1: '充值中',
    2: '充值成功',
    3: '已取消',
    4: '已退款'
  }
  return map[status] || '未知'
}

function getStatusBgClass(status?: number): string {
  if (status === undefined) return ''
  const map: Record<number, string> = {
    0: 'bg-gradient-to-r from-warning to-[hsl(42,84%,70%)]',
    1: 'bg-gradient-to-r from-primary to-[hsl(212,100%,60%)]',
    2: 'bg-gradient-to-r from-success to-[hsl(144,57%,68%)]',
    3: 'bg-gradient-to-r from-muted to-[hsl(240,4.8%,75%)]',
    4: 'bg-gradient-to-r from-destructive to-[hsl(359,100%,75%)]'
  }
  return map[status] || ''
}

function getPayTypeText(payType: number): string {
  const map: Record<number, string> = {
    1: '余额支付',
    2: 'PIX支付',
    3: '信用卡支付'
  }
  return map[payType] || '其他支付'
}

function formatTime(time: string): string {
  if (!time) return ''
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

function copyOrderNo() {
  if (order.value?.orderNo) {
    navigator.clipboard.writeText(order.value.orderNo)
    message.success('订单号已复制')
  }
}

function goToPay() {
  if (!order.value) return
  router.push({
    path: '/recharge/confirm',
    query: { orderId: order.value.id }
  })
}

async function handleCancel() {
  if (!order.value) return

  Modal.confirm({
    title: '提示',
    content: '确定要取消该订单吗？',
    async onOk() {
      await cancelOrder(order.value!.id)
      message.success('订单已取消')
      await loadOrder(order.value!.id)
    }
  })
}
</script>

<style scoped>
.timeline {
  position: relative;
  padding-left: 24px;
}

.timeline-item {
  position: relative;
  padding-bottom: 20px;
  padding-left: 20px;
}

.timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 8px;
  bottom: -12px;
  width: 2px;
  background: #e5e5e5;
}

.timeline-item:last-child::before {
  display: none;
}

.timeline-item.active::before {
  background: var(--primary);
}

.timeline-dot {
  position: absolute;
  left: -5px;
  top: 4px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #e5e5e5;
  border: 2px solid white;
  box-shadow: 0 0 0 2px #e5e5e5;
}

.timeline-item.active .timeline-dot {
  background: var(--primary);
  box-shadow: 0 0 0 2px var(--primary);
}

.timeline-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.timeline-item:not(.active) .timeline-title {
  color: #999;
}

.timeline-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.animate-spin {
  animation: spin 1s linear infinite;
}
</style>
