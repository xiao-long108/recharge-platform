<template>
  <div class="products-page">
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-label">产品总数</div>
        <div class="stat-value blue">{{ stats.totalProducts }} <span class="unit">个</span></div>
      </div>
      <div class="stat-card">
        <div class="stat-label">已启用</div>
        <div class="stat-value green">{{ stats.enabledProducts }} <span class="unit">个</span></div>
      </div>
      <div class="stat-card">
        <div class="stat-label">已禁用</div>
        <div class="stat-value orange">{{ stats.disabledProducts }} <span class="unit">个</span></div>
      </div>
      <div class="stat-card">
        <div class="stat-label">今日订单</div>
        <div class="stat-value green">{{ stats.todayOrders }} <span class="unit">笔</span></div>
      </div>
    </div>

    <!-- 搜索筛选栏 -->
    <div class="filter-bar">
      <div class="filter-left">
        <a-input
          v-model:value="query.title"
          placeholder="产品名称"
          allow-clear
          style="width: 180px"
        >
          <template #prefix><SearchOutlined /></template>
        </a-input>
        <a-select v-model:value="query.disable" placeholder="产品状态" allow-clear style="width: 140px">
          <a-select-option :value="null">全部</a-select-option>
          <a-select-option :value="1">已启用</a-select-option>
          <a-select-option :value="0">已禁用</a-select-option>
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
          添加产品
        </a-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <a-table :data-source="list" :loading="loading" :pagination="false" :scroll="{ x: 1000 }">
        <a-table-column title="产品信息" data-index="title" :width="220">
          <template #default="{ record }">
            <div class="product-info">
              <div class="product-icon" :class="{ disabled: record.disable !== 1 }">
                <CreditCardOutlined />
              </div>
              <div class="product-detail">
                <div class="title">{{ record.title }}</div>
                <div class="product-id">ID: {{ record.id }}</div>
              </div>
            </div>
          </template>
        </a-table-column>
        <a-table-column title="面值" data-index="price" :width="120" align="center">
          <template #default="{ record }">
            <span class="price">R$ {{ record.price }}</span>
          </template>
        </a-table-column>
        <a-table-column title="实际到账" data-index="realPrice" :width="120" align="center">
          <template #default="{ record }">
            <span class="real-price">R$ {{ record.realPrice }}</span>
          </template>
        </a-table-column>
        <a-table-column title="折扣率" :width="100" align="center">
          <template #default="{ record }">
            <a-tag color="default">
              {{ ((record.realPrice / record.price) * 100).toFixed(1) }}%
            </a-tag>
          </template>
        </a-table-column>
        <a-table-column title="状态" data-index="disable" :width="100" align="center">
          <template #default="{ record }">
            <a-tag :color="record.disable === 1 ? 'success' : 'error'">
              {{ record.disable === 1 ? '启用' : '禁用' }}
            </a-tag>
          </template>
        </a-table-column>
        <a-table-column title="更新时间" data-index="addtime" :width="160" align="center" />
        <a-table-column title="操作" :width="180" align="center" fixed="right">
          <template #default="{ record }">
            <a-button type="link" size="small" @click="handleEdit(record)">
              <EditOutlined /> 编辑
            </a-button>
            <a-button type="link" size="small" @click="handleToggle(record)">
              <SwapOutlined /> {{ record.disable === 1 ? '禁用' : '启用' }}
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
        :page-size-options="['10', '20', '50', '100']"
        show-quick-jumper
        @change="fetchList"
        @show-size-change="fetchList"
      />
    </div>

    <!-- 添加/编辑弹窗 -->
    <a-modal v-model:open="dialogVisible" :title="isEdit ? '编辑产品' : '添加产品'" width="500px">
      <a-form :model="form" :rules="rules" ref="formRef" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="产品名称" name="title">
          <a-input v-model:value="form.title" placeholder="如：50元话费充值" />
        </a-form-item>
        <a-form-item label="面值" name="price">
          <a-input-number v-model:value="form.price" :min="1" :step="10" style="width: 200px" />
          <span class="form-hint">用户支付金额</span>
        </a-form-item>
        <a-form-item label="实际到账" name="realPrice">
          <a-input-number v-model:value="form.realPrice" :min="1" :step="10" style="width: 200px" />
          <span class="form-hint">用户实际获得金额</span>
        </a-form-item>
        <a-form-item label="状态">
          <a-radio-group v-model:value="form.disable" button-style="solid">
            <a-radio-button :value="1">启用</a-radio-button>
            <a-radio-button :value="0">禁用</a-radio-button>
          </a-radio-group>
        </a-form-item>
      </a-form>
      <template #footer>
        <a-button @click="dialogVisible = false">取消</a-button>
        <a-button type="primary" @click="submitForm">{{ isEdit ? '保存' : '添加' }}</a-button>
      </template>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { SearchOutlined, ReloadOutlined, PlusOutlined, EditOutlined, DeleteOutlined, SwapOutlined, CreditCardOutlined } from '@ant-design/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)

const stats = reactive({
  totalProducts: 0,
  enabledProducts: 0,
  disabledProducts: 0,
  todayOrders: 0
})

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  disable: null as number | null
})

const fetchStats = async () => {
  try {
    const res = await request.get('/api/admin/stats/products')
    if (res.data.code === 200) {
      Object.assign(stats, res.data.data)
    }
  } catch (e) {
    // 使用模拟数据
    stats.totalProducts = 12
    stats.enabledProducts = 10
    stats.disabledProducts = 2
    stats.todayOrders = 568
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/products', { params: query })
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
  query.title = ''
  query.disable = null
  query.pageNum = 1
  fetchList()
}

// 弹窗
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const currentId = ref<number | null>(null)

const form = reactive({
  title: '',
  price: 50,
  realPrice: 50,
  disable: 1
})

const rules = {
  title: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入面值', trigger: 'blur' }],
  realPrice: [{ required: true, message: '请输入实际到账', trigger: 'blur' }]
}

const handleAdd = () => {
  isEdit.value = false
  currentId.value = null
  form.title = ''
  form.price = 50
  form.realPrice = 50
  form.disable = 1
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  currentId.value = row.id
  form.title = row.title
  form.price = row.price
  form.realPrice = row.realPrice
  form.disable = row.disable
  dialogVisible.value = true
}

const submitForm = async () => {
  await formRef.value.validate()

  try {
    if (isEdit.value) {
      await request.put(`/api/admin/products/${currentId.value}`, form)
    } else {
      await request.post('/api/admin/products', form)
    }
    message.success(isEdit.value ? '修改成功' : '添加成功')
    dialogVisible.value = false
    fetchList()
    fetchStats()
  } catch (e) {
    message.error('操作失败')
  }
}

const handleToggle = async (row: any) => {
  const action = row.disable === 1 ? '禁用' : '启用'
  Modal.confirm({
    title: '提示',
    content: `确定要${action}该产品吗？`,
    onOk: async () => {
      try {
        await request.put(`/api/admin/products/${row.id}/toggle`)
        message.success(`${action}成功`)
        fetchList()
        fetchStats()
      } catch (e) {
        message.error(`${action}失败`)
      }
    }
  })
}

const handleDelete = async (row: any) => {
  Modal.confirm({
    title: '提示',
    content: '确定要删除该产品吗？删除后无法恢复',
    onOk: async () => {
      try {
        await request.delete(`/api/admin/products/${row.id}`)
        message.success('删除成功')
        fetchList()
        fetchStats()
      } catch (e) {
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
.products-page {
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

.product-info {
  display: flex;
  align-items: center;
  gap: 12px;

  .product-icon {
    width: 40px;
    height: 40px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 20px;

    &.disabled {
      background: #c0c4cc;
    }
  }

  .product-detail {
    .title {
      font-size: 14px;
      color: #303133;
      font-weight: 500;
    }

    .product-id {
      font-size: 12px;
      color: #909399;
    }
  }
}

.price {
  color: #303133;
  font-weight: 500;
}

.real-price {
  color: #10b981;
  font-weight: 600;
}

.form-hint {
  margin-left: 12px;
  font-size: 12px;
  color: #909399;
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
