<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="flex items-center bg-primary text-white p-4">
      <LeftOutlined class="text-xl cursor-pointer" @click="router.back()" />
      <span class="flex-1 text-center text-lg font-bold">我的订单</span>
      <span class="w-5"></span>
    </div>

    <!-- Tabs -->
    <a-tabs v-model:activeKey="activeTab" class="bg-card" @change="loadOrders">
      <a-tab-pane key="all" tab="全部" />
      <a-tab-pane key="pending" tab="待支付" />
      <a-tab-pane key="processing" tab="充值中" />
      <a-tab-pane key="completed" tab="已完成" />
    </a-tabs>

    <!-- 订单列表 -->
    <div class="p-3">
      <div
        v-for="order in orders"
        :key="order.id"
        class="card-box mb-3 cursor-pointer"
        @click="router.push(`/order/${order.id}`)"
      >
        <div class="flex justify-between items-center pb-3 border-b border-border">
          <span class="text-sm text-muted-foreground">{{ order.orderNo }}</span>
          <span :class="['status-badge', getStatusClass(order.orderStatus)]">
            {{ getStatusText(order.orderStatus) }}
          </span>
        </div>
        <div class="flex justify-between items-center py-3">
          <div>
            <div class="text-sm text-foreground">充值号码：{{ order.phoneNumber }}</div>
            <div class="text-xs text-muted-foreground mt-1">面值：{{ order.faceValue }}元</div>
          </div>
          <div class="text-lg font-bold text-destructive">¥{{ order.payAmount.toFixed(2) }}</div>
        </div>
        <div class="flex justify-between items-center pt-3 border-t border-border">
          <span class="text-xs text-muted-foreground">{{ formatTime(order.createdTime) }}</span>
          <div class="flex gap-2" @click.stop>
            <a-button
              v-if="order.orderStatus === 0"
              type="primary"
              size="small"
              @click="goToPay(order)"
            >
              去支付
            </a-button>
            <a-button
              v-if="order.orderStatus === 0"
              size="small"
              @click="handleCancel(order)"
            >
              取消
            </a-button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="orders.length === 0" class="empty-state">
        <FileTextOutlined class="text-5xl mb-4" />
        <p>暂无订单</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import { LeftOutlined, FileTextOutlined } from '@ant-design/icons-vue'
import { getOrders, cancelOrder } from '@/api/order'
import dayjs from 'dayjs'
import type { Order } from '@/types'

const router = useRouter()

const activeTab = ref('all')
const orders = ref<Order[]>([])

onMounted(() => {
  loadOrders()
})

async function loadOrders() {
  const statusMap: Record<string, number | undefined> = {
    all: undefined,
    pending: 0,
    processing: 1,
    completed: 2
  }

  try {
    orders.value = await getOrders({
      status: statusMap[activeTab.value]
    })
  } catch {
    // 错误已处理
  }
}

function getStatusText(status: number): string {
  const map: Record<number, string> = {
    0: '待支付',
    1: '充值中',
    2: '已完成',
    3: '已取消',
    4: '已退款'
  }
  return map[status] || '未知'
}

function getStatusClass(status: number): string {
  const map: Record<number, string> = {
    0: 'status-pending',
    1: 'status-processing',
    2: 'status-completed',
    3: 'status-failed',
    4: 'status-failed'
  }
  return map[status] || ''
}

function formatTime(time: string): string {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

function goToPay(order: Order) {
  router.push({
    path: '/recharge/confirm',
    query: { orderId: order.id }
  })
}

async function handleCancel(order: Order) {
  Modal.confirm({
    title: '提示',
    content: '确定要取消该订单吗？',
    async onOk() {
      await cancelOrder(order.id)
      message.success('订单已取消')
      loadOrders()
    }
  })
}
</script>
