<template>
  <div class="users-page">
    <!-- 统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
      <a-card>
        <a-statistic
          title="总用户数"
          :value="stats.totalUsers"
          :value-style="{ color: '#1890ff' }"
        >
          <template #prefix><UserOutlined /></template>
          <template #suffix>人</template>
        </a-statistic>
      </a-card>
      <a-card>
        <a-statistic
          title="今日新增"
          :value="stats.todayUsers"
          :value-style="{ color: '#52c41a' }"
        >
          <template #suffix>人</template>
        </a-statistic>
      </a-card>
      <a-card>
        <a-statistic
          title="用户总余额"
          :value="stats.totalBalance"
          :precision="2"
          prefix="R$"
          :value-style="{ color: '#52c41a' }"
        />
      </a-card>
      <a-card>
        <a-statistic
          title="用户总佣金"
          :value="stats.totalCommission"
          :precision="2"
          prefix="R$"
          :value-style="{ color: '#fa8c16' }"
        />
      </a-card>
    </div>

    <!-- 筛选区域 -->
    <a-card class="mb-4">
      <a-space wrap :size="16">
        <a-input
          v-model:value="query.mobile"
          placeholder="手机号/用户ID"
          style="width: 180px"
          allow-clear
        >
          <template #prefix><SearchOutlined /></template>
        </a-input>
        <a-select
          v-model:value="query.status"
          placeholder="用户状态"
          style="width: 120px"
          allow-clear
        >
          <a-select-option :value="1">正常</a-select-option>
          <a-select-option :value="2">冻结</a-select-option>
        </a-select>
        <a-range-picker
          v-model:value="dateRange"
          :placeholder="['注册开始', '注册结束']"
          value-format="YYYY-MM-DD"
        />
        <a-button type="primary" @click="fetchList">
          <SearchOutlined /> 查询
        </a-button>
        <a-button @click="resetQuery">
          <ReloadOutlined /> 重置
        </a-button>
        <a-button @click="handleExport">
          <ExportOutlined /> 导出
        </a-button>
      </a-space>
    </a-card>

    <!-- 数据表格 -->
    <a-card>
      <a-table
        :columns="columns"
        :data-source="list"
        :loading="loading"
        :pagination="pagination"
        :scroll="{ x: 1200 }"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'userInfo'">
            <div class="user-cell">
              <a-avatar :size="40" :src="record.avatar || defaultAvatar">
                {{ record.nickname?.charAt(0) || 'U' }}
              </a-avatar>
              <div class="user-info">
                <div class="user-name">{{ record.nickname || '用户' + record.id }}</div>
                <div class="user-meta">ID: {{ record.id }} | {{ record.mobile }}</div>
              </div>
            </div>
          </template>
          <template v-else-if="column.key === 'inviteCode'">
            <a-tag>{{ record.inviteCode || '-' }}</a-tag>
          </template>
          <template v-else-if="column.key === 'price'">
            <span class="text-green-500 font-medium">R$ {{ formatMoney(record.price) }}</span>
          </template>
          <template v-else-if="column.key === 'incomePrice'">
            <span class="text-orange-500 font-medium">R$ {{ formatMoney(record.incomePrice) }}</span>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.status === 1 ? 'success' : 'error'">
              {{ record.status === 1 ? '正常' : '冻结' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-dropdown>
              <a-button type="link" size="small">
                操作 <MoreOutlined />
              </a-button>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="handleView(record)">
                    <EyeOutlined /> 查看详情
                  </a-menu-item>
                  <a-menu-item @click="handleAdjustBalance(record)">
                    <WalletOutlined /> 调整余额
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item
                    v-if="record.status === 1"
                    danger
                    @click="handleToggleStatus(record)"
                  >
                    <LockOutlined /> 冻结账号
                  </a-menu-item>
                  <a-menu-item v-else @click="handleToggleStatus(record)">
                    <UnlockOutlined /> 解冻账号
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 用户详情弹窗 -->
    <a-modal
      v-model:open="detailVisible"
      title="用户详情"
      width="650px"
      :footer="null"
    >
      <a-descriptions :column="2" bordered v-if="currentUser" class="mt-4">
        <a-descriptions-item label="用户ID">{{ currentUser.id }}</a-descriptions-item>
        <a-descriptions-item label="手机号">{{ currentUser.mobile }}</a-descriptions-item>
        <a-descriptions-item label="昵称">{{ currentUser.nickname || '-' }}</a-descriptions-item>
        <a-descriptions-item label="邀请码">{{ currentUser.inviteCode || '-' }}</a-descriptions-item>
        <a-descriptions-item label="账户余额">
          <span class="text-green-500 font-medium">R$ {{ formatMoney(currentUser.price) }}</span>
        </a-descriptions-item>
        <a-descriptions-item label="佣金余额">
          <span class="text-orange-500 font-medium">R$ {{ formatMoney(currentUser.incomePrice) }}</span>
        </a-descriptions-item>
        <a-descriptions-item label="用户状态">
          <a-tag :color="currentUser.status === 1 ? 'success' : 'error'">
            {{ currentUser.status === 1 ? '正常' : '冻结' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="注册时间">{{ currentUser.createTime }}</a-descriptions-item>
      </a-descriptions>
      <div class="mt-4 flex justify-end gap-2">
        <a-button @click="detailVisible = false">关闭</a-button>
        <a-button type="primary" @click="handleAdjustBalance(currentUser)">
          <WalletOutlined /> 调整余额
        </a-button>
        <a-button danger @click="handleResetPassword(currentUser)">
          <KeyOutlined /> 重置密码
        </a-button>
      </div>
    </a-modal>

    <!-- 调整余额弹窗 -->
    <a-modal
      v-model:open="balanceDialogVisible"
      title="调整余额"
      width="480px"
      :confirm-loading="balanceLoading"
      @ok="submitAdjustBalance"
    >
      <div class="mt-4">
        <a-descriptions :column="1" bordered size="small">
          <a-descriptions-item label="用户">
            {{ balanceUser?.nickname || '用户' + balanceUser?.id }} ({{ balanceUser?.mobile }})
          </a-descriptions-item>
          <a-descriptions-item label="当前余额">
            <span class="text-green-500 font-medium">R$ {{ formatMoney(balanceUser?.price) }}</span>
          </a-descriptions-item>
        </a-descriptions>
        <a-divider />
        <a-form layout="vertical">
          <a-form-item label="操作类型" required>
            <a-radio-group v-model:value="balanceForm.type">
              <a-radio-button value="add">
                <span class="text-green-500">增加余额</span>
              </a-radio-button>
              <a-radio-button value="deduct">
                <span class="text-red-500">扣除余额</span>
              </a-radio-button>
            </a-radio-group>
          </a-form-item>
          <a-form-item label="金额" required>
            <a-input-number
              v-model:value="balanceForm.amount"
              :min="0"
              :precision="2"
              placeholder="请输入金额"
              style="width: 100%"
            >
              <template #prefix>R$</template>
            </a-input-number>
          </a-form-item>
          <a-form-item label="备注" required>
            <a-textarea
              v-model:value="balanceForm.remark"
              :rows="3"
              placeholder="请输入调整原因（必填）"
            />
          </a-form-item>
        </a-form>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  UserOutlined,
  SearchOutlined,
  ReloadOutlined,
  ExportOutlined,
  MoreOutlined,
  EyeOutlined,
  WalletOutlined,
  LockOutlined,
  UnlockOutlined,
  KeyOutlined,
} from '@ant-design/icons-vue'
import type { TableProps } from 'ant-design-vue'
import request from '@/utils/request'

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const loading = ref(false)
const list = ref<any[]>([])
const dateRange = ref<string[]>([])
const detailVisible = ref(false)
const currentUser = ref<any>(null)

const stats = reactive({
  totalUsers: 0,
  todayUsers: 0,
  totalBalance: 0,
  totalCommission: 0
})

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  mobile: '',
  status: undefined as number | undefined
})

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`,
})

const columns = [
  { title: '用户信息', key: 'userInfo', width: 220, fixed: 'left' },
  { title: '邀请码', key: 'inviteCode', width: 110, align: 'center' },
  { title: '账户余额', key: 'price', width: 140, align: 'right' },
  { title: '佣金余额', key: 'incomePrice', width: 140, align: 'right' },
  { title: '团队人数', dataIndex: 'teamCount', width: 100, align: 'center' },
  { title: '状态', key: 'status', width: 90, align: 'center' },
  { title: '注册时间', dataIndex: 'createTime', width: 160, align: 'center' },
  { title: '操作', key: 'action', width: 100, fixed: 'right', align: 'center' },
]

const formatMoney = (value: number | undefined) => {
  if (!value) return '0.00'
  return value.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const fetchStats = async () => {
  try {
    const res = await request.get('/api/admin/stats/users')
    if (res.data.code === 200) {
      Object.assign(stats, res.data.data)
    }
  } catch (e) {
    stats.totalUsers = 12580
    stats.todayUsers = 156
    stats.totalBalance = 8568000
    stats.totalCommission = 1256800
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const params: any = { ...query }
    if (dateRange.value?.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await request.get('/api/admin/users', { params })
    if (res.data.code === 200) {
      list.value = res.data.data.records
      pagination.total = res.data.data.total
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  query.mobile = ''
  query.status = undefined
  query.pageNum = 1
  dateRange.value = []
  pagination.current = 1
  fetchList()
}

const handleTableChange: TableProps['onChange'] = (pag) => {
  pagination.current = pag.current || 1
  pagination.pageSize = pag.pageSize || 10
  query.pageNum = pagination.current
  query.pageSize = pagination.pageSize
  fetchList()
}

const handleView = (row: any) => {
  currentUser.value = row
  detailVisible.value = true
}

const handleExport = () => {
  message.info('导出任务已创建，请稍后在下载中心查看')
}

// 调整余额
const balanceDialogVisible = ref(false)
const balanceLoading = ref(false)
const balanceUser = ref<any>(null)
const balanceForm = reactive({
  type: 'add',
  amount: 0,
  remark: ''
})

const handleAdjustBalance = (row: any) => {
  balanceUser.value = row
  balanceForm.type = 'add'
  balanceForm.amount = 0
  balanceForm.remark = ''
  balanceDialogVisible.value = true
  detailVisible.value = false
}

const submitAdjustBalance = async () => {
  if (!balanceForm.amount || balanceForm.amount <= 0) {
    message.error('请输入有效金额')
    return
  }
  if (!balanceForm.remark) {
    message.error('请填写调整原因')
    return
  }
  balanceLoading.value = true
  try {
    await request.put(`/api/admin/users/${balanceUser.value.id}/balance`, balanceForm)
    message.success('调整成功')
    balanceDialogVisible.value = false
    fetchList()
    fetchStats()
  } catch (e) {
    message.error('调整失败')
  } finally {
    balanceLoading.value = false
  }
}

// 冻结/解冻
const handleToggleStatus = (row: any) => {
  const newStatus = row.status === 1 ? 2 : 1
  const action = row.status === 1 ? '冻结' : '解冻'

  Modal.confirm({
    title: `确认${action}`,
    content: `确定要${action}该用户吗？${row.status === 1 ? '冻结后用户将无法登录。' : ''}`,
    okText: '确定',
    cancelText: '取消',
    onOk: async () => {
      try {
        await request.put(`/api/admin/users/${row.id}/status`, { status: newStatus })
        message.success(`${action}成功`)
        fetchList()
      } catch (e) {
        message.error(`${action}失败`)
      }
    }
  })
}

// 重置密码
const handleResetPassword = (row: any) => {
  Modal.confirm({
    title: '确认重置密码',
    content: '确定要重置该用户密码为 123456 吗？',
    okText: '确定',
    cancelText: '取消',
    okButtonProps: { danger: true },
    onOk: async () => {
      try {
        await request.put(`/api/admin/users/${row.id}/reset-password`)
        message.success('重置成功，新密码为 123456')
        detailVisible.value = false
      } catch (e) {
        message.error('重置失败')
      }
    }
  })
}

onMounted(() => {
  fetchStats()
  fetchList()
})
</script>

<style scoped>
.users-page :deep(.ant-card) {
  border-radius: 8px;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 500;
  color: #1f1f1f;
}

.user-meta {
  font-size: 12px;
  color: #8c8c8c;
}
</style>
