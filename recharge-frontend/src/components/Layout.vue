<template>
  <div class="min-h-screen bg-background-deep">
    <router-view />
    <nav class="bottom-nav">
      <router-link
        v-for="item in navItems"
        :key="item.path"
        :to="item.path"
        class="nav-item"
        :class="{ active: isActive(item.path) }"
      >
        <component :is="item.icon" class="text-xl mb-1" />
        <span>{{ item.title }}</span>
      </router-link>
    </nav>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import {
  HomeOutlined,
  ShopOutlined,
  TeamOutlined,
  UserOutlined
} from '@ant-design/icons-vue'

const route = useRoute()

const navItems = [
  { path: '/home', title: '首页', icon: HomeOutlined },
  { path: '/store', title: '店铺', icon: ShopOutlined },
  { path: '/team', title: '团队', icon: TeamOutlined },
  { path: '/profile', title: '我的', icon: UserOutlined }
]

function isActive(path: string) {
  return route.path === path || route.path.startsWith(path + '/')
}
</script>
