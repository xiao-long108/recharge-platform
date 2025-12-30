<template>
  <div class="notices-page">
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-label">公告总数</div>
        <div class="stat-value blue">{{ stats.total }} <span class="unit">条</span></div>
      </div>
      <div class="stat-card">
        <div class="stat-label">已发布</div>
        <div class="stat-value green">{{ stats.published }} <span class="unit">条</span></div>
      </div>
      <div class="stat-card">
        <div class="stat-label">草稿</div>
        <div class="stat-value orange">{{ stats.draft }} <span class="unit">条</span></div>
      </div>
      <div class="stat-card">
        <div class="stat-label">本月新增</div>
        <div class="stat-value green">{{ stats.monthCount }} <span class="unit">条</span></div>
      </div>
    </div>

    <!-- 搜索筛选栏 -->
    <div class="filter-bar">
      <div class="filter-left">
        <a-input
          v-model:value="query.title"
          placeholder="公告标题"
          allow-clear
          style="width: 200px"
        >
          <template #prefix><SearchOutlined /></template>
        </a-input>
        <a-select v-model:value="query.status" placeholder="状态" allow-clear style="width: 120px">
          <a-select-option :value="null">全部</a-select-option>
          <a-select-option :value="1">已发布</a-select-option>
          <a-select-option :value="0">草稿</a-select-option>
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
          发布公告
        </a-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <a-table :data-source="list" :loading="loading" :pagination="false" :scroll="{ x: 900 }">
        <a-table-column title="公告标题" data-index="title" :width="300">
          <template #default="{ record }">
            <div class="notice-title">
              <a-tag v-if="record.isTop === 1" color="red" size="small">置顶</a-tag>
              {{ record.title }}
            </div>
          </template>
        </a-table-column>
        <a-table-column title="类型" data-index="type" :width="100" align="center">
          <template #default="{ record }">
            <a-tag :color="getTypeColor(record.type)">{{ getTypeText(record.type) }}</a-tag>
          </template>
        </a-table-column>
        <a-table-column title="状态" data-index="status" :width="100" align="center">
          <template #default="{ record }">
            <a-tag :color="record.status === 1 ? 'success' : 'default'">
              {{ record.status === 1 ? '已发布' : '草稿' }}
            </a-tag>
          </template>
        </a-table-column>
        <a-table-column title="发布时间" data-index="createTime" :width="160" align="center" />
        <a-table-column title="操作" :width="180" align="center" fixed="right">
          <template #default="{ record }">
            <a-button type="link" size="small" @click="handleEdit(record)">
              <EditOutlined /> 编辑
            </a-button>
            <a-button type="link" size="small" @click="handleToggleTop(record)">
              <VerticalAlignTopOutlined /> {{ record.isTop === 1 ? '取消置顶' : '置顶' }}
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
    <a-modal v-model:open="dialogVisible" :title="isEdit ? '编辑公告' : '发布公告'" width="700px" :footer="null">
      <a-form :model="form" :rules="rules" ref="formRef" layout="vertical" class="mt-4">
        <a-form-item label="公告标题" name="title">
          <a-input v-model:value="form.title" placeholder="请输入公告标题" :maxlength="100" show-count />
        </a-form-item>
        <a-form-item label="公告类型" name="type">
          <a-radio-group v-model:value="form.type">
            <a-radio-button :value="1">系统公告</a-radio-button>
            <a-radio-button :value="2">活动通知</a-radio-button>
            <a-radio-button :value="3">更新日志</a-radio-button>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="公告内容" name="content">
          <a-textarea v-model:value="form.content" placeholder="请输入公告内容" :rows="6" :maxlength="2000" show-count />
        </a-form-item>
        <a-form-item label="选项">
          <a-space>
            <a-checkbox v-model:checked="form.isTop">置顶显示</a-checkbox>
          </a-space>
        </a-form-item>
        <a-form-item>
          <div class="flex justify-end gap-2">
            <a-button @click="dialogVisible = false">取消</a-button>
            <a-button @click="submitForm(0)">保存为草稿</a-button>
            <a-button type="primary" @click="submitForm(1)">立即发布</a-button>
          </div>
        </a-form-item>
      </a-form>
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
  VerticalAlignTopOutlined
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
  published: 0,
  draft: 0,
  monthCount: 0
})

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  status: null as number | null
})

const form = reactive({
  title: '',
  type: 1,
  content: '',
  isTop: false
})

const rules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
}

const getTypeText = (type: number) => {
  const map: Record<number, string> = { 1: '系统公告', 2: '活动通知', 3: '更新日志' }
  return map[type] || '其他'
}

const getTypeColor = (type: number) => {
  const map: Record<number, string> = { 1: 'blue', 2: 'orange', 3: 'green' }
  return map[type] || 'default'
}

const fetchStats = async () => {
  try {
    const res = await request.get('/api/admin/notices/stats')
    if (res.data.code === 200) {
      Object.assign(stats, res.data.data)
    }
  } catch {
    stats.total = 15
    stats.published = 12
    stats.draft = 3
    stats.monthCount = 5
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/notices', { params: query })
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
  query.status = null
  query.pageNum = 1
  fetchList()
}

const handleAdd = () => {
  isEdit.value = false
  currentId.value = null
  form.title = ''
  form.type = 1
  form.content = ''
  form.isTop = false
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  currentId.value = row.id
  form.title = row.title
  form.type = row.type
  form.content = row.content
  form.isTop = row.isTop === 1
  dialogVisible.value = true
}

const submitForm = async (status: number) => {
  await formRef.value.validate()

  try {
    const data = { ...form, status, isTop: form.isTop ? 1 : 0 }
    if (isEdit.value) {
      await request.put(`/api/admin/notices/${currentId.value}`, data)
    } else {
      await request.post('/api/admin/notices', data)
    }
    message.success(status === 1 ? '发布成功' : '保存成功')
    dialogVisible.value = false
    fetchList()
    fetchStats()
  } catch {
    message.error('操作失败')
  }
}

const handleToggleTop = async (row: any) => {
  try {
    await request.put(`/api/admin/notices/${row.id}/top`)
    message.success(row.isTop === 1 ? '已取消置顶' : '已置顶')
    fetchList()
  } catch {
    message.error('操作失败')
  }
}

const handleDelete = (row: any) => {
  Modal.confirm({
    title: '提示',
    content: '确定要删除该公告吗？',
    onOk: async () => {
      try {
        await request.delete(`/api/admin/notices/${row.id}`)
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
.notices-page {
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

.notice-title {
  display: flex;
  align-items: center;
  gap: 8px;
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
