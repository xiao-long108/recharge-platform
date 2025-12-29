<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="flex items-center bg-primary text-white p-4">
      <LeftOutlined class="text-xl cursor-pointer" @click="router.back()" />
      <span class="flex-1 text-center text-lg font-bold">修改登录密码</span>
      <span class="w-5"></span>
    </div>

    <div class="card-box m-3 p-5">
      <a-form
        ref="formRef"
        :model="form"
        :rules="rules"
        layout="vertical"
        @finish="handleSubmit"
      >
        <a-form-item label="旧密码" name="oldPassword">
          <a-input-password v-model:value="form.oldPassword" placeholder="请输入旧密码" />
        </a-form-item>
        <a-form-item label="新密码" name="newPassword">
          <a-input-password v-model:value="form.newPassword" placeholder="请输入新密码" />
        </a-form-item>
        <a-form-item label="确认密码" name="confirmPassword">
          <a-input-password v-model:value="form.confirmPassword" placeholder="请确认新密码" />
        </a-form-item>

        <a-button
          type="primary"
          html-type="submit"
          size="large"
          :loading="loading"
          class="w-full h-11 mt-5"
        >
          确认修改
        </a-button>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import type { Rule } from 'ant-design-vue/es/form'
import { LeftOutlined } from '@ant-design/icons-vue'
import { changePassword } from '@/api/user'

const router = useRouter()

const formRef = ref()
const loading = ref(false)

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = async (_rule: Rule, value: string) => {
  if (value !== form.newPassword) {
    throw new Error('两次输入的密码不一致')
  }
}

const rules: Record<string, Rule[]> = {
  oldPassword: [
    { required: true, message: '请输入旧密码' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码' },
    { min: 6, message: '密码长度不能少于6位' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码' },
    { validator: validateConfirmPassword }
  ]
}

async function handleSubmit() {
  loading.value = true
  try {
    await changePassword(form.oldPassword, form.newPassword)
    message.success('密码修改成功')
    router.back()
  } catch {
    // 错误已处理
  } finally {
    loading.value = false
  }
}
</script>
