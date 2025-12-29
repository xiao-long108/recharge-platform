<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="flex items-center bg-primary text-white p-4">
      <LeftOutlined class="text-xl cursor-pointer" @click="router.back()" />
      <span class="flex-1 text-center text-lg font-bold">余额宝</span>
      <span class="w-5"></span>
    </div>

    <!-- 余额卡片 -->
    <div class="bg-gradient-to-r from-warning to-[hsl(42,84%,70%)] p-8 text-white text-center">
      <div class="text-sm opacity-90">总金额（元）</div>
      <div class="text-4xl font-bold mt-2">{{ account?.balance?.toFixed(2) || '0.00' }}</div>
      <div class="flex mt-6">
        <div class="flex-1 text-center">
          <div class="text-base font-bold">¥{{ account?.yesterdayIncome?.toFixed(4) || '0.0000' }}</div>
          <div class="text-xs opacity-90 mt-1">昨日收益</div>
        </div>
        <div class="flex-1 text-center">
          <div class="text-base font-bold">¥{{ account?.totalIncome?.toFixed(2) || '0.00' }}</div>
          <div class="text-xs opacity-90 mt-1">累计收益</div>
        </div>
        <div class="flex-1 text-center">
          <div class="text-base font-bold">{{ annualRatePercent }}%</div>
          <div class="text-xs opacity-90 mt-1">年化收益率</div>
        </div>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="card-box m-3 flex gap-4">
      <a-button type="primary" size="large" class="flex-1 h-11" @click="goTransfer('in')">转入</a-button>
      <a-button size="large" class="flex-1 h-11" @click="goTransfer('out')">转出</a-button>
    </div>

    <!-- 收益记录 -->
    <div class="card-box m-3">
      <div class="flex justify-between items-center mb-4">
        <span class="text-base font-bold text-foreground">收益记录</span>
        <span class="text-sm text-primary cursor-pointer" @click="showAllRecords = true">查看全部</span>
      </div>
      <div v-for="record in recentRecords" :key="record.id" class="list-item">
        <div>
          <div class="text-sm text-foreground">{{ getRecordTypeText(record.recordType) }}</div>
          <div class="text-xs text-muted-foreground mt-1">{{ formatTime(record.createdTime) }}</div>
        </div>
        <div class="text-base font-bold" :class="record.recordType === 2 ? 'text-destructive' : 'text-success'">
          {{ record.recordType === 2 ? '-' : '+' }}¥{{ record.amount.toFixed(4) }}
        </div>
      </div>
      <div v-if="recentRecords.length === 0" class="text-center py-5 text-muted-foreground">暂无记录</div>
    </div>

    <!-- 全部记录 -->
    <a-drawer v-model:open="showAllRecords" title="全部记录" placement="bottom" height="70%">
      <div v-for="record in allRecords" :key="record.id" class="list-item">
        <div>
          <div class="text-sm text-foreground">{{ getRecordTypeText(record.recordType) }}</div>
          <div class="text-xs text-muted-foreground mt-1">{{ formatTime(record.createdTime) }}</div>
        </div>
        <div class="text-base font-bold" :class="record.recordType === 2 ? 'text-destructive' : 'text-success'">
          {{ record.recordType === 2 ? '-' : '+' }}¥{{ record.amount.toFixed(4) }}
        </div>
      </div>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { LeftOutlined } from '@ant-design/icons-vue'
import { getYuebaoAccount, getYuebaoRecords } from '@/api/yuebao'
import dayjs from 'dayjs'
import type { YuebaoAccount, YuebaoRecord } from '@/types'

const router = useRouter()
const account = ref<YuebaoAccount | null>(null)
const recentRecords = ref<YuebaoRecord[]>([])
const allRecords = ref<YuebaoRecord[]>([])
const showAllRecords = ref(false)

const annualRatePercent = computed(() => ((account.value?.annualRate || 0) * 100).toFixed(2))

watch(showAllRecords, async (val) => {
  if (val && allRecords.value.length === 0) {
    try { allRecords.value = await getYuebaoRecords({ pageSize: 50 }) } catch {}
  }
})

onMounted(async () => {
  try {
    account.value = await getYuebaoAccount()
    recentRecords.value = await getYuebaoRecords({ pageSize: 5 })
  } catch {}
})

function getRecordTypeText(type: number): string {
  return { 1: '转入', 2: '转出', 3: '收益' }[type] || '未知'
}

function formatTime(time: string): string {
  return dayjs(time).format('MM-DD HH:mm')
}

function goTransfer(type: string) {
  router.push({ path: '/yuebao/transfer', query: { type } })
}
</script>
