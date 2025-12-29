<template>
  <div class="page-container !pb-20">
    <!-- 有店铺 -->
    <template v-if="store">
      <div class="user-card-gradient p-8 text-white flex justify-between items-start">
        <div>
          <div class="text-2xl font-bold">{{ store.storeName }}</div>
          <div class="text-sm opacity-90 mt-2">{{ store.storeDescription || '暂无描述' }}</div>
        </div>
        <a-button size="small" @click="showEditDialog = true">编辑</a-button>
      </div>

      <div class="card-box m-3 flex">
        <div class="flex-1 text-center">
          <div class="text-2xl font-bold text-primary">{{ store.totalOrders }}</div>
          <div class="text-xs text-muted-foreground mt-1">总订单</div>
        </div>
        <div class="flex-1 text-center">
          <div class="text-2xl font-bold text-primary">¥{{ store.totalSales?.toFixed(2) || '0.00' }}</div>
          <div class="text-xs text-muted-foreground mt-1">总销售额</div>
        </div>
      </div>

      <div class="card-box m-3">
        <div class="text-base font-bold text-foreground mb-4">店铺二维码</div>
        <div class="text-center">
          <div class="bg-muted p-5 rounded-lg mb-4">
            <LinkOutlined class="text-3xl text-primary" />
            <p class="text-xs text-muted-foreground mt-2 break-all">{{ shareLink }}</p>
          </div>
          <a-button type="primary" size="small" @click="copyLink">复制链接</a-button>
        </div>
      </div>
    </template>

    <!-- 无店铺 -->
    <template v-else>
      <div class="text-center py-24 px-5">
        <ShopOutlined class="text-6xl text-muted-foreground" />
        <p class="text-base text-foreground mt-4">您还没有开通店铺</p>
        <p class="text-sm text-muted-foreground mt-2">开通店铺后，可以推广充值服务赚取佣金</p>
        <a-button type="primary" size="large" class="mt-6 w-48 h-11" @click="showOpenDialog = true">立即开通</a-button>
      </div>
    </template>

    <!-- 开店弹窗 -->
    <a-modal v-model:open="showOpenDialog" title="开通店铺" :confirm-loading="loading" @ok="handleOpenStore">
      <a-form :model="openForm" layout="vertical" class="mt-4">
        <a-form-item label="店铺名称">
          <a-input v-model:value="openForm.storeName" placeholder="请输入店铺名称" />
        </a-form-item>
        <a-form-item label="店铺描述">
          <a-textarea v-model:value="openForm.storeDescription" placeholder="请输入店铺描述" :rows="3" />
        </a-form-item>
        <a-form-item label="支付密码">
          <a-input-password v-model:value="openForm.payPassword" :maxlength="6" placeholder="请输入支付密码" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 编辑弹窗 -->
    <a-modal v-model:open="showEditDialog" title="编辑店铺" :confirm-loading="loading" @ok="handleUpdateStore">
      <a-form :model="editForm" layout="vertical" class="mt-4">
        <a-form-item label="店铺名称">
          <a-input v-model:value="editForm.storeName" placeholder="请输入店铺名称" />
        </a-form-item>
        <a-form-item label="店铺描述">
          <a-textarea v-model:value="editForm.storeDescription" placeholder="请输入店铺描述" :rows="3" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { message } from 'ant-design-vue'
import { LinkOutlined, ShopOutlined } from '@ant-design/icons-vue'
import { getMyStore, openStore, updateStore } from '@/api/store'
import { useUserStore } from '@/stores/user'
import type { Store } from '@/types'

const userStore = useUserStore()

const store = ref<Store | null>(null)
const loading = ref(false)
const showOpenDialog = ref(false)
const showEditDialog = ref(false)

const openForm = reactive({
  storeName: '',
  storeDescription: '',
  payPassword: ''
})

const editForm = reactive({
  storeName: '',
  storeDescription: ''
})

const shareLink = computed(() => {
  if (!store.value || !userStore.userInfo) return ''
  return `${window.location.origin}/register?inviteCode=${userStore.userInfo.inviteCode}`
})

watch(showEditDialog, (val) => {
  if (val && store.value) {
    editForm.storeName = store.value.storeName
    editForm.storeDescription = store.value.storeDescription || ''
  }
})

onMounted(async () => {
  await loadStore()
})

async function loadStore() {
  try {
    store.value = await getMyStore()
  } catch {
    store.value = null
  }
}

async function handleOpenStore() {
  if (!openForm.storeName) return message.error('请输入店铺名称')
  if (!openForm.payPassword) return message.error('请输入支付密码')

  loading.value = true
  try {
    store.value = await openStore(openForm)
    message.success('开通成功')
    showOpenDialog.value = false
  } catch {} finally { loading.value = false }
}

async function handleUpdateStore() {
  if (!editForm.storeName) return message.error('请输入店铺名称')

  loading.value = true
  try {
    await updateStore(editForm)
    message.success('保存成功')
    showEditDialog.value = false
    await loadStore()
  } catch {} finally { loading.value = false }
}

function copyLink() {
  navigator.clipboard.writeText(shareLink.value)
  message.success('链接已复制')
}
</script>
