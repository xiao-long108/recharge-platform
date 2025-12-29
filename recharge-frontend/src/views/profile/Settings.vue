<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="flex items-center bg-primary text-white p-4">
      <LeftOutlined class="text-xl cursor-pointer" @click="router.back()" />
      <span class="flex-1 text-center text-lg font-bold">账号设置</span>
      <span class="w-5"></span>
    </div>

    <div class="menu-card m-3">
      <div class="menu-item" @click="router.push('/profile/change-password')">
        <span class="text-sm text-foreground">修改登录密码</span>
        <RightOutlined class="text-sm text-muted-foreground" />
      </div>
      <div class="menu-item" @click="handlePayPassword">
        <span class="text-sm text-foreground">{{ hasPayPassword ? '修改支付密码' : '设置支付密码' }}</span>
        <RightOutlined class="text-sm text-muted-foreground" />
      </div>
    </div>

    <!-- 设置支付密码弹窗 -->
    <a-modal
      v-model:open="showPayPasswordDialog"
      :title="hasPayPassword ? '修改支付密码' : '设置支付密码'"
      :confirm-loading="loading"
      @ok="submitPayPassword"
    >
      <a-form layout="vertical" class="mt-4">
        <a-form-item label="旧支付密码" v-if="hasPayPassword">
          <a-input-password v-model:value="payPasswordForm.oldPassword" :maxlength="6" />
        </a-form-item>
        <a-form-item label="新支付密码">
          <a-input-password v-model:value="payPasswordForm.newPassword" :maxlength="6" />
        </a-form-item>
        <a-form-item label="确认密码">
          <a-input-password v-model:value="payPasswordForm.confirmPassword" :maxlength="6" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { LeftOutlined, RightOutlined } from '@ant-design/icons-vue'
import { setPayPassword, changePayPassword } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const hasPayPassword = computed(() => userStore.userInfo?.hasPayPassword || false)
const showPayPasswordDialog = ref(false)
const loading = ref(false)

const payPasswordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

function handlePayPassword() {
  payPasswordForm.oldPassword = ''
  payPasswordForm.newPassword = ''
  payPasswordForm.confirmPassword = ''
  showPayPasswordDialog.value = true
}

async function submitPayPassword() {
  if (!payPasswordForm.newPassword || payPasswordForm.newPassword.length !== 6) {
    message.error('请输入6位支付密码')
    return
  }

  if (payPasswordForm.newPassword !== payPasswordForm.confirmPassword) {
    message.error('两次输入的密码不一致')
    return
  }

  if (hasPayPassword.value && !payPasswordForm.oldPassword) {
    message.error('请输入旧支付密码')
    return
  }

  loading.value = true
  try {
    if (hasPayPassword.value) {
      await changePayPassword(payPasswordForm.oldPassword, payPasswordForm.newPassword)
    } else {
      await setPayPassword(payPasswordForm.newPassword)
    }

    message.success('设置成功')
    showPayPasswordDialog.value = false

    // 更新用户信息
    if (userStore.userInfo) {
      userStore.userInfo.hasPayPassword = true
    }
  } catch {
    // 错误已处理
  } finally {
    loading.value = false
  }
}
</script>
