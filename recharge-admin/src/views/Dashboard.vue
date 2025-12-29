<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
      <a-card>
        <a-statistic
          title="总用户数"
          :value="stats.totalUsers || 0"
          :value-style="{ color: '#1890ff' }"
        >
          <template #prefix>
            <UserOutlined />
          </template>
        </a-statistic>
      </a-card>

      <a-card>
        <a-statistic
          title="总订单数"
          :value="stats.totalOrders || 0"
          :value-style="{ color: '#52c41a' }"
        >
          <template #prefix>
            <FileTextOutlined />
          </template>
        </a-statistic>
      </a-card>

      <a-card>
        <a-statistic
          title="待处理订单"
          :value="stats.pendingOrders || 0"
          :value-style="{ color: '#faad14' }"
        >
          <template #prefix>
            <ClockCircleOutlined />
          </template>
        </a-statistic>
      </a-card>

      <a-card>
        <a-statistic
          title="待审核提现"
          :value="stats.pendingWithdraws || 0"
          :value-style="{ color: '#ff4d4f' }"
        >
          <template #prefix>
            <WalletOutlined />
          </template>
        </a-statistic>
      </a-card>
    </div>

    <!-- 第二行统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <a-card>
        <a-statistic
          title="今日新增用户"
          :value="stats.todayUsers || 0"
          :value-style="{ color: '#722ed1' }"
        >
          <template #prefix>
            <UserAddOutlined />
          </template>
          <template #suffix>
            <span class="text-xs text-green-500 ml-2">
              <ArrowUpOutlined /> 12%
            </span>
          </template>
        </a-statistic>
      </a-card>

      <a-card>
        <a-statistic
          title="今日订单"
          :value="stats.todayOrders || 0"
          :value-style="{ color: '#13c2c2' }"
        >
          <template #prefix>
            <FileAddOutlined />
          </template>
        </a-statistic>
      </a-card>

      <a-card>
        <a-statistic
          title="成功订单"
          :value="stats.successOrders || 0"
          :value-style="{ color: '#52c41a' }"
        >
          <template #prefix>
            <CheckCircleOutlined />
          </template>
        </a-statistic>
      </a-card>

      <a-card>
        <a-statistic
          title="今日充值金额"
          :value="stats.todayAmount || 0"
          :precision="2"
          :value-style="{ color: '#eb2f96' }"
          prefix="¥"
        >
        </a-statistic>
      </a-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  UserOutlined,
  FileTextOutlined,
  ClockCircleOutlined,
  WalletOutlined,
  UserAddOutlined,
  FileAddOutlined,
  CheckCircleOutlined,
  ArrowUpOutlined,
} from '@ant-design/icons-vue'
import request from '@/utils/request'

const stats = ref<any>({})

const fetchStats = async () => {
  try {
    const res = await request.get('/api/admin/stats/dashboard')
    if (res.data.code === 200) {
      stats.value = res.data.data
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
.dashboard :deep(.ant-card) {
  border-radius: 8px;
}

.dashboard :deep(.ant-statistic-title) {
  font-size: 14px;
  color: #8c8c8c;
  margin-bottom: 8px;
}

.dashboard :deep(.ant-statistic-content) {
  font-size: 28px;
}

.dashboard :deep(.ant-statistic-content-prefix) {
  margin-right: 8px;
}
</style>
