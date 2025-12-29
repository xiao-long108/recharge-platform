<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="flex items-center bg-primary text-white p-4">
      <LeftOutlined class="text-xl cursor-pointer" @click="router.back()" />
      <span class="flex-1 text-center text-lg font-bold">订单详情</span>
      <span class="w-5"></span>
    </div>

    <!-- 状态卡片 -->
    <div :class="['p-8 text-center text-white', getStatusBgClass(order?.orderStatus)]" v-if="order">
      <CheckCircleOutlined v-if="order.orderStatus === 2" class="text-5xl mb-3" />
      <LoadingOutlined v-else-if="order.orderStatus === 1" class="text-5xl mb-3" />
      <ClockCircleOutlined v-else-if="order.orderStatus === 0" class="text-5xl mb-3" />
      <CloseCircleOutlined v-else class="text-5xl mb-3" />
      <div class="text-xl font-bold">{{ getStatusText(order.orderStatus) }}</div>
    </div>

    <!-- 订单信息 -->
    <div class="card-box m-3" v-if="order">
      <div class="text-base font-bold text-foreground mb-4 pb-3 border-b border-border">订单信息</div>
      <div class="list-item">
        <span class="text-muted-foreground">订单编号</span>
        <span class="text-foreground">{{ order.orderNo }}</span>
      </div>
      <div class="list-item">
        <span class="text-muted-foreground">充值号码</span>
        <span class="text-foreground">{{ order.phoneNumber }}</span>
      </div>
      <div class="list-item">
        <span class="text-muted-foreground">充值面值</span>
        <span class="text-foreground">{{ order.faceValue }}元</span>
      </div>
      <div class="list-item">
        <span class="text-muted-foreground">支付金额</span>
        <span class="text-destructive font-bold">¥{{ order.payAmount.toFixed(2) }}</span>
      </div>
      <div class="list-item">
        <span class="text-muted-foreground">创建时间</span>
        <span class="text-foreground">{{ formatTime(order.createdTime) }}</span>
      </div>
      <div class="list-item" v-if="order.payTime">
        <span class="text-muted-foreground">支付时间</span>
        <span class="text-foreground">{{ formatTime(order.payTime) }}</span>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="p-4 flex gap-3" v-if="order?.orderStatus === 0">
      <a-button type="primary" size="large" class="flex-1 h-11" @click="goToPay">去支付</a-button>
      <a-button size="large" class="flex-1 h-11" @click="handleCancel">取消订单</a-button>
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
  CloseCircleOutlined
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

function formatTime(time: string): string {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
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
