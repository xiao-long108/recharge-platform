<template>
  <div class="min-h-screen bg-gradient-to-br from-primary to-[hsl(180,70%,45%)] px-5 py-16">
    <div class="text-center text-white mb-10">
      <h1 class="text-3xl font-bold mb-2">话费充值平台</h1>
      <p class="text-base opacity-90">注册新账号</p>
    </div>

    <div class="bg-card rounded-xl p-6 max-w-md mx-auto shadow-lg">
      <a-form
        ref="formRef"
        :model="form"
        :rules="rules"
        layout="vertical"
        @finish="handleRegister"
      >
        <a-form-item name="phone">
          <a-input
            v-model:value="form.phone"
            placeholder="请输入手机号"
            size="large"
            :maxlength="11"
          >
            <template #prefix>
              <MobileOutlined class="text-muted-foreground" />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item name="password">
          <a-input-password
            v-model:value="form.password"
            placeholder="请输入密码"
            size="large"
          >
            <template #prefix>
              <LockOutlined class="text-muted-foreground" />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item name="confirmPassword">
          <a-input-password
            v-model:value="form.confirmPassword"
            placeholder="请确认密码"
            size="large"
          >
            <template #prefix>
              <LockOutlined class="text-muted-foreground" />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item name="inviteCode">
          <a-input
            v-model:value="form.inviteCode"
            placeholder="邀请码（选填）"
            size="large"
          >
            <template #prefix>
              <GiftOutlined class="text-muted-foreground" />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            :loading="loading"
            class="w-full h-12 text-base rounded-full"
          >
            注册
          </a-button>
        </a-form-item>
      </a-form>

      <div class="text-center mt-5 text-muted-foreground">
        已有账号？
        <router-link to="/login" class="vben-link">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import type { Rule } from 'ant-design-vue/es/form'
import { MobileOutlined, LockOutlined, GiftOutlined } from '@ant-design/icons-vue'
import { register } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)

const form = reactive({
  phone: '',
  password: '',
  confirmPassword: '',
  inviteCode: ''
})

const validateConfirmPassword = async (_rule: Rule, value: string) => {
  if (value !== form.password) {
    throw new Error('两次输入的密码不一致')
  }
}

const rules: Record<string, Rule[]> = {
  phone: [
    { required: true, message: '请输入手机号' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, message: '密码长度不能少于6位' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码' },
    { validator: validateConfirmPassword }
  ]
}

async function handleRegister() {
  loading.value = true
  try {
    const res = await register({
      phone: form.phone,
      password: form.password,
      confirmPassword: form.confirmPassword,
      inviteCode: form.inviteCode || undefined
    })
    userStore.setToken(res.token)
    message.success('注册成功')
    router.push('/home')
  } catch {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}
</script>
