<template>
  <div class="page-container !pb-24">
    <!-- 页面头部 -->
    <div class="flex items-center bg-primary text-white p-4">
      <LeftOutlined class="text-xl cursor-pointer" @click="router.back()" />
      <span class="flex-1 text-center text-lg font-bold">提现</span>
      <span class="w-5"></span>
    </div>

    <!-- 余额选择 -->
    <div class="user-card-gradient p-5">
      <div class="flex bg-white/10 rounded-lg p-1">
        <div
          class="flex-1 text-center py-4 rounded-md cursor-pointer"
          :class="withdrawType === 1 ? 'bg-card text-primary' : 'text-white/80'"
          @click="withdrawType = 1"
        >
          <div class="text-2xl font-bold" :class="withdrawType === 1 ? 'text-primary' : 'text-white'">
            ¥{{ userStore.userInfo?.balance?.toFixed(2) || '0.00' }}
          </div>
          <div class="text-xs mt-1">余额</div>
        </div>
        <div
          class="flex-1 text-center py-4 rounded-md cursor-pointer"
          :class="withdrawType === 2 ? 'bg-card text-primary' : 'text-white/80'"
          @click="withdrawType = 2"
        >
          <div class="text-2xl font-bold" :class="withdrawType === 2 ? 'text-primary' : 'text-white'">
            ¥{{ userStore.userInfo?.commissionBalance?.toFixed(2) || '0.00' }}
          </div>
          <div class="text-xs mt-1">佣金</div>
        </div>
      </div>
    </div>

    <!-- 表单 -->
    <div class="card-box m-3">
      <div class="mb-5">
        <div class="text-sm text-foreground mb-2">提现金额</div>
        <div class="flex items-center border-b border-border pb-2">
          <span class="text-2xl text-foreground mr-2">¥</span>
          <a-input
            v-model:value="amount"
            type="number"
            placeholder="请输入提现金额"
            :bordered="false"
            class="!text-2xl"
          />
        </div>
        <div class="text-xs text-muted-foreground mt-2">提现手续费：0.6%，最低100元，最高50000元</div>
      </div>

      <div class="mb-5">
        <div class="text-sm text-foreground mb-2">提现到</div>
        <div
          class="flex items-center justify-between bg-muted p-3 rounded-lg cursor-pointer"
          @click="showBankSelect = true"
        >
          <span v-if="selectedBank" class="text-sm">{{ selectedBank.bankName }} ({{ selectedBank.accountNumber.slice(-4) }})</span>
          <span v-else class="text-sm text-muted-foreground">请选择银行卡</span>
          <RightOutlined class="text-muted-foreground" />
        </div>
      </div>

      <div v-if="amount && Number(amount) > 0" class="bg-muted p-3 rounded-lg">
        <div class="flex justify-between text-sm py-1 text-muted-foreground">
          <span>手续费</span>
          <span>¥{{ fee.toFixed(2) }}</span>
        </div>
        <div class="flex justify-between text-sm py-1">
          <span class="text-muted-foreground">实际到账</span>
          <span class="text-destructive font-bold">¥{{ actualAmount.toFixed(2) }}</span>
        </div>
      </div>
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
        确认提现
      </a-button>
    </div>

    <!-- 银行卡选择 -->
    <a-drawer v-model:open="showBankSelect" title="选择银行卡" placement="bottom" height="50%">
      <div v-for="bank in bankAccounts" :key="bank.id"
        class="flex items-center p-4 border-b border-border cursor-pointer"
        :class="selectedBank?.id === bank.id ? 'bg-primary/5' : ''"
        @click="selectBank(bank)"
      >
        <CreditCardOutlined class="text-2xl text-primary" />
        <div class="flex-1 ml-3">
          <div class="text-sm text-foreground">{{ bank.bankName }}</div>
          <div class="text-xs text-muted-foreground mt-1">**** {{ bank.accountNumber.slice(-4) }}</div>
        </div>
        <CheckOutlined v-if="selectedBank?.id === bank.id" class="text-success" />
      </div>
      <div v-if="bankAccounts.length === 0" class="text-center py-10 text-muted-foreground">
        <p class="mb-4">暂无银行卡</p>
        <a-button type="primary" @click="router.push('/bank-account/add')">添加银行卡</a-button>
      </div>
    </a-drawer>

    <!-- 支付密码弹窗 -->
    <a-modal
      v-model:open="showPasswordDialog"
      title="输入支付密码"
      :confirm-loading="loading"
      @ok="confirmWithdraw"
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
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { LeftOutlined, RightOutlined, CreditCardOutlined, CheckOutlined } from '@ant-design/icons-vue'
import { getBankAccounts } from '@/api/user'
import { applyWithdraw } from '@/api/withdraw'
import { useUserStore } from '@/stores/user'
import type { BankAccount } from '@/types'

const router = useRouter()
const userStore = useUserStore()

const withdrawType = ref(1)
const amount = ref('')
const bankAccounts = ref<BankAccount[]>([])
const selectedBank = ref<BankAccount | null>(null)
const showBankSelect = ref(false)
const showPasswordDialog = ref(false)
const payPassword = ref('')
const loading = ref(false)

const fee = computed(() => (Number(amount.value) || 0) * 0.006)
const actualAmount = computed(() => (Number(amount.value) || 0) - fee.value)
const canSubmit = computed(() => {
  const amt = Number(amount.value) || 0
  return amt >= 100 && amt <= 50000 && selectedBank.value
})

onMounted(async () => {
  await loadBankAccounts()
})

async function loadBankAccounts() {
  try {
    bankAccounts.value = await getBankAccounts()
    const defaultBank = bankAccounts.value.find(b => b.isDefault === 1)
    selectedBank.value = defaultBank || bankAccounts.value[0] || null
  } catch {}
}

function selectBank(bank: BankAccount) {
  selectedBank.value = bank
  showBankSelect.value = false
}

function handleSubmit() {
  const amt = Number(amount.value) || 0
  if (amt < 100) return message.error('提现金额不能少于100元')
  if (amt > 50000) return message.error('提现金额不能超过50000元')

  const maxAmount = withdrawType.value === 1 ? userStore.userInfo?.balance || 0 : userStore.userInfo?.commissionBalance || 0
  if (amt > maxAmount) return message.error('余额不足')
  if (!selectedBank.value) return message.error('请选择银行卡')

  showPasswordDialog.value = true
}

async function confirmWithdraw() {
  if (!payPassword.value || payPassword.value.length !== 6) return message.error('请输入6位支付密码')
  if (!selectedBank.value) return

  loading.value = true
  try {
    await applyWithdraw({
      bankAccountId: selectedBank.value.id,
      amount: Number(amount.value),
      withdrawType: withdrawType.value,
      payPassword: payPassword.value
    })
    message.success('提现申请已提交')
    showPasswordDialog.value = false
    router.push('/withdraw/records')
  } catch {} finally { loading.value = false }
}
</script>
