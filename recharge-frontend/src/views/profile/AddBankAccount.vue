<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="flex items-center bg-primary text-white p-4">
      <LeftOutlined class="text-xl cursor-pointer" @click="router.back()" />
      <span class="flex-1 text-center text-lg font-bold">添加银行卡</span>
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
        <a-form-item label="银行名称" name="bankName">
          <a-input v-model:value="form.bankName" placeholder="请输入银行名称" />
        </a-form-item>
        <a-form-item label="持卡人" name="accountHolder">
          <a-input v-model:value="form.accountHolder" placeholder="请输入持卡人姓名" />
        </a-form-item>
        <a-form-item label="银行卡号" name="accountNumber">
          <a-input v-model:value="form.accountNumber" placeholder="请输入银行卡号" :maxlength="19" />
        </a-form-item>
        <a-form-item label="开户支行" name="branchName">
          <a-input v-model:value="form.branchName" placeholder="请输入开户支行" />
        </a-form-item>

        <a-button
          type="primary"
          html-type="submit"
          size="large"
          :loading="loading"
          class="w-full h-11 mt-5"
        >
          确认添加
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
import { addBankAccount } from '@/api/user'

const router = useRouter()

const formRef = ref()
const loading = ref(false)

const form = reactive({
  bankName: '',
  accountHolder: '',
  accountNumber: '',
  branchName: ''
})

const rules: Record<string, Rule[]> = {
  bankName: [
    { required: true, message: '请输入银行名称' }
  ],
  accountHolder: [
    { required: true, message: '请输入持卡人姓名' }
  ],
  accountNumber: [
    { required: true, message: '请输入银行卡号' },
    { pattern: /^\d{16,19}$/, message: '请输入正确的银行卡号' }
  ],
  branchName: [
    { required: true, message: '请输入开户支行' }
  ]
}

async function handleSubmit() {
  loading.value = true
  try {
    await addBankAccount(form)
    message.success('添加成功')
    router.back()
  } catch {
    // 错误已处理
  } finally {
    loading.value = false
  }
}
</script>
