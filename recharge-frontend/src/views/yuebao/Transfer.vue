<template>
  <div class="page-container !pb-24">
    <!-- 页面头部 -->
    <div class="flex items-center bg-primary text-white p-4">
      <LeftOutlined class="text-xl cursor-pointer" @click="router.back()" />
      <span class="flex-1 text-center text-lg font-bold">{{ isTransferIn ? '转入余额宝' : '转出余额宝' }}</span>
      <span class="w-5"></span>
    </div>

    <!-- 可用金额 -->
    <div class="card-box m-3">
      <div class="flex justify-between items-center">
        <span class="text-sm text-muted-foreground">{{ isTransferIn ? '可转入金额' : '可转出金额' }}</span>
        <span class="text-lg font-bold text-primary">¥{{ availableAmount.toFixed(2) }}</span>
      </div>
    </div>

    <!-- 金额输入 -->
    <div class="card-box m-3">
      <div class="text-sm text-foreground mb-2">转账金额</div>
      <div class="flex items-center border-b border-border pb-2">
        <span class="text-2xl text-foreground mr-2">¥</span>
        <a-input
          v-model:value="amount"
          type="number"
          placeholder="请输入金额"
          :bordered="false"
          class="!text-2xl flex-1"
        />
        <span class="text-primary text-sm cursor-pointer" @click="fillAll">全部</span>
      </div>
    </div>

    <!-- 提示 -->
    <div class="card-box m-3">
      <p v-if="isTransferIn" class="text-xs text-muted-foreground m-0">转入后次日开始计算收益，T+1到账</p>
      <p v-else class="text-xs text-muted-foreground m-0">转出后实时到账余额</p>
    </div>

    <!-- 提交按钮 -->
    <div class="fixed bottom-0 left-0 right-0 p-4 bg-card shadow-[0_-2px_10px_rgba(0,0,0,0.05)]">
      <a-button
        type="primary"
        size="large"
        class="w-full h-12"
        :disabled="!canSubmit"
        @click="handleSubmit"
      >
        确认{{ isTransferIn ? '转入' : '转出' }}
      </a-button>
    </div>

    <!-- 支付密码弹窗 -->
    <a-modal
      v-model:open="showPasswordDialog"
      title="输入支付密码"
      :confirm-loading="loading"
      @ok="confirmTransfer"
    >
      <a-input-password
        v-model:value="payPassword"
        placeholder="请输入6位支付密码"
        :maxlength="6"
        size="large"
        class="mt-4"
      />
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { LeftOutlined } from '@ant-design/icons-vue'
import { getYuebaoAccount, transferIn, transferOut } from '@/api/yuebao'
import { getUserInfo } from '@/api/user'
import { useUserStore } from '@/stores/user'
import type { YuebaoAccount, UserInfo } from '@/types'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isTransferIn = computed(() => route.query.type !== 'out')
const amount = ref('')
const yuebaoAccount = ref<YuebaoAccount | null>(null)
const userInfo = ref<UserInfo | null>(null)
const showPasswordDialog = ref(false)
const payPassword = ref('')
const loading = ref(false)

const availableAmount = computed(() =>
  isTransferIn.value ? (userInfo.value?.balance || 0) : (yuebaoAccount.value?.balance || 0)
)

const canSubmit = computed(() => {
  const amt = Number(amount.value) || 0
  return amt > 0 && amt <= availableAmount.value
})

onMounted(async () => {
  try {
    yuebaoAccount.value = await getYuebaoAccount()
    userInfo.value = await getUserInfo()
    userStore.setUserInfo(userInfo.value)
  } catch {}
})

function fillAll() {
  amount.value = String(availableAmount.value)
}

function handleSubmit() {
  const amt = Number(amount.value) || 0
  if (amt <= 0) return message.error('请输入金额')
  if (amt > availableAmount.value) return message.error('余额不足')
  showPasswordDialog.value = true
}

async function confirmTransfer() {
  if (!payPassword.value || payPassword.value.length !== 6) return message.error('请输入6位支付密码')

  loading.value = true
  try {
    const data = { amount: Number(amount.value), payPassword: payPassword.value }
    isTransferIn.value ? await transferIn(data) : await transferOut(data)
    message.success('操作成功')
    showPasswordDialog.value = false
    router.back()
  } catch {} finally { loading.value = false }
}
</script>
