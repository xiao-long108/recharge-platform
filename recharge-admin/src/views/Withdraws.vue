<template>
  <div class="withdraws-page">
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-label">待审核</div>
        <div class="stat-value orange">{{ stats.pendingCount }} <span class="unit">笔</span></div>
      </div>
      <div class="stat-card">
        <div class="stat-label">今日提现</div>
        <div class="stat-value blue">R$ {{ formatMoney(stats.todayAmount) }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">本月提现</div>
        <div class="stat-value green">R$ {{ formatMoney(stats.monthAmount) }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">累计提现</div>
        <div class="stat-value green">R$ {{ formatMoney(stats.totalAmount) }}</div>
      </div>
    </div>

    <!-- 搜索筛选栏 -->
    <div class="filter-bar">
      <div class="filter-left">
        <a-input
          v-model:value="query.userId"
          placeholder="用户ID"
          allow-clear
          style="width: 150px"
        >
          <template #prefix><SearchOutlined /></template>
        </a-input>
        <a-select v-model:value="query.status" placeholder="提现状态" allow-clear style="width: 140px">
          <a-select-option :value="null">全部</a-select-option>
          <a-select-option :value="3">待审核</a-select-option>
          <a-select-option :value="1">成功</a-select-option>
          <a-select-option :value="2">失败</a-select-option>
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
        <a-table-column title="提现单号" :width="160">
          <template #default="{ record }">
            <div class="order-no">W{{ record.id }}</div>
          </template>
        </a-table-column>
        <a-table-column title="用户信息" :width="180">
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
        <a-table-column title="提现金额" data-index="price" :width="140" align="right">
          <template #default="{ record }">
            <span class="amount">R$ {{ formatMoney(record.price) }}</span>
          </template>
        </a-table-column>
        <a-table-column title="银行账户" :width="180">
          <template #default="{ record }">
            <div class="bank-info">
              <div class="bank-name">{{ record.bankName || '-' }}</div>
              <div class="account-no">{{ record.accountNumber || '-' }}</div>
            </div>
          </template>
        </a-table-column>
        <a-table-column title="状态" data-index="status" :width="100" align="center">
          <template #default="{ record }">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
        </a-table-column>
        <a-table-column title="申请时间" data-index="createTime" :width="160" align="center" />
        <a-table-column title="操作" :width="140" align="center" fixed="right">
          <template #default="{ record }">
            <template v-if="record.status === 3">
              <a-button type="link" size="small" @click="handleApprove(record)">
                <CheckOutlined /> 通过
              </a-button>
              <a-button type="link" danger size="small" @click="handleReject(record)">
                <CloseOutlined /> 拒绝
              </a-button>
            </template>
            <a-button v-else type="link" size="small" @click="handleView(record)">
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

    <!-- 提现详情弹窗 -->
    <a-modal v-model:open="detailVisible" title="提现详情" width="550px">
      <a-descriptions :column="2" bordered v-if="currentWithdraw">
        <a-descriptions-item label="提现单号">W{{ currentWithdraw.id }}</a-descriptions-item>
        <a-descriptions-item label="用户ID">{{ currentWithdraw.userId }}</a-descriptions-item>
        <a-descriptions-item label="提现金额">
          <span class="amount">R$ {{ formatMoney(currentWithdraw.price) }}</span>
        </a-descriptions-item>
        <a-descriptions-item label="提现状态">
          <a-tag :color="getStatusColor(currentWithdraw.status)">
            {{ getStatusText(currentWithdraw.status) }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="银行名称">{{ currentWithdraw.bankName || '-' }}</a-descriptions-item>
        <a-descriptions-item label="账户号码">{{ currentWithdraw.accountNumber || '-' }}</a-descriptions-item>
        <a-descriptions-item label="申请时间" :span="2">{{ currentWithdraw.createTime }}</a-descriptions-item>
        <a-descriptions-item label="拒绝原因" :span="2" v-if="currentWithdraw.reason">
          <span class="reject-reason">{{ currentWithdraw.reason }}</span>
        </a-descriptions-item>
      </a-descriptions>
      <template #footer>
        <a-button @click="detailVisible = false">关闭</a-button>
      </template>
    </a-modal>

    <!-- 拒绝原因弹窗 -->
    <a-modal v-model:open="rejectDialogVisible" title="拒绝提现" width="450px">
      <a-form :model="rejectForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="提现金额">
          <span class="amount">R$ {{ formatMoney(rejectUser?.price) }}</span>
        </a-form-item>
        <a-form-item label="拒绝原因" required>
          <a-textarea
            v-model:value="rejectForm.reason"
            :rows="3"
            placeholder="请输入拒绝原因，将反馈给用户"
          />
        </a-form-item>
      </a-form>
      <template #footer>
        <a-button @click="rejectDialogVisible = false">取消</a-button>
        <a-button danger @click="submitReject">确认拒绝</a-button>
      </template>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { SearchOutlined, ReloadOutlined, UploadOutlined, EyeOutlined, CheckOutlined, CloseOutlined } from '@ant-design/icons-vue'
import request from '@/utils/request'

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const dateRange = ref<string[]>([])
const detailVisible = ref(false)
const currentWithdraw = ref<any>(null)

const stats = reactive({
  pendingCount: 0,
  todayAmount: 0,
  monthAmount: 0,
  totalAmount: 0
})

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  userId: '',
  status: null as number | null
})

const formatMoney = (value: number | undefined) => {
  if (!value) return '0.00'
  return value.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const getStatusText = (status: number) => {
  const map: Record<number, string> = {
    1: '成功',
    2: '失败',
    3: '待审核'
  }
  return map[status] || '未知'
}

const getStatusColor = (status: number) => {
  const map: Record<number, string> = {
    1: 'success',
    2: 'error',
    3: 'warning'
  }
  return map[status] || 'default'
}

const fetchStats = async () => {
  try {
    const res = await request.get('/api/admin/withdraws/stats')
    if (res.data.code === 200) {
      Object.assign(stats, res.data.data)
    }
  } catch (e) {
    console.error('获取提现统计失败', e)
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
    const res = await request.get('/api/admin/withdraws', { params })
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
  query.userId = ''
  query.status = null
  query.pageNum = 1
  dateRange.value = []
  fetchList()
}

const handleView = (row: any) => {
  currentWithdraw.value = row
  detailVisible.value = true
}

const handleExport = () => {
  message.info('导出功能开发中')
}

const handleApprove = async (row: any) => {
  Modal.confirm({
    title: '审核通过',
    content: `确定要通过该提现申请吗？金额：R$ ${formatMoney(row.price)}`,
    okText: '确认通过',
    onOk: async () => {
      try {
        await request.post(`/api/admin/withdraws/${row.id}/approve`)
        message.success('审核通过')
        fetchList()
        fetchStats()
      } catch (e) {
        message.error('操作失败')
      }
    }
  })
}

// 拒绝弹窗
const rejectDialogVisible = ref(false)
const rejectUser = ref<any>(null)
const rejectForm = reactive({
  reason: ''
})

const handleReject = (row: any) => {
  rejectUser.value = row
  rejectForm.reason = ''
  rejectDialogVisible.value = true
}

const submitReject = async () => {
  if (!rejectForm.reason.trim()) {
    message.warning('请输入拒绝原因')
    return
  }

  try {
    await request.post(`/api/admin/withdraws/${rejectUser.value.id}/reject`, rejectForm)
    message.success('已拒绝该提现申请')
    rejectDialogVisible.value = false
    fetchList()
    fetchStats()
  } catch (e) {
    message.error('操作失败')
  }
}

onMounted(() => {
  fetchStats()
  fetchList()
})
</script>

<style scoped lang="scss">
.withdraws-page {
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

.order-no {
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

.amount {
  color: #f56c6c;
  font-weight: 600;
}

.bank-info {
  .bank-name {
    font-size: 14px;
    color: #303133;
  }

  .account-no {
    font-size: 12px;
    color: #909399;
    font-family: monospace;
  }
}

.reject-reason {
  color: #f56c6c;
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
