<template>
  <div class="page-container !pb-24">
    <!-- 页面头部 -->
    <div class="flex items-center bg-primary text-white p-4">
      <LeftOutlined class="text-xl cursor-pointer" @click="router.back()" />
      <span class="flex-1 text-center text-lg font-bold">银行卡管理</span>
      <span class="w-5"></span>
    </div>

    <!-- 银行卡列表 -->
    <div class="p-3">
      <div
        v-for="account in accounts"
        :key="account.id"
        class="user-card-gradient rounded-xl p-5 mb-3 flex items-center cursor-pointer"
        @click="showActionSheet(account)"
      >
        <div class="w-12 h-12 bg-white/20 rounded-full flex-center mr-4">
          <CreditCardOutlined class="text-2xl" />
        </div>
        <div class="flex-1">
          <div class="text-base font-bold flex items-center gap-2">
            {{ account.bankName }}
            <a-tag v-if="account.isDefault" color="success" class="!m-0">默认</a-tag>
          </div>
          <div class="text-lg mt-2 tracking-wider">{{ maskAccountNumber(account.accountNumber) }}</div>
          <div class="text-xs opacity-90 mt-1">{{ account.accountHolder }}</div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="accounts.length === 0" class="empty-state">
        <CreditCardOutlined class="text-5xl mb-4" />
        <p>暂无银行卡</p>
      </div>
    </div>

    <!-- 添加按钮 -->
    <div class="fixed bottom-0 left-0 right-0 p-4 bg-card shadow-[0_-2px_10px_rgba(0,0,0,0.05)]">
      <a-button type="primary" size="large" class="w-full h-12" @click="router.push('/bank-account/add')">
        <PlusOutlined />
        添加银行卡
      </a-button>
    </div>

    <!-- 操作弹窗 -->
    <a-modal
      v-model:open="showAction"
      title="选择操作"
      :footer="null"
      centered
    >
      <div class="py-2">
        <div
          class="py-3 text-center cursor-pointer hover:bg-muted rounded"
          @click="handleSetDefault"
        >
          设为默认
        </div>
        <div
          class="py-3 text-center cursor-pointer text-destructive hover:bg-muted rounded"
          @click="handleDelete"
        >
          删除
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import { LeftOutlined, CreditCardOutlined, PlusOutlined } from '@ant-design/icons-vue'
import { getBankAccounts, deleteBankAccount, setDefaultBankAccount } from '@/api/user'
import type { BankAccount } from '@/types'

const router = useRouter()

const accounts = ref<BankAccount[]>([])
const showAction = ref(false)
const currentAccount = ref<BankAccount | null>(null)

onMounted(async () => {
  await loadAccounts()
})

async function loadAccounts() {
  try {
    accounts.value = await getBankAccounts()
  } catch {
    // 错误已处理
  }
}

function maskAccountNumber(number: string): string {
  if (!number || number.length < 8) return number
  return '**** **** **** ' + number.slice(-4)
}

function showActionSheet(account: BankAccount) {
  currentAccount.value = account
  showAction.value = true
}

async function handleSetDefault() {
  if (!currentAccount.value) return

  try {
    await setDefaultBankAccount(currentAccount.value.id)
    message.success('设置成功')
    showAction.value = false
    await loadAccounts()
  } catch {
    // 错误已处理
  }
}

async function handleDelete() {
  if (!currentAccount.value) return

  Modal.confirm({
    title: '提示',
    content: '确定要删除该银行卡吗？',
    async onOk() {
      await deleteBankAccount(currentAccount.value!.id)
      message.success('删除成功')
      showAction.value = false
      await loadAccounts()
    }
  })
}
</script>
