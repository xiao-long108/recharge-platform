<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="flex items-center bg-primary text-white p-4">
      <LeftOutlined class="text-xl cursor-pointer" @click="router.back()" />
      <span class="flex-1 text-center text-lg font-bold">团队成员</span>
      <span class="w-5"></span>
    </div>

    <!-- Tabs -->
    <a-tabs v-model:activeKey="activeLevel" class="bg-card" @change="loadMembers">
      <a-tab-pane key="all" tab="全部" />
      <a-tab-pane key="1" tab="一级" />
      <a-tab-pane key="2" tab="二级" />
      <a-tab-pane key="3" tab="三级" />
    </a-tabs>

    <!-- 成员列表 -->
    <div class="p-3">
      <div v-for="member in members" :key="member.id" class="card-box mb-2 flex items-center">
        <a-avatar :size="40" :src="member.avatar">
          <template #icon>
            <UserOutlined />
          </template>
        </a-avatar>
        <div class="flex-1 ml-3">
          <div class="text-sm text-foreground">{{ member.nickname || '用户' }}</div>
          <div class="text-xs text-muted-foreground mt-1">{{ maskPhone(member.phone) }}</div>
        </div>
        <div class="text-xs text-muted-foreground">{{ formatTime(member.createdTime) }}</div>
      </div>

      <!-- 空状态 -->
      <div v-if="members.length === 0" class="empty-state">
        <UserOutlined class="text-5xl mb-4" />
        <p>暂无成员</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { LeftOutlined, UserOutlined } from '@ant-design/icons-vue'
import { getTeamMembers } from '@/api/team'
import dayjs from 'dayjs'
import type { TeamMember } from '@/types'

const router = useRouter()
const route = useRoute()

const activeLevel = ref('all')
const members = ref<TeamMember[]>([])

onMounted(() => {
  if (route.query.level) {
    activeLevel.value = String(route.query.level)
  }
  loadMembers()
})

async function loadMembers() {
  const level = activeLevel.value === 'all' ? undefined : Number(activeLevel.value)
  try {
    members.value = await getTeamMembers(level)
  } catch {
    // 错误已处理
  }
}

function maskPhone(phone: string): string {
  if (!phone || phone.length < 11) return phone
  return phone.slice(0, 3) + '****' + phone.slice(-4)
}

function formatTime(time: string): string {
  return dayjs(time).format('YYYY-MM-DD')
}
</script>
