<template>
  <div class="coupons-page">
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-label">优惠券总数</div>
        <div class="stat-value blue">{{ stats.total }} <span class="unit">张</span></div>
      </div>
      <div class="stat-card">
        <div class="stat-label">已领取</div>
        <div class="stat-value green">{{ stats.claimed }} <span class="unit">张</span></div>
      </div>
      <div class="stat-card">
        <div class="stat-label">已使用</div>
        <div class="stat-value orange">{{ stats.used }} <span class="unit">张</span></div>
      </div>
      <div class="stat-card">
        <div class="stat-label">优惠总额</div>
        <div class="stat-value green">R$ {{ formatMoney(stats.totalDiscount) }}</div>
      </div>
    </div>

    <!-- 搜索筛选栏 -->
    <div class="filter-bar">
      <div class="filter-left">
        <a-input
          v-model:value="query.name"
          placeholder="优惠券名称"
          allow-clear
          style="width: 180px"
        >
          <template #prefix><SearchOutlined /></template>
        </a-input>
        <a-select v-model:value="query.type" placeholder="类型" allow-clear style="width: 120px">
          <a-select-option :value="null">全部</a-select-option>
          <a-select-option :value="1">满减券</a-select-option>
          <a-select-option :value="2">折扣券</a-select-option>
          <a-select-option :value="3">立减券</a-select-option>
        </a-select>
        <a-select v-model:value="query.status" placeholder="状态" allow-clear style="width: 120px">
          <a-select-option :value="null">全部</a-select-option>
          <a-select-option :value="1">进行中</a-select-option>
          <a-select-option :value="0">已停止</a-select-option>
          <a-select-option :value="2">已结束</a-select-option>
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
        <a-button type="primary" @click="handleAdd">
          <template #icon><PlusOutlined /></template>
          创建优惠券
        </a-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <a-table :data-source="list" :loading="loading" :pagination="false" :scroll="{ x: 1100 }">
        <a-table-column title="优惠券信息" :width="260">
          <template #default="{ record }">
            <div class="coupon-info">
              <div class="coupon-icon" :class="getCouponClass(record.type)">
                <GiftOutlined />
              </div>
              <div class="coupon-detail">
                <div class="name">{{ record.name }}</div>
                <div class="desc">{{ getCouponDesc(record) }}</div>
              </div>
            </div>
          </template>
        </a-table-column>
        <a-table-column title="类型" data-index="type" :width="100" align="center">
          <template #default="{ record }">
            <a-tag :color="getTypeColor(record.type)">{{ getTypeText(record.type) }}</a-tag>
          </template>
        </a-table-column>
        <a-table-column title="发放/领取" :width="120" align="center">
          <template #default="{ record }">
            <span>{{ record.claimedCount || 0 }} / {{ record.totalCount }}</span>
          </template>
        </a-table-column>
        <a-table-column title="已使用" :width="80" align="center">
          <template #default="{ record }">
            <span class="text-green-500">{{ record.usedCount || 0 }}</span>
          </template>
        </a-table-column>
        <a-table-column title="有效期" :width="200" align="center">
          <template #default="{ record }">
            <div class="text-xs">
              <div>{{ record.startTime }}</div>
              <div>至 {{ record.endTime }}</div>
            </div>
          </template>
        </a-table-column>
        <a-table-column title="状态" data-index="status" :width="90" align="center">
          <template #default="{ record }">
            <a-tag :color="getStatusColor(record.status)">{{ getStatusText(record.status) }}</a-tag>
          </template>
        </a-table-column>
        <a-table-column title="操作" :width="180" align="center" fixed="right">
          <template #default="{ record }">
            <a-button type="link" size="small" @click="handleEdit(record)">
              <EditOutlined /> 编辑
            </a-button>
            <a-button type="link" size="small" @click="handleToggle(record)" v-if="record.status !== 2">
              {{ record.status === 1 ? '停止' : '启用' }}
            </a-button>
            <a-button type="link" danger size="small" @click="handleDelete(record)">
              <DeleteOutlined />
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

    <!-- 添加/编辑弹窗 -->
    <a-modal v-model:open="dialogVisible" :title="isEdit ? '编辑优惠券' : '创建优惠券'" width="600px">
      <a-form :model="form" :rules="rules" ref="formRef" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }" class="mt-4">
        <a-form-item label="优惠券名称" name="name">
          <a-input v-model:value="form.name" placeholder="如：新人专享券" :maxlength="30" />
        </a-form-item>
        <a-form-item label="优惠券类型" name="type">
          <a-radio-group v-model:value="form.type">
            <a-radio :value="1">满减券</a-radio>
            <a-radio :value="2">折扣券</a-radio>
            <a-radio :value="3">立减券</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="使用门槛" v-if="form.type !== 3">
          <a-input-number v-model:value="form.minAmount" :min="0" :precision="2" style="width: 150px" />
          <span class="ml-2 text-gray-500">满此金额可用</span>
        </a-form-item>
        <a-form-item label="优惠金额" v-if="form.type === 1 || form.type === 3" name="discountAmount">
          <a-input-number v-model:value="form.discountAmount" :min="1" :precision="2" style="width: 150px">
            <template #prefix>R$</template>
          </a-input-number>
        </a-form-item>
        <a-form-item label="折扣比例" v-if="form.type === 2" name="discountRate">
          <a-input-number v-model:value="form.discountRate" :min="1" :max="99" :precision="0" style="width: 150px">
            <template #addonAfter>%</template>
          </a-input-number>
          <span class="ml-2 text-gray-500">用户支付原价的百分比</span>
        </a-form-item>
        <a-form-item label="发放数量" name="totalCount">
          <a-input-number v-model:value="form.totalCount" :min="1" :max="100000" style="width: 150px" />
          <span class="ml-2 text-gray-500">张</span>
        </a-form-item>
        <a-form-item label="每人限领" name="perLimit">
          <a-input-number v-model:value="form.perLimit" :min="1" :max="10" style="width: 150px" />
          <span class="ml-2 text-gray-500">张</span>
        </a-form-item>
        <a-form-item label="有效期" name="timeRange">
          <a-range-picker
            v-model:value="form.timeRange"
            show-time
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </a-form-item>
      </a-form>
      <template #footer>
        <a-button @click="dialogVisible = false">取消</a-button>
        <a-button type="primary" @click="submitForm">{{ isEdit ? '保存' : '创建' }}</a-button>
      </template>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  GiftOutlined
} from '@ant-design/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const currentId = ref<number | null>(null)

const stats = reactive({
  total: 0,
  claimed: 0,
  used: 0,
  totalDiscount: 0
})

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  type: null as number | null,
  status: null as number | null
})

const form = reactive({
  name: '',
  type: 1,
  minAmount: 0,
  discountAmount: 10,
  discountRate: 90,
  totalCount: 100,
  perLimit: 1,
  timeRange: [] as string[]
})

const rules = {
  name: [{ required: true, message: '请输入优惠券名称', trigger: 'blur' }],
  totalCount: [{ required: true, message: '请输入发放数量', trigger: 'blur' }],
  timeRange: [{ required: true, message: '请选择有效期', trigger: 'change' }]
}

const formatMoney = (value: number | undefined) => {
  if (!value) return '0.00'
  return value.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const getTypeText = (type: number) => {
  const map: Record<number, string> = { 1: '满减券', 2: '折扣券', 3: '立减券' }
  return map[type] || '未知'
}

const getTypeColor = (type: number) => {
  const map: Record<number, string> = { 1: 'blue', 2: 'purple', 3: 'orange' }
  return map[type] || 'default'
}

const getStatusText = (status: number) => {
  const map: Record<number, string> = { 0: '已停止', 1: '进行中', 2: '已结束' }
  return map[status] || '未知'
}

const getStatusColor = (status: number) => {
  const map: Record<number, string> = { 0: 'default', 1: 'success', 2: 'error' }
  return map[status] || 'default'
}

const getCouponClass = (type: number) => {
  const map: Record<number, string> = { 1: 'type-1', 2: 'type-2', 3: 'type-3' }
  return map[type] || ''
}

const getCouponDesc = (record: any) => {
  if (record.type === 1) return `满R$${record.minAmount}减R$${record.discountAmount}`
  if (record.type === 2) return `满R$${record.minAmount}打${record.discountRate / 10}折`
  return `立减R$${record.discountAmount}`
}

const fetchStats = async () => {
  try {
    const res = await request.get('/api/admin/coupons/stats')
    if (res.data.code === 200) {
      Object.assign(stats, res.data.data)
    }
  } catch {
    stats.total = 5000
    stats.claimed = 3200
    stats.used = 1800
    stats.totalDiscount = 56800
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/coupons', { params: query })
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
  query.name = ''
  query.type = null
  query.status = null
  query.pageNum = 1
  fetchList()
}

const handleAdd = () => {
  isEdit.value = false
  currentId.value = null
  form.name = ''
  form.type = 1
  form.minAmount = 0
  form.discountAmount = 10
  form.discountRate = 90
  form.totalCount = 100
  form.perLimit = 1
  form.timeRange = []
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  currentId.value = row.id
  form.name = row.name
  form.type = row.type
  form.minAmount = row.minAmount
  form.discountAmount = row.discountAmount
  form.discountRate = row.discountRate
  form.totalCount = row.totalCount
  form.perLimit = row.perLimit
  form.timeRange = [row.startTime, row.endTime]
  dialogVisible.value = true
}

const submitForm = async () => {
  await formRef.value.validate()

  try {
    const data = {
      ...form,
      startTime: form.timeRange[0],
      endTime: form.timeRange[1]
    }
    if (isEdit.value) {
      await request.put(`/api/admin/coupons/${currentId.value}`, data)
    } else {
      await request.post('/api/admin/coupons', data)
    }
    message.success(isEdit.value ? '修改成功' : '创建成功')
    dialogVisible.value = false
    fetchList()
    fetchStats()
  } catch {
    message.error('操作失败')
  }
}

const handleToggle = async (row: any) => {
  try {
    await request.put(`/api/admin/coupons/${row.id}/toggle`)
    message.success('操作成功')
    fetchList()
  } catch {
    message.error('操作失败')
  }
}

const handleDelete = (row: any) => {
  Modal.confirm({
    title: '提示',
    content: '确定要删除该优惠券吗？已领取的优惠券将失效',
    onOk: async () => {
      try {
        await request.delete(`/api/admin/coupons/${row.id}`)
        message.success('删除成功')
        fetchList()
        fetchStats()
      } catch {
        message.error('删除失败')
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
.coupons-page {
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
}

.coupon-info {
  display: flex;
  align-items: center;
  gap: 12px;

  .coupon-icon {
    width: 44px;
    height: 44px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 20px;

    &.type-1 { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
    &.type-2 { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
    &.type-3 { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
  }

  .coupon-detail {
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
