<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="flex items-center bg-primary text-white p-4">
      <LeftOutlined class="text-xl cursor-pointer" @click="router.back()" />
      <span class="flex-1 text-center text-lg font-bold">提现记录</span>
      <span class="w-5"></span>
    </div>

    <!-- 记录列表 -->
    <div class="p-3">
      <div v-for="record in records" :key="record.id" class="card-box mb-2">
        <div class="flex justify-between items-center">
          <span class="text-sm text-foreground">{{ record.withdrawType === 1 ? '余额提现' : '佣金提现' }}</span>
          <span class="text-base font-bold text-destructive">-¥{{ record.amount.toFixed(2) }}</span>
        </div>
        <div class="text-xs text-muted-foreground mt-2">
          手续费：¥{{ record.fee.toFixed(2) }} | 到账：¥{{ record.actualAmount.toFixed(2) }}
        </div>
        <div class="flex justify-between items-center mt-2">
          <span class="text-xs text-border">{{ formatTime(record.createdTime) }}</span>
          <span :class="['status-badge', getStatusClass(record.status)]">{{ getStatusText(record.status) }}</span>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="records.length === 0" class="empty-state">
        <WalletOutlined class="text-5xl mb-4" />
        <p>暂无提现记录</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { LeftOutlined, WalletOutlined } from '@ant-design/icons-vue'
import { getWithdrawRecords } from '@/api/withdraw'
import dayjs from 'dayjs'
import type { WithdrawRecord } from '@/types'

const router = useRouter()
const records = ref<WithdrawRecord[]>([])

onMounted(async () => {
  try { records.value = await getWithdrawRecords({}) } catch {}
})

function getStatusText(status: number): string {
  return { 0: '待审核', 1: '处理中', 2: '已完成', 3: '已拒绝', 4: '已取消' }[status] || '未知'
}

function getStatusClass(status: number): string {
  return { 0: 'status-pending', 1: 'status-processing', 2: 'status-completed', 3: 'status-failed', 4: 'status-failed' }[status] || ''
}

function formatTime(time: string): string {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}
</script>
