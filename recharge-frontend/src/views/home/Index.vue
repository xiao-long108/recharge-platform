<template>
  <div class="page-container !pb-20">
    <!-- 轮播图 -->
    <div class="banner-wrapper" v-if="banners.length > 0">
      <a-carousel autoplay :dots="true">
        <div v-for="banner in banners" :key="banner.id" class="banner-slide" @click="handleBannerClick(banner)">
          <img :src="banner.imageUrl" :alt="banner.title" />
        </div>
      </a-carousel>
    </div>

    <!-- 公告滚动 -->
    <div class="notice-bar m-3" v-if="notices.length > 0" @click="showNoticeModal = true">
      <SoundOutlined class="text-primary mr-2" />
      <div class="notice-content">
        <span>{{ notices[currentNoticeIndex]?.title }}</span>
      </div>
      <RightOutlined class="text-muted-foreground" />
    </div>

    <!-- 用户信息卡片 -->
    <div class="user-card-gradient mx-3 p-5 cursor-pointer rounded-xl" @click="router.push('/profile')">
      <div class="flex items-center">
        <a-avatar :size="50" :src="userInfo?.avatar">
          <template #icon>
            <UserOutlined />
          </template>
        </a-avatar>
        <div class="ml-3">
          <div class="text-lg font-bold">{{ userInfo?.nickname || '用户' }}</div>
          <div class="text-sm opacity-90 mt-1">{{ userInfo?.phone }}</div>
        </div>
      </div>
      <div class="flex mt-5">
        <div class="flex-1 text-center">
          <div class="text-sm opacity-90">余额</div>
          <div class="text-xl font-bold mt-1">¥{{ userInfo?.balance?.toFixed(2) || '0.00' }}</div>
        </div>
        <div class="flex-1 text-center">
          <div class="text-sm opacity-90">佣金</div>
          <div class="text-xl font-bold mt-1">¥{{ userInfo?.commissionBalance?.toFixed(2) || '0.00' }}</div>
        </div>
      </div>
    </div>

    <!-- 功能入口 -->
    <div class="card-box m-3 !p-5">
      <div class="grid grid-cols-5 gap-2">
        <div class="flex-col-center cursor-pointer" @click="router.push('/recharge')">
          <MobileOutlined class="text-2xl text-primary mb-2" />
          <span class="text-xs text-muted-foreground">话费充值</span>
        </div>
        <div class="flex-col-center cursor-pointer" @click="router.push('/order/list')">
          <OrderedListOutlined class="text-2xl text-success mb-2" />
          <span class="text-xs text-muted-foreground">我的订单</span>
        </div>
        <div class="flex-col-center cursor-pointer" @click="router.push('/withdraw')">
          <WalletOutlined class="text-2xl text-warning mb-2" />
          <span class="text-xs text-muted-foreground">余额提现</span>
        </div>
        <div class="flex-col-center cursor-pointer" @click="router.push('/sign')">
          <GiftOutlined class="text-2xl text-destructive mb-2" />
          <span class="text-xs text-muted-foreground">每日签到</span>
        </div>
        <div class="flex-col-center cursor-pointer" @click="router.push('/coupon')">
          <TagOutlined class="text-2xl text-purple-500 mb-2" />
          <span class="text-xs text-muted-foreground">优惠券</span>
        </div>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="card-box m-3">
      <div class="text-base font-bold text-foreground mb-4">热门充值</div>
      <div class="grid-4">
        <div
          v-for="product in products"
          :key="product.id"
          class="product-item"
          @click="handleSelectProduct(product)"
        >
          <div class="text-base font-bold text-foreground">{{ product.faceValue }}元</div>
          <div class="text-sm text-destructive mt-1">¥{{ product.salePrice.toFixed(2) }}</div>
          <div class="text-xs text-muted-foreground mt-1">{{ (product.discount * 10).toFixed(1) }}折</div>
        </div>
      </div>
    </div>

    <!-- 公告弹窗 -->
    <a-modal v-model:open="showNoticeModal" title="系统公告" :footer="null" width="90%">
      <div class="notice-list">
        <div
          v-for="notice in notices"
          :key="notice.id"
          class="notice-item"
          @click="showNoticeDetail(notice)"
        >
          <div class="notice-title">
            <a-tag v-if="notice.isTop === 1" color="red" size="small">置顶</a-tag>
            {{ notice.title }}
          </div>
          <div class="notice-time">{{ formatDate(notice.createdTime) }}</div>
        </div>
      </div>
    </a-modal>

    <!-- 公告详情弹窗 -->
    <a-modal v-model:open="showDetailModal" :title="currentNotice?.title" :footer="null" width="90%">
      <div class="notice-detail" v-if="currentNotice">
        <div class="detail-time">{{ formatDate(currentNotice.createdTime) }}</div>
        <div class="detail-content">{{ currentNotice.content }}</div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  UserOutlined,
  MobileOutlined,
  OrderedListOutlined,
  WalletOutlined,
  GiftOutlined,
  TagOutlined,
  SoundOutlined,
  RightOutlined
} from '@ant-design/icons-vue'
import { getUserInfo } from '@/api/user'
import { getProducts } from '@/api/product'
import { getLatestNotices } from '@/api/notice'
import { getHomeBanners } from '@/api/banner'
import { useUserStore } from '@/stores/user'
import type { UserInfo, Product, Notice, Banner } from '@/types'

const router = useRouter()
const userStore = useUserStore()

const userInfo = ref<UserInfo | null>(null)
const products = ref<Product[]>([])
const notices = ref<Notice[]>([])
const banners = ref<Banner[]>([])
const currentNoticeIndex = ref(0)
const showNoticeModal = ref(false)
const showDetailModal = ref(false)
const currentNotice = ref<Notice | null>(null)

let noticeTimer: ReturnType<typeof setInterval> | null = null

onMounted(async () => {
  await Promise.all([
    loadUserInfo(),
    loadProducts(),
    loadNotices(),
    loadBanners()
  ])

  // 公告轮播
  if (notices.value.length > 1) {
    noticeTimer = setInterval(() => {
      currentNoticeIndex.value = (currentNoticeIndex.value + 1) % notices.value.length
    }, 3000)
  }
})

onUnmounted(() => {
  if (noticeTimer) {
    clearInterval(noticeTimer)
  }
})

async function loadUserInfo() {
  try {
    const data = await getUserInfo()
    userInfo.value = data
    userStore.setUserInfo(data)
  } catch {
    // 错误已处理
  }
}

async function loadProducts() {
  try {
    products.value = await getProducts()
  } catch {
    // 错误已处理
  }
}

async function loadNotices() {
  try {
    notices.value = await getLatestNotices(5)
  } catch {
    // 错误已处理
  }
}

async function loadBanners() {
  try {
    banners.value = await getHomeBanners()
  } catch {
    // 错误已处理
  }
}

function handleSelectProduct(product: Product) {
  router.push({
    path: '/recharge',
    query: { productId: product.id }
  })
}

function handleBannerClick(banner: Banner) {
  if (banner.linkUrl) {
    window.location.href = banner.linkUrl
  }
}

function showNoticeDetail(notice: Notice) {
  currentNotice.value = notice
  showNoticeModal.value = false
  showDetailModal.value = true
}

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  return dateStr.slice(0, 10)
}
</script>

<style scoped>
.banner-wrapper {
  margin-bottom: -20px;
}

.banner-slide img {
  width: 100%;
  height: 160px;
  object-fit: cover;
}

.notice-bar {
  display: flex;
  align-items: center;
  background: white;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
}

.notice-content {
  flex: 1;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  font-size: 13px;
  color: #666;
}

.notice-list {
  max-height: 400px;
  overflow-y: auto;
}

.notice-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-title {
  font-size: 14px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 6px;
}

.notice-time {
  font-size: 12px;
  color: #999;
  margin-top: 6px;
}

.notice-detail {
  padding: 10px 0;
}

.detail-time {
  font-size: 12px;
  color: #999;
  margin-bottom: 16px;
}

.detail-content {
  font-size: 14px;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
}
</style>
