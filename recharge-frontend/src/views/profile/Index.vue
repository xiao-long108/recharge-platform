<template>
  <div class="page-container">
    <!-- 用户卡片 -->
    <div class="user-card-gradient p-6">
      <div class="flex items-center">
        <a-avatar :size="60" :src="userInfo?.avatar">
          <template #icon>
            <UserOutlined />
          </template>
        </a-avatar>
        <div class="ml-4">
          <div class="text-xl font-bold">{{ userInfo?.nickname || '用户' }}</div>
          <div class="text-sm opacity-90 mt-1">{{ userInfo?.phone }}</div>
          <div class="text-xs opacity-90 mt-1 flex items-center">
            邀请码：{{ userInfo?.inviteCode }}
            <CopyOutlined class="ml-2 cursor-pointer" @click="copyInviteCode" />
          </div>
        </div>
      </div>
    </div>

    <!-- 资产信息 -->
    <div class="card-box m-3 flex p-5">
      <div class="flex-1 text-center cursor-pointer" @click="router.push('/withdraw')">
        <div class="text-xl font-bold text-foreground">¥{{ userInfo?.balance?.toFixed(2) || '0.00' }}</div>
        <div class="text-xs text-muted-foreground mt-1">余额</div>
      </div>
      <div class="w-px bg-border"></div>
      <div class="flex-1 text-center cursor-pointer" @click="router.push('/withdraw')">
        <div class="text-xl font-bold text-foreground">¥{{ userInfo?.commissionBalance?.toFixed(2) || '0.00' }}</div>
        <div class="text-xs text-muted-foreground mt-1">佣金</div>
      </div>
      <div class="w-px bg-border"></div>
      <div class="flex-1 text-center cursor-pointer" @click="router.push('/yuebao')">
        <div class="text-xl font-bold text-foreground">¥{{ yuebaoBalance.toFixed(2) }}</div>
        <div class="text-xs text-muted-foreground mt-1">余额宝</div>
      </div>
    </div>

    <!-- 功能列表 -->
    <div class="menu-card m-3">
      <div class="menu-item" @click="router.push('/order/list')">
        <OrderedListOutlined class="text-xl text-primary mr-3" />
        <span class="flex-1 text-sm text-foreground">我的订单</span>
        <RightOutlined class="text-sm text-muted-foreground" />
      </div>
      <div class="menu-item" @click="router.push('/withdraw/records')">
        <WalletOutlined class="text-xl text-primary mr-3" />
        <span class="flex-1 text-sm text-foreground">提现记录</span>
        <RightOutlined class="text-sm text-muted-foreground" />
      </div>
      <div class="menu-item" @click="router.push('/bank-account')">
        <CreditCardOutlined class="text-xl text-primary mr-3" />
        <span class="flex-1 text-sm text-foreground">银行卡管理</span>
        <RightOutlined class="text-sm text-muted-foreground" />
      </div>
      <div class="menu-item" @click="router.push('/yuebao')">
        <PayCircleOutlined class="text-xl text-primary mr-3" />
        <span class="flex-1 text-sm text-foreground">余额宝</span>
        <RightOutlined class="text-sm text-muted-foreground" />
      </div>
    </div>

    <div class="menu-card m-3">
      <div class="menu-item" @click="router.push('/profile/settings')">
        <SettingOutlined class="text-xl text-primary mr-3" />
        <span class="flex-1 text-sm text-foreground">账号设置</span>
        <RightOutlined class="text-sm text-muted-foreground" />
      </div>
      <div class="menu-item" @click="handleLogout">
        <LogoutOutlined class="text-xl text-primary mr-3" />
        <span class="flex-1 text-sm text-foreground">退出登录</span>
        <RightOutlined class="text-sm text-muted-foreground" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import {
  UserOutlined,
  CopyOutlined,
  OrderedListOutlined,
  WalletOutlined,
  CreditCardOutlined,
  PayCircleOutlined,
  SettingOutlined,
  LogoutOutlined,
  RightOutlined
} from '@ant-design/icons-vue'
import { getUserInfo } from '@/api/user'
import { getYuebaoAccount } from '@/api/yuebao'
import { logout } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import type { UserInfo } from '@/types'

const router = useRouter()
const userStore = useUserStore()

const userInfo = ref<UserInfo | null>(null)
const yuebaoBalance = ref(0)

onMounted(async () => {
  await loadUserInfo()
  await loadYuebaoAccount()
})

async function loadUserInfo() {
  try {
    userInfo.value = await getUserInfo()
    userStore.setUserInfo(userInfo.value)
  } catch {
    // 错误已处理
  }
}

async function loadYuebaoAccount() {
  try {
    const account = await getYuebaoAccount()
    yuebaoBalance.value = account.balance
  } catch {
    // 错误已处理
  }
}

function copyInviteCode() {
  if (userInfo.value?.inviteCode) {
    navigator.clipboard.writeText(userInfo.value.inviteCode)
    message.success('邀请码已复制')
  }
}

async function handleLogout() {
  Modal.confirm({
    title: '提示',
    content: '确定要退出登录吗？',
    async onOk() {
      await logout()
      userStore.logout()
      router.push('/login')
    }
  })
}
</script>
