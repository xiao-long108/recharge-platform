<template>
  <div class="banners-page">
    <!-- 搜索筛选栏 -->
    <div class="filter-bar">
      <div class="filter-left">
        <a-select v-model:value="query.position" placeholder="展示位置" allow-clear style="width: 150px">
          <a-select-option :value="null">全部位置</a-select-option>
          <a-select-option :value="1">首页轮播</a-select-option>
          <a-select-option :value="2">店铺轮播</a-select-option>
          <a-select-option :value="3">活动页</a-select-option>
        </a-select>
        <a-select v-model:value="query.status" placeholder="状态" allow-clear style="width: 120px">
          <a-select-option :value="null">全部</a-select-option>
          <a-select-option :value="1">启用</a-select-option>
          <a-select-option :value="0">禁用</a-select-option>
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
          添加轮播图
        </a-button>
      </div>
    </div>

    <!-- 轮播图列表 -->
    <div class="banner-grid">
      <div v-for="item in list" :key="item.id" class="banner-card">
        <div class="banner-image">
          <img :src="item.imageUrl" :alt="item.title" />
          <div class="banner-overlay">
            <a-button type="primary" size="small" @click="handleEdit(item)">
              <EditOutlined /> 编辑
            </a-button>
            <a-button size="small" @click="handleToggle(item)">
              {{ item.status === 1 ? '禁用' : '启用' }}
            </a-button>
            <a-button danger size="small" @click="handleDelete(item)">
              <DeleteOutlined />
            </a-button>
          </div>
        </div>
        <div class="banner-info">
          <div class="banner-title">{{ item.title }}</div>
          <div class="banner-meta">
            <a-tag :color="getPositionColor(item.position)">{{ getPositionText(item.position) }}</a-tag>
            <a-tag :color="item.status === 1 ? 'success' : 'default'">
              {{ item.status === 1 ? '启用' : '禁用' }}
            </a-tag>
            <span class="sort">排序: {{ item.sort }}</span>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="list.length === 0 && !loading" class="empty-state">
        <PictureOutlined class="empty-icon" />
        <p>暂无轮播图</p>
        <a-button type="primary" @click="handleAdd">添加轮播图</a-button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <a-spin size="large" />
    </div>

    <!-- 添加/编辑弹窗 -->
    <a-modal v-model:open="dialogVisible" :title="isEdit ? '编辑轮播图' : '添加轮播图'" width="550px">
      <a-form :model="form" :rules="rules" ref="formRef" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }" class="mt-4">
        <a-form-item label="标题" name="title">
          <a-input v-model:value="form.title" placeholder="轮播图标题" :maxlength="50" />
        </a-form-item>
        <a-form-item label="图片地址" name="imageUrl">
          <a-input v-model:value="form.imageUrl" placeholder="请输入图片URL" />
          <div class="mt-2 text-xs text-gray-400">建议尺寸: 750 x 300 像素</div>
        </a-form-item>
        <a-form-item label="预览" v-if="form.imageUrl">
          <img :src="form.imageUrl" class="preview-image" />
        </a-form-item>
        <a-form-item label="跳转链接">
          <a-input v-model:value="form.linkUrl" placeholder="点击后跳转的URL（选填）" />
        </a-form-item>
        <a-form-item label="展示位置" name="position">
          <a-select v-model:value="form.position" placeholder="请选择">
            <a-select-option :value="1">首页轮播</a-select-option>
            <a-select-option :value="2">店铺轮播</a-select-option>
            <a-select-option :value="3">活动页</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number v-model:value="form.sort" :min="0" :max="999" style="width: 120px" />
          <span class="ml-2 text-gray-400">数字越小越靠前</span>
        </a-form-item>
        <a-form-item label="状态">
          <a-switch v-model:checked="form.status" checked-children="启用" un-checked-children="禁用" />
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
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  PictureOutlined
} from '@ant-design/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const list = ref<any[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const currentId = ref<number | null>(null)

const query = reactive({
  position: null as number | null,
  status: null as number | null
})

const form = reactive({
  title: '',
  imageUrl: '',
  linkUrl: '',
  position: 1,
  sort: 0,
  status: true
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  imageUrl: [{ required: true, message: '请输入图片地址', trigger: 'blur' }],
  position: [{ required: true, message: '请选择展示位置', trigger: 'change' }]
}

const getPositionText = (position: number) => {
  const map: Record<number, string> = { 1: '首页轮播', 2: '店铺轮播', 3: '活动页' }
  return map[position] || '未知'
}

const getPositionColor = (position: number) => {
  const map: Record<number, string> = { 1: 'blue', 2: 'green', 3: 'orange' }
  return map[position] || 'default'
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/banners', { params: query })
    if (res.data.code === 200) {
      list.value = res.data.data
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  query.position = null
  query.status = null
  fetchList()
}

const handleAdd = () => {
  isEdit.value = false
  currentId.value = null
  form.title = ''
  form.imageUrl = ''
  form.linkUrl = ''
  form.position = 1
  form.sort = 0
  form.status = true
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  currentId.value = row.id
  form.title = row.title
  form.imageUrl = row.imageUrl
  form.linkUrl = row.linkUrl || ''
  form.position = row.position
  form.sort = row.sort
  form.status = row.status === 1
  dialogVisible.value = true
}

const submitForm = async () => {
  await formRef.value.validate()

  try {
    const data = { ...form, status: form.status ? 1 : 0 }
    if (isEdit.value) {
      await request.put(`/api/admin/banners/${currentId.value}`, data)
    } else {
      await request.post('/api/admin/banners', data)
    }
    message.success(isEdit.value ? '修改成功' : '添加成功')
    dialogVisible.value = false
    fetchList()
  } catch {
    message.error('操作失败')
  }
}

const handleToggle = async (row: any) => {
  try {
    await request.put(`/api/admin/banners/${row.id}/toggle`)
    message.success('操作成功')
    fetchList()
  } catch {
    message.error('操作失败')
  }
}

const handleDelete = (row: any) => {
  Modal.confirm({
    title: '提示',
    content: '确定要删除该轮播图吗？',
    onOk: async () => {
      try {
        await request.delete(`/api/admin/banners/${row.id}`)
        message.success('删除成功')
        fetchList()
      } catch {
        message.error('删除失败')
      }
    }
  })
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped lang="scss">
.banners-page {
  padding: 0;
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

.banner-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 20px;
}

.banner-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s, box-shadow 0.2s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);

    .banner-overlay {
      opacity: 1;
    }
  }
}

.banner-image {
  position: relative;
  height: 160px;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .banner-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    opacity: 0;
    transition: opacity 0.2s;
  }
}

.banner-info {
  padding: 16px;

  .banner-title {
    font-size: 15px;
    font-weight: 500;
    color: #303133;
    margin-bottom: 10px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .banner-meta {
    display: flex;
    align-items: center;
    gap: 8px;

    .sort {
      font-size: 12px;
      color: #909399;
      margin-left: auto;
    }
  }
}

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 20px;
  background: #fff;
  border-radius: 12px;

  .empty-icon {
    font-size: 64px;
    color: #c0c4cc;
  }

  p {
    color: #909399;
    margin: 16px 0;
  }
}

.loading-state {
  text-align: center;
  padding: 60px;
}

.preview-image {
  max-width: 100%;
  max-height: 150px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}
</style>
