<template>
  <div class="page-container">
    <!-- 头部 -->
    <div class="user-card-gradient p-10 text-center">
      <div class="text-4xl font-bold">¥{{ userStore.userInfo?.totalCommission?.toFixed(2) || '0.00' }}</div>
      <div class="text-sm opacity-90 mt-2">累计佣金</div>
    </div>

    <!-- 统计卡片 -->
    <div class="card-box m-3 -mt-8 relative z-10 flex p-4">
      <div class="flex-1 text-center cursor-pointer" @click="router.push({ path: '/team/members', query: { level: 1 } })">
        <div class="text-2xl font-bold text-primary">{{ stats.level1Count }}</div>
        <div class="text-xs text-muted-foreground mt-1">一级成员</div>
      </div>
      <div class="flex-1 text-center cursor-pointer" @click="router.push({ path: '/team/members', query: { level: 2 } })">
        <div class="text-2xl font-bold text-primary">{{ stats.level2Count }}</div>
        <div class="text-xs text-muted-foreground mt-1">二级成员</div>
      </div>
      <div class="flex-1 text-center cursor-pointer" @click="router.push({ path: '/team/members', query: { level: 3 } })">
        <div class="text-2xl font-bold text-primary">{{ stats.level3Count }}</div>
        <div class="text-xs text-muted-foreground mt-1">三级成员</div>
      </div>
    </div>

    <!-- 菜单 -->
    <div class="menu-card m-3">
      <div class="menu-item" @click="router.push('/team/members')">
        <UserOutlined class="text-xl text-primary mr-3" />
        <span class="flex-1 text-sm text-foreground">团队成员</span>
        <span class="text-sm text-muted-foreground mr-2">{{ stats.totalCount }}人</span>
        <RightOutlined class="text-sm text-muted-foreground" />
      </div>
      <div class="menu-item" @click="router.push('/team/commission')">
        <DollarOutlined class="text-xl text-primary mr-3" />
        <span class="flex-1 text-sm text-foreground">佣金明细</span>
        <RightOutlined class="text-sm text-muted-foreground" />
      </div>
    </div>

    <!-- 邀请卡片 -->
    <div class="card-box m-3">
      <div class="text-base font-bold text-foreground mb-4">邀请好友</div>
      <div class="flex justify-between items-center bg-muted p-3 rounded-lg mb-3">
        <span class="text-sm text-muted-foreground">邀请码：{{ userStore.userInfo?.inviteCode }}</span>
        <a-button size="small" type="primary" @click="copyInviteCode">复制</a-button>
      </div>
      <div class="bg-muted p-3 rounded-lg">
        <span class="text-xs text-muted-foreground">邀请链接：</span>
        <p class="text-xs text-foreground my-2 break-all">{{ inviteLink }}</p>
        <a-button size="small" type="primary" @click="copyInviteLink">复制链接</a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, DollarOutlined, RightOutlined } from '@ant-design/icons-vue'
import { getTeamStats } from '@/api/team'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const stats = ref({
  level1Count: 0,
  level2Count: 0,
  level3Count: 0,
  totalCount: 0
})

const inviteLink = computed(() => {
  return `${window.location.origin}/register?inviteCode=${userStore.userInfo?.inviteCode || ''}`
})

onMounted(async () => {
  await loadStats()
})

async function loadStats() {
  try {
    stats.value = await getTeamStats()
  } catch {
    // 错误已处理
  }
}

function copyInviteCode() {
  if (userStore.userInfo?.inviteCode) {
    navigator.clipboard.writeText(userStore.userInfo.inviteCode)
    message.success('邀请码已复制')
  }
}

function copyInviteLink() {
  navigator.clipboard.writeText(inviteLink.value)
  message.success('邀请链接已复制')
}
</script>
