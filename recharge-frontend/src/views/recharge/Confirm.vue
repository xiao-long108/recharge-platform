<template>
  <div class="page-container !pb-28">
    <!-- 页面头部 -->
    <div class="flex items-center bg-primary text-white p-4">
      <LeftOutlined class="text-xl cursor-pointer" @click="router.back()" />
      <span class="flex-1 text-center text-lg font-bold">确认订单</span>
      <span class="w-5"></span>
    </div>

    <!-- 订单信息 -->
    <div class="card-box m-3" v-if="order">
      <div class="list-item">
        <span class="text-muted-foreground">订单编号</span>
        <span class="text-foreground">{{ order.orderNo }}</span>
      </div>
      <div class="list-item">
        <span class="text-muted-foreground">充值号码</span>
        <span class="text-foreground">{{ order.phoneNumber }}</span>
      </div>
      <div class="list-item">
        <span class="text-muted-foreground">充值面值</span>
        <span class="text-foreground">{{ order.faceValue }}元</span>
      </div>
      <div class="list-item">
        <span class="text-muted-foreground">应付金额</span>
        <span class="text-destructive text-lg font-bold">¥{{ order.payAmount.toFixed(2) }}</span>
      </div>
    </div>

    <!-- 支付方式 -->
    <div class="card-box m-3">
      <div class="text-base font-bold text-foreground mb-3">支付方式</div>
      <a-radio-group v-model:value="payType" class="w-full">
        <a-radio :value="1" class="w-full !flex items-center py-2">
          <div class="flex items-center">
            <WalletOutlined class="text-2xl text-primary mr-2" />
            <span>余额支付</span>
            <span class="text-muted-foreground text-xs ml-2">（余额: ¥{{ userBalance.toFixed(2) }}）</span>
          </div>
        </a-radio>
      </a-radio-group>
    </div>

    <!-- 底部支付栏 -->
    <div class="fixed bottom-0 left-0 right-0 bg-card p-3 flex items-center shadow-[0_-2px_10px_rgba(0,0,0,0.05)]">
      <div class="flex-1">
        <span class="text-foreground">需支付：</span>
        <span class="text-destructive text-xl font-bold">¥{{ order?.payAmount?.toFixed(2) || '0.00' }}</span>
      </div>
      <a-button
        type="primary"
        size="large"
        class="w-28 h-11"
        :loading="paying"
        @click="handlePay"
      >
        确认支付
      </a-button>
    </div>

    <!-- 支付密码弹窗 -->
    <a-modal
      v-model:open="showPasswordDialog"
      title="输入支付密码"
      :confirm-loading="paying"
      @ok="confirmPay"
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
import { LeftOutlined, WalletOutlined } from '@ant-design/icons-vue'
import { getOrderDetail, payOrder } from '@/api/order'
import { useUserStore } from '@/stores/user'
import type { Order } from '@/types'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const order = ref<Order | null>(null)
const payType = ref(1)
const paying = ref(false)
const showPasswordDialog = ref(false)
const payPassword = ref('')

const userBalance = computed(() => userStore.userInfo?.balance || 0)

onMounted(async () => {
  const orderId = Number(route.query.orderId)
  if (orderId) {
    await loadOrder(orderId)
  }
})

async function loadOrder(orderId: number) {
  try {
    order.value = await getOrderDetail(orderId)
  } catch {
    router.back()
  }
}

function handlePay() {
  if (!order.value) return

  if (payType.value === 1 && userBalance.value < order.value.payAmount) {
    message.error('余额不足')
    return
  }

  // 检查是否设置了支付密码
  if (!userStore.userInfo?.hasPayPassword) {
    message.error('请先设置支付密码')
    router.push('/profile/settings')
    return
  }

  showPasswordDialog.value = true
}

async function confirmPay() {
  if (!order.value) return

  if (!payPassword.value || payPassword.value.length !== 6) {
    message.error('请输入6位支付密码')
    return
  }

  paying.value = true
  try {
    await payOrder({
      orderId: order.value.id,
      payType: payType.value,
      payPassword: payPassword.value
    })

    message.success('支付成功')
    showPasswordDialog.value = false
    router.push(`/order/${order.value.id}`)
  } catch {
    // 错误已处理
  } finally {
    paying.value = false
  }
}
</script>
