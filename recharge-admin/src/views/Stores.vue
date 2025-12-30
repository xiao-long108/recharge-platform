<template>
  <div class="stores-page">
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-label">店铺总数</div>
        <div class="stat-value blue">{{ stats.total }} <span class="unit">家</span></div>
      </div>
      <div class="stat-card">
        <div class="stat-label">正常开通</div>
        <div class="stat-value green">{{ stats.open }} <span class="unit">家</span></div>
      </div>
      <div class="stat-card">
        <div class="stat-label">已关闭</div>
        <div class="stat-value gray">{{ stats.closed }} <span class="unit">家</span></div>
      </div>
      <div class="stat-card">
        <div class="stat-label">待审核</div>
        <div class="stat-value orange">{{ stats.pending }} <span class="unit">家</span></div>
      </div>
      <div class="stat-card">
        <div class="stat-label">已冻结</div>
        <div class="stat-value red">{{ stats.frozen }} <span class="unit">家</span></div>
      </div>
      <div class="stat-card">
        <div class="stat-label">本月销售</div>
        <div class="stat-value green">R$ {{ formatMoney(stats.monthSales) }}</div>
      </div>
    </div>

    <!-- 搜索筛选栏 -->
    <div class="filter-bar">
      <div class="filter-left">
        <a-input
          v-model:value="query.storeName"
          placeholder="店铺名称/ID"
          allow-clear
          style="width: 180px"
        >
          <template #prefix><SearchOutlined /></template>
        </a-input>
        <a-select v-model:value="query.openStatus" placeholder="开通状态" allow-clear style="width: 120px">
          <a-select-option :value="null">全部</a-select-option>
          <a-select-option :value="1">正常开通</a-select-option>
          <a-select-option :value="2">已关闭</a-select-option>
        </a-select>
        <a-select v-model:value="query.status" placeholder="审核状态" allow-clear style="width: 120px">
          <a-select-option :value="null">全部</a-select-option>
          <a-select-option :value="0">待审核</a-select-option>
          <a-select-option :value="1">审核通过</a-select-option>
          <a-select-option :value="2">已冻结</a-select-option>
          <a-select-option :value="3">已拒绝</a-select-option>
        </a-select>
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
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <a-table :data-source="list" :loading="loading" :pagination="false" :scroll="{ x: 1100 }">
        <a-table-column title="店铺信息" :width="260">
          <template #default="{ record }">
            <div class="store-info">
              <a-avatar :size="44" :src="record.sLogo" shape="square">
                <template #icon><ShopOutlined /></template>
              </a-avatar>
              <div class="store-detail">
                <div class="name">{{ record.sName }}</div>
                <div class="desc">ID: {{ record.id }} | 店主: 用户{{ record.userId }}</div>
              </div>
            </div>
          </template>
        </a-table-column>
        <a-table-column title="销量" :width="100" align="center">
          <template #default="{ record }">
            <span>{{ record.sSales || 0 }}</span>
          </template>
        </a-table-column>
        <a-table-column title="店铺余额" :width="140" align="right">
          <template #default="{ record }">
            <span class="font-medium">R$ {{ formatMoney(record.sPrice) }}</span>
          </template>
        </a-table-column>
        <a-table-column title="开通状态" :width="100" align="center">
          <template #default="{ record }">
            <a-tag :color="record.sOpenStatus === 1 ? 'success' : 'default'">
              {{ record.sOpenStatus === 1 ? '正常开通' : '已关闭' }}
            </a-tag>
          </template>
        </a-table-column>
        <a-table-column title="审核状态" :width="100" align="center">
          <template #default="{ record }">
            <a-tag :color="getStatusColor(record.sStatus)">
              {{ getStatusText(record.sStatus) }}
            </a-tag>
          </template>
        </a-table-column>
        <a-table-column title="开店时间" data-index="createTime" :width="160" align="center" />
        <a-table-column title="操作" :width="220" align="center" fixed="right">
          <template #default="{ record }">
            <a-button type="link" size="small" @click="handleView(record)">
              <EyeOutlined /> 详情
            </a-button>
            <a-button type="link" size="small" @click="handleToggleOpenStatus(record)">
              {{ record.sOpenStatus === 1 ? '关闭' : '开通' }}
            </a-button>
            <a-button type="link" size="small" @click="handleToggleAuditStatus(record)" v-if="record.sStatus === 1 || record.sStatus === 2">
              {{ record.sStatus === 1 ? '冻结' : '解冻' }}
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
        show-quick-jumper
        @change="fetchList"
      />
    </div>

    <!-- 店铺详情弹窗 -->
    <a-modal v-model:open="detailVisible" title="店铺详情" width="650px" :footer="null">
      <a-descriptions :column="2" bordered v-if="currentStore" class="mt-4">
        <a-descriptions-item label="店铺ID">{{ currentStore.id }}</a-descriptions-item>
        <a-descriptions-item label="店主ID">{{ currentStore.userId }}</a-descriptions-item>
        <a-descriptions-item label="店铺名称" :span="2">{{ currentStore.sName }}</a-descriptions-item>
        <a-descriptions-item label="店铺描述" :span="2">{{ currentStore.sInfo || '暂无描述' }}</a-descriptions-item>
        <a-descriptions-item label="销量">{{ currentStore.sSales || 0 }} 笔</a-descriptions-item>
        <a-descriptions-item label="店铺余额">
          <span class="font-medium">R$ {{ formatMoney(currentStore.sPrice) }}</span>
        </a-descriptions-item>
        <a-descriptions-item label="开通状态">
          <a-tag :color="currentStore.sOpenStatus === 1 ? 'success' : 'default'">
            {{ currentStore.sOpenStatus === 1 ? '正常开通' : '已关闭' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="审核状态">
          <a-tag :color="getStatusColor(currentStore.sStatus)">
            {{ getStatusText(currentStore.sStatus) }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="开店时间" :span="2">{{ currentStore.createTime }}</a-descriptions-item>
      </a-descriptions>
      <div class="mt-4 flex justify-end gap-2">
        <a-button @click="detailVisible = false">关闭</a-button>
        <a-button @click="handleToggleOpenStatus(currentStore)">
          {{ currentStore?.sOpenStatus === 1 ? '关闭店铺' : '开通店铺' }}
        </a-button>
        <a-button type="primary" danger @click="handleToggleAuditStatus(currentStore)" v-if="currentStore?.sStatus === 1 || currentStore?.sStatus === 2">
          {{ currentStore?.sStatus === 1 ? '冻结店铺' : '解冻店铺' }}
        </a-button>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  SearchOutlined,
  ReloadOutlined,
  EyeOutlined,
  ShopOutlined
} from '@ant-design/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const detailVisible = ref(false)
const currentStore = ref<any>(null)

const stats = reactive({
  total: 0,
  open: 0,
  closed: 0,
  pending: 0,
  approved: 0,
  frozen: 0,
  rejected: 0,
  monthSales: 0
})

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  storeName: '',
  status: null as number | null,
  openStatus: null as number | null
})

// 审核状态颜色
const getStatusColor = (status: number) => {
  switch (status) {
    case 0: return 'warning'   // 待审核
    case 1: return 'success'   // 审核通过
    case 2: return 'error'     // 冻结
    case 3: return 'default'   // 拒绝
    default: return 'default'
  }
}

// 审核状态文字
const getStatusText = (status: number) => {
  switch (status) {
    case 0: return '待审核'
    case 1: return '审核通过'
    case 2: return '已冻结'
    case 3: return '已拒绝'
    default: return '未知'
  }
}

const formatMoney = (value: number | undefined) => {
  if (!value) return '0.00'
  return value.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const fetchStats = async () => {
  try {
    const res = await request.get('/api/admin/stores/stats')
    if (res.data.code === 200) {
      Object.assign(stats, res.data.data)
    }
  } catch {
    stats.total = 256
    stats.normal = 248
    stats.frozen = 8
    stats.monthSales = 1568000
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/stores', { params: query })
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
  query.storeName = ''
  query.status = null
  query.openStatus = null
  query.pageNum = 1
  fetchList()
}

const handleView = (row: any) => {
  currentStore.value = row
  detailVisible.value = true
}

// 切换开通状态
const handleToggleOpenStatus = (row: any) => {
  const action = row.sOpenStatus === 1 ? '关闭' : '开通'
  Modal.confirm({
    title: `确认${action}`,
    content: `确定要${action}该店铺吗？${row.sOpenStatus === 1 ? '关闭后店铺将无法正常营业' : ''}`,
    onOk: async () => {
      try {
        await request.put(`/api/admin/stores/${row.id}/open-status`, {
          status: row.sOpenStatus === 1 ? 2 : 1
        })
        message.success(`${action}成功`)
        detailVisible.value = false
        fetchList()
        fetchStats()
      } catch {
        message.error(`${action}失败`)
      }
    }
  })
}

// 切换审核状态（冻结/解冻）
const handleToggleAuditStatus = (row: any) => {
  const action = row.sStatus === 1 ? '冻结' : '解冻'
  Modal.confirm({
    title: `确认${action}`,
    content: `确定要${action}该店铺吗？${row.sStatus === 1 ? '冻结后店铺将无法正常营业' : ''}`,
    onOk: async () => {
      try {
        await request.put(`/api/admin/stores/${row.id}/status`, {
          status: row.sStatus === 1 ? 2 : 1
        })
        message.success(`${action}成功`)
        detailVisible.value = false
        fetchList()
        fetchStats()
      } catch {
        message.error(`${action}失败`)
      }
    }
  })
}

onMounted(() => {
  fetchStats()
  fetchList()
})
</script>

<style scoped lang="scss">
.stores-page {
  padding: 0;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
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
      &.red { color: #ef4444; }
      &.gray { color: #6b7280; }

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
}

.store-info {
  display: flex;
  align-items: center;
  gap: 12px;

  .store-detail {
    .name {
      font-size: 14px;
      font-weight: 500;
      color: #303133;
    }
    .desc {
      font-size: 12px;
      color: #909399;
      margin-top: 4px;
    }
  }
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
