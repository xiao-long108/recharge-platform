<template>
  <div class="page-container !pb-24">
    <!-- 页面头部 -->
    <div class="flex items-center bg-primary text-white p-4">
      <LeftOutlined class="text-xl cursor-pointer" @click="router.back()" />
      <span class="flex-1 text-center text-lg font-bold">话费充值</span>
      <span class="w-5"></span>
    </div>

    <!-- 手机号输入 -->
    <div class="card-box m-3 relative">
      <div class="text-sm text-foreground mb-3">充值号码</div>
      <a-input
        v-model:value="phoneNumber"
        placeholder="请输入充值手机号"
        :maxlength="11"
        size="large"
      >
        <template #prefix>
          <MobileOutlined class="text-muted-foreground" />
        </template>
      </a-input>
      <span
        class="absolute right-7 top-12 text-primary text-sm cursor-pointer"
        @click="fillMyPhone"
      >
        充本机
      </span>
    </div>

    <!-- 选择面值 -->
    <div class="card-box m-3">
      <div class="text-sm text-foreground mb-3">选择面值</div>
      <div class="grid-4">
        <div
          v-for="product in products"
          :key="product.id"
          class="product-item"
          :class="{ selected: selectedProduct?.id === product.id }"
          @click="selectProduct(product)"
        >
          <div class="text-base font-bold text-foreground">{{ product.faceValue }}元</div>
          <div class="text-sm text-destructive mt-1">¥{{ product.salePrice.toFixed(2) }}</div>
          <div class="text-xs text-muted-foreground mt-1">{{ (product.discount * 10).toFixed(1) }}折</div>
        </div>
      </div>
    </div>

    <!-- 订单信息 -->
    <div class="card-box m-3" v-if="selectedProduct">
      <div class="list-item">
        <span class="text-muted-foreground">充值面值</span>
        <span class="text-foreground">{{ selectedProduct.faceValue }}元</span>
      </div>
      <div class="list-item">
        <span class="text-muted-foreground">优惠金额</span>
        <span class="text-success">-¥{{ (selectedProduct.faceValue - selectedProduct.salePrice).toFixed(2) }}</span>
      </div>
      <div class="list-item border-t border-border mt-2 pt-3 font-bold">
        <span class="text-foreground">实付金额</span>
        <span class="text-destructive text-lg">¥{{ selectedProduct.salePrice.toFixed(2) }}</span>
      </div>
    </div>

    <!-- 提交按钮 -->
    <div class="fixed bottom-0 left-0 right-0 bg-card p-4 shadow-[0_-2px_10px_rgba(0,0,0,0.05)]">
      <a-button
        type="primary"
        size="large"
        class="w-full h-12 text-base rounded-lg"
        :disabled="!canSubmit"
        @click="handleSubmit"
      >
        立即充值
      </a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { LeftOutlined, MobileOutlined } from '@ant-design/icons-vue'
import { getProducts } from '@/api/product'
import { createOrder } from '@/api/order'
import { useUserStore } from '@/stores/user'
import type { Product } from '@/types'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const phoneNumber = ref('')
const products = ref<Product[]>([])
const selectedProduct = ref<Product | null>(null)

const canSubmit = computed(() => {
  return phoneNumber.value.length === 11 && selectedProduct.value
})

onMounted(async () => {
  await loadProducts()

  // 从路由参数选择商品
  if (route.query.productId) {
    const productId = Number(route.query.productId)
    const product = products.value.find(p => p.id === productId)
    if (product) {
      selectedProduct.value = product
    }
  }
})

async function loadProducts() {
  try {
    products.value = await getProducts()
    if (products.value.length > 0 && !selectedProduct.value) {
      selectedProduct.value = products.value[0]
    }
  } catch {
    // 错误已处理
  }
}

function fillMyPhone() {
  if (userStore.userInfo?.phone) {
    phoneNumber.value = userStore.userInfo.phone
  }
}

function selectProduct(product: Product) {
  selectedProduct.value = product
}

async function handleSubmit() {
  if (!selectedProduct.value) return

  if (!/^1[3-9]\d{9}$/.test(phoneNumber.value)) {
    message.error('请输入正确的手机号')
    return
  }

  try {
    // 创建订单
    const order = await createOrder({
      productId: selectedProduct.value.id,
      phoneNumber: phoneNumber.value
    })

    // 跳转到确认页面
    router.push({
      path: '/recharge/confirm',
      query: { orderId: order.id }
    })
  } catch {
    // 错误已处理
  }
}
</script>
