<template>
  <div class="page-container">
    <!-- 用户信息卡片 -->
    <div class="user-card-gradient p-5 cursor-pointer" @click="router.push('/profile')">
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
      <div class="grid-4">
        <div class="flex-col-center cursor-pointer" @click="router.push('/recharge')">
          <MobileOutlined class="text-3xl text-primary mb-2" />
          <span class="text-xs text-muted-foreground">话费充值</span>
        </div>
        <div class="flex-col-center cursor-pointer" @click="router.push('/order/list')">
          <OrderedListOutlined class="text-3xl text-success mb-2" />
          <span class="text-xs text-muted-foreground">我的订单</span>
        </div>
        <div class="flex-col-center cursor-pointer" @click="router.push('/withdraw')">
          <WalletOutlined class="text-3xl text-warning mb-2" />
          <span class="text-xs text-muted-foreground">余额提现</span>
        </div>
        <div class="flex-col-center cursor-pointer" @click="router.push('/yuebao')">
          <PayCircleOutlined class="text-3xl text-destructive mb-2" />
          <span class="text-xs text-muted-foreground">余额宝</span>
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  UserOutlined,
  MobileOutlined,
  OrderedListOutlined,
  WalletOutlined,
  PayCircleOutlined
} from '@ant-design/icons-vue'
import { getUserInfo } from '@/api/user'
import { getProducts } from '@/api/product'
import { useUserStore } from '@/stores/user'
import type { UserInfo, Product } from '@/types'

const router = useRouter()
const userStore = useUserStore()

const userInfo = ref<UserInfo | null>(null)
const products = ref<Product[]>([])

onMounted(async () => {
  await loadUserInfo()
  await loadProducts()
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

function handleSelectProduct(product: Product) {
  router.push({
    path: '/recharge',
    query: { productId: product.id }
  })
}
</script>
