<template>
  <div class="orders-page">
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-label">今日充值金额</div>
        <div class="stat-value green">R$ {{ formatMoney(stats.todayAmount) }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">今日充值笔数</div>
        <div class="stat-value blue">{{ stats.todayCount }} <span class="unit">笔</span></div>
      </div>
      <div class="stat-card">
        <div class="stat-label">本月充值金额</div>
        <div class="stat-value green">R$ {{ formatMoney(stats.monthAmount) }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">笔均金额</div>
        <div class="stat-value orange">R$ {{ formatMoney(stats.avgAmount) }}</div>
      </div>
    </div>

    <!-- 搜索筛选栏 -->
    <div class="filter-bar">
      <div class="filter-left">
        <a-input
          v-model:value="query.orderNo"
          placeholder="订单号/用户ID"
          allow-clear
          style="width: 180px"
        >
          <template #prefix><SearchOutlined /></template>
        </a-input>
        <a-select v-model:value="query.payChannel" placeholder="支付通道" allow-clear style="width: 140px">
          <a-select-option value="PIX">PIX</a-select-option>
          <a-select-option value="Boleto">Boleto</a-select-option>
          <a-select-option value="CreditCard">Credit Card</a-select-option>
          <a-select-option value="TED">TED</a-select-option>
        </a-select>
        <a-select v-model:value="query.status" placeholder="订单状态" allow-clear style="width: 140px">
          <a-select-option :value="null">全部</a-select-option>
          <a-select-option :value="0">待支付</a-select-option>
          <a-select-option :value="1">处理中</a-select-option>
          <a-select-option :value="2">成功</a-select-option>
          <a-select-option :value="-1">已取消</a-select-option>
          <a-select-option :value="-2">失败</a-select-option>
        </a-select>
        <a-range-picker
          v-model:value="dateRange"
          value-format="YYYY-MM-DD"
          style="width: 260px"
        />
      </div>
      <div class="filter-right">
        <a-button type="primary" @click="fetchList">
          <template #icon><SearchOutlined /></template>
          查询
        </a-button>
        <a-button @click="resetQuery">
          <template #icon><ReloadOutlined /></template>
          重置
        </a-button>
        <a-button @click="handleExport">
          <template #icon><UploadOutlined /></template>
          导出
        </a-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <a-table :data-source="list" :loading="loading" :pagination="false" :scroll="{ x: 1200 }">
        <a-table-column title="订单信息" data-index="orderNo" :width="160">
          <template #default="{ record }">
            <div class="order-info">{{ record.orderNo || 'R' + record.id }}</div>
          </template>
        </a-table-column>
        <a-table-column title="用户信息" :width="160">
          <template #default="{ record }">
            <div class="user-info">
              <a-avatar :size="36" :src="record.userAvatar || defaultAvatar">
                {{ record.userNickname?.charAt(0) || 'U' }}
              </a-avatar>
              <div class="user-detail">
                <div class="nickname">{{ record.userNickname || '用户' + record.userId }}</div>
                <div class="user-id">ID: {{ record.userId }}</div>
              </div>
            </div>
          </template>
        </a-table-column>
        <a-table-column title="充值金额" data-index="price" :width="120" align="right">
          <template #default="{ record }">
            <span>R$ {{ formatMoney(record.price) }}</span>
          </template>
        </a-table-column>
        <a-table-column title="实付金额" :width="120" align="right">
          <template #default="{ record }">
            <span class="pay-amount">R$ {{ formatMoney(record.payAmount || record.price) }}</span>
          </template>
        </a-table-column>
        <a-table-column title="支付通道" data-index="payChannel" :width="110" align="center">
          <template #default="{ record }">
            <a-tag :color="getChannelColor(record.payChannel)">
              {{ record.payChannel || 'PIX' }}
            </a-tag>
          </template>
        </a-table-column>
        <a-table-column title="状态" data-index="status" :width="90" align="center">
          <template #default="{ record }">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
        </a-table-column>
        <a-table-column title="支付时间" data-index="payTime" :width="160" align="center">
          <template #default="{ record }">
            <span>{{ record.payTime || '-' }}</span>
          </template>
        </a-table-column>
        <a-table-column title="创建时间" data-index="createTime" :width="160" align="center" />
        <a-table-column title="操作" :width="80" align="center" fixed="right">
          <template #default="{ record }">
            <a-button type="link" @click="handleView(record)">
              <EyeOutlined />
            </a-button>
          </template>
        </a-table-column>
      </a-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <span class="total-text">共 {{ total }} 条记录</span>
      <a-pagination
        v-model:current="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        :show-size-changer="true"
        :page-size-options="['10', '20', '50', '100']"
        show-quick-jumper
        @change="fetchList"
        @show-size-change="fetchList"
      />
    </div>

    <!-- 订单详情弹窗 -->
    <a-modal v-model:open="detailVisible" title="订单详情" width="600px">
      <a-descriptions :column="2" bordered v-if="currentOrder">
        <a-descriptions-item label="订单号">{{ currentOrder.orderNo }}</a-descriptions-item>
        <a-descriptions-item label="用户ID">{{ currentOrder.userId }}</a-descriptions-item>
        <a-descriptions-item label="充值手机">{{ currentOrder.mobile }}</a-descriptions-item>
        <a-descriptions-item label="充值金额">R$ {{ formatMoney(currentOrder.price) }}</a-descriptions-item>
        <a-descriptions-item label="实付金额">R$ {{ formatMoney(currentOrder.payAmount) }}</a-descriptions-item>
        <a-descriptions-item label="支付状态">
          <a-tag :color="currentOrder.payStatus === 1 ? 'success' : ''">
            {{ currentOrder.payStatus === 1 ? '已支付' : '未支付' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="订单状态">
          <a-tag :color="getStatusColor(currentOrder.status)">
            {{ getStatusText(currentOrder.status) }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="创建时间">{{ currentOrder.createTime }}</a-descriptions-item>
      </a-descriptions>
      <template #footer>
        <a-button @click="detailVisible = false">关闭</a-button>
        <a-button type="primary" @click="handleUpdateStatus(currentOrder, 2)" v-if="currentOrder?.status === 1">
          标记成功
        </a-button>
        <a-button danger @click="handleUpdateStatus(currentOrder, -2)" v-if="currentOrder?.status === 1">
          标记失败
        </a-button>
        <a-button type="default" @click="handleRefund(currentOrder)" v-if="currentOrder?.payStatus === 1 && currentOrder?.status !== -3">
          退款
        </a-button>
      </template>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { SearchOutlined, ReloadOutlined, UploadOutlined, EyeOutlined } from '@ant-design/icons-vue'
import request from '@/utils/request'

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const dateRange = ref<string[]>([])
const detailVisible = ref(false)
const currentOrder = ref<any>(null)

const stats = reactive({
  todayAmount: 0,
  todayCount: 0,
  monthAmount: 0,
  avgAmount: 0
})

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  orderNo: '',
  mobile: '',
  status: null as number | null,
  payChannel: ''
})

const formatMoney = (value: number | undefined) => {
  if (!value) return '0.00'
  return value.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const getStatusText = (status: number) => {
  const map: Record<number, string> = {
    0: '待支付',
    1: '处理中',
    2: '成功',
    '-1': '已取消',
    '-2': '失败',
    '-3': '已退款'
  }
  return map[status] || '未知'
}

const getStatusColor = (status: number) => {
  const map: Record<number, string> = {
    0: 'default',
    1: 'orange',
    2: 'success',
    '-1': 'default',
    '-2': 'error',
    '-3': 'warning'
  }
  return map[status] || 'default'
}

const getChannelColor = (channel: string) => {
  const map: Record<string, string> = {
    'PIX': 'blue',
    'Boleto': 'orange',
    'CreditCard': 'green',
    'TED': 'default'
  }
  return map[channel] || 'default'
}

const fetchStats = async () => {
  try {
    const res = await request.get('/api/admin/stats')
    if (res.data.code === 200) {
      Object.assign(stats, res.data.data)
    }
  } catch (e) {
    // 使用模拟数据
    stats.todayAmount = 125680050
    stats.todayCount = 2568
    stats.monthAmount = 3568000000
    stats.avgAmount = 48950
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const params = { ...query }
    if (dateRange.value?.length === 2) {
      (params as any).startDate = dateRange.value[0]
      ;(params as any).endDate = dateRange.value[1]
    }
    const res = await request.get('/api/admin/orders', { params })
    if (res.data.code === 200) {
      list.value = res.data.data.records
      total.value = res.data.data.total
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  query.orderNo = ''
  query.mobile = ''
  query.status = null
  query.payChannel = ''
  query.pageNum = 1
  dateRange.value = []
  fetchList()
}

const handleView = (row: any) => {
  currentOrder.value = row
  detailVisible.value = true
}

const handleUpdateStatus = async (row: any, status: number) => {
  const action = status === 2 ? '标记为成功' : '标记为失败'
  Modal.confirm({
    title: '提示',
    content: `确定要${action}吗？`,
    onOk: async () => {
      try {
        await request.put(`/api/admin/orders/${row.id}/status`, { status })
        message.success('操作成功')
        detailVisible.value = false
        fetchList()
      } catch (e) {
        message.error('操作失败')
      }
    }
  })
}

const handleRefund = async (row: any) => {
  Modal.confirm({
    title: '提示',
    content: '确定要退款吗？退款后将返还用户余额',
    onOk: async () => {
      try {
        await request.post(`/api/admin/orders/${row.id}/refund`)
        message.success('退款成功')
        detailVisible.value = false
        fetchList()
      } catch (e) {
        message.error('退款失败')
      }
    }
  })
}

const handleExport = () => {
  message.info('导出功能开发中')
}

onMounted(() => {
  fetchStats()
  fetchList()
})
</script>

<style scoped lang="scss">
.orders-page {
  padding: 0;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 16px;

  .stat-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px 24px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

    .stat-label {
      font-size: 14px;
      color: #909399;
      margin-bottom: 12px;
    }

    .stat-value {
      font-size: 28px;
      font-weight: 600;

      &.green { color: #10b981; }
      &.blue { color: #3b82f6; }
      &.orange { color: #f59e0b; }

      .unit {
        font-size: 14px;
        font-weight: normal;
        color: #909399;
        margin-left: 4px;
      }
    }
  }
}

.filter-bar {
  background: #fff;
  border-radius: 8px;
  padding: 16px 20px;
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

  .filter-left {
    display: flex;
    gap: 12px;
    align-items: center;
  }

  .filter-right {
    display: flex;
    gap: 8px;
  }
}

.table-container {
  background: #fff;
  border-radius: 8px;
  padding: 0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  overflow: hidden;

  :deep(.el-table) {
    --el-table-header-bg-color: #fafafa;

    th {
      font-weight: 500;
      color: #606266;
    }
  }
}

.order-info {
  font-family: monospace;
  color: #303133;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;

  .user-detail {
    .nickname {
      font-size: 14px;
      color: #303133;
      font-weight: 500;
    }

    .user-id {
      font-size: 12px;
      color: #909399;
    }
  }
}

.pay-amount {
  color: #f56c6c;
  font-weight: 500;
}

.pagination-container {
  background: #fff;
  border-radius: 0 0 8px 8px;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #ebeef5;

  .total-text {
    font-size: 14px;
    color: #606266;
  }
}
</style>
