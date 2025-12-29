<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="flex items-center bg-primary text-white p-4">
      <LeftOutlined class="text-xl cursor-pointer" @click="router.back()" />
      <span class="flex-1 text-center text-lg font-bold">佣金明细</span>
      <span class="w-5"></span>
    </div>

    <!-- 记录列表 -->
    <div class="p-3">
      <div v-for="record in records" :key="record.id" class="card-box mb-2">
        <div class="flex justify-between items-center">
          <span class="text-sm text-foreground">{{ getLevelText(record.level) }}佣金</span>
          <span class="text-base font-bold text-success">+¥{{ record.commissionAmount.toFixed(2) }}</span>
        </div>
        <div class="text-xs text-muted-foreground mt-2">
          订单金额：¥{{ record.orderAmount.toFixed(2) }} |
          佣金比例：{{ (record.commissionRate * 100).toFixed(1) }}%
        </div>
        <div class="text-xs text-border mt-1">{{ formatTime(record.createdTime) }}</div>
      </div>

      <!-- 空状态 -->
      <div v-if="records.length === 0" class="empty-state">
        <DollarOutlined class="text-5xl mb-4" />
        <p>暂无佣金记录</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { LeftOutlined, DollarOutlined } from '@ant-design/icons-vue'
import { getCommissionRecords } from '@/api/team'
import dayjs from 'dayjs'
import type { CommissionRecord } from '@/types'

const router = useRouter()

const records = ref<CommissionRecord[]>([])

onMounted(async () => {
  await loadRecords()
})

async function loadRecords() {
  try {
    records.value = await getCommissionRecords({})
  } catch {
    // 错误已处理
  }
}

function getLevelText(level: number): string {
  const map: Record<number, string> = {
    1: '一级',
    2: '二级',
    3: '三级'
  }
  return map[level] || ''
}

function formatTime(time: string): string {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}
</script>
