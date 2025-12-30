<template>
  <div class="settings-page">
    <div class="settings-container">
      <!-- 左侧菜单 -->
      <div class="settings-menu">
        <div
          v-for="item in menuItems"
          :key="item.key"
          class="menu-item"
          :class="{ active: activeKey === item.key }"
          @click="activeKey = item.key"
        >
          <component :is="item.icon" />
          <span>{{ item.label }}</span>
        </div>
      </div>

      <!-- 右侧内容 -->
      <div class="settings-content">
        <!-- 基础设置 -->
        <div v-show="activeKey === 'basic'" class="settings-section">
          <h3 class="section-title">基础设置</h3>
          <a-form :model="basicForm" :label-col="{ span: 4 }" :wrapper-col="{ span: 14 }">
            <a-form-item label="平台名称">
              <a-input v-model:value="basicForm.siteName" placeholder="请输入平台名称" />
            </a-form-item>
            <a-form-item label="平台描述">
              <a-input v-model:value="basicForm.siteDesc" placeholder="请输入平台描述" />
            </a-form-item>
            <a-form-item label="平台Logo">
              <a-input v-model:value="basicForm.siteLogo" placeholder="Logo图片URL" />
            </a-form-item>
            <a-form-item label="客服链接">
              <a-input v-model:value="basicForm.customerServiceUrl" placeholder="客服链接地址" />
            </a-form-item>
            <a-form-item label="分享标题">
              <a-input v-model:value="basicForm.shareTitle" placeholder="分享标题" />
            </a-form-item>
            <a-form-item label="分享描述">
              <a-input v-model:value="basicForm.shareDesc" placeholder="分享描述" />
            </a-form-item>
            <a-form-item label="分享图片">
              <a-input v-model:value="basicForm.shareImage" placeholder="分享图片URL" />
            </a-form-item>
            <a-form-item :wrapper-col="{ offset: 4 }">
              <a-button type="primary" @click="saveBasic">保存设置</a-button>
            </a-form-item>
          </a-form>
        </div>

        <!-- 充值设置 -->
        <div v-show="activeKey === 'recharge'" class="settings-section">
          <h3 class="section-title">充值设置</h3>
          <a-form :model="rechargeForm" :label-col="{ span: 4 }" :wrapper-col="{ span: 14 }">
            <a-form-item label="最低充值">
              <a-input v-model:value="rechargeForm.minRechargeAmount" style="width: 200px">
                <template #addonAfter>R$</template>
              </a-input>
            </a-form-item>
            <a-form-item label="最高充值">
              <a-input v-model:value="rechargeForm.maxRechargeAmount" style="width: 200px">
                <template #addonAfter>R$</template>
              </a-input>
            </a-form-item>
            <a-form-item label="订单超时">
              <a-input v-model:value="rechargeForm.orderTimeout" style="width: 200px">
                <template #addonAfter>分钟</template>
              </a-input>
              <div class="form-help">订单未支付自动取消时间</div>
            </a-form-item>
            <a-form-item label="自动退款">
              <a-switch v-model:checked="autoRefundEnabled" checked-children="开启" un-checked-children="关闭" />
              <div class="form-help">充值失败自动退款</div>
            </a-form-item>
            <a-form-item :wrapper-col="{ offset: 4 }">
              <a-button type="primary" @click="saveRecharge">保存设置</a-button>
            </a-form-item>
          </a-form>
        </div>

        <!-- 提现设置 -->
        <div v-show="activeKey === 'withdraw'" class="settings-section">
          <h3 class="section-title">提现设置</h3>
          <a-form :model="withdrawForm" :label-col="{ span: 4 }" :wrapper-col="{ span: 14 }">
            <a-form-item label="最低提现">
              <a-input v-model:value="withdrawForm.withdrawMinAmount" style="width: 200px">
                <template #addonAfter>R$</template>
              </a-input>
            </a-form-item>
            <a-form-item label="最高提现">
              <a-input v-model:value="withdrawForm.withdrawMaxAmount" style="width: 200px">
                <template #addonAfter>R$</template>
              </a-input>
            </a-form-item>
            <a-form-item label="提现手续费">
              <a-input v-model:value="withdrawForm.withdrawFeeRate" style="width: 200px">
                <template #addonAfter>比例(如0.006=0.6%)</template>
              </a-input>
            </a-form-item>
            <a-form-item label="每日提现次数">
              <a-input v-model:value="withdrawForm.withdrawDailyLimit" style="width: 200px">
                <template #addonAfter>次</template>
              </a-input>
            </a-form-item>
            <a-form-item :wrapper-col="{ offset: 4 }">
              <a-button type="primary" @click="saveWithdraw">保存设置</a-button>
            </a-form-item>
          </a-form>
        </div>

        <!-- 佣金设置 -->
        <div v-show="activeKey === 'commission'" class="settings-section">
          <h3 class="section-title">佣金设置</h3>
          <a-alert message="佣金比例说明：用户消费后，其上级可获得对应比例的佣金奖励。比例为小数，如0.05表示5%" type="info" show-icon class="mb-4" />
          <a-form :model="commissionForm" :label-col="{ span: 4 }" :wrapper-col="{ span: 14 }">
            <a-form-item label="启用佣金">
              <a-switch v-model:checked="commissionEnabled" checked-children="开启" un-checked-children="关闭" />
            </a-form-item>
            <a-form-item label="一级佣金">
              <a-input v-model:value="commissionForm.commissionRateLevel1" style="width: 200px">
                <template #addonAfter>比例(如0.05=5%)</template>
              </a-input>
              <div class="form-help">直接邀请的用户消费可获得的佣金比例</div>
            </a-form-item>
            <a-form-item label="二级佣金">
              <a-input v-model:value="commissionForm.commissionRateLevel2" style="width: 200px">
                <template #addonAfter>比例(如0.03=3%)</template>
              </a-input>
              <div class="form-help">间接邀请的用户（二级）消费可获得的佣金比例</div>
            </a-form-item>
            <a-form-item label="三级佣金">
              <a-input v-model:value="commissionForm.commissionRateLevel3" style="width: 200px">
                <template #addonAfter>比例(如0.01=1%)</template>
              </a-input>
              <div class="form-help">间接邀请的用户（三级）消费可获得的佣金比例</div>
            </a-form-item>
            <a-form-item :wrapper-col="{ offset: 4 }">
              <a-button type="primary" @click="saveCommission">保存设置</a-button>
            </a-form-item>
          </a-form>
        </div>

        <!-- 余额宝设置 -->
        <div v-show="activeKey === 'yuebao'" class="settings-section">
          <h3 class="section-title">余额宝设置</h3>
          <a-form :model="yuebaoForm" :label-col="{ span: 4 }" :wrapper-col="{ span: 14 }">
            <a-form-item label="功能开关">
              <a-switch v-model:checked="yuebaoEnabled" checked-children="开启" un-checked-children="关闭" />
            </a-form-item>
            <a-form-item label="年化收益率">
              <a-input v-model:value="yuebaoForm.yuebaoAnnualRate" style="width: 200px">
                <template #addonAfter>比例(如0.0365=3.65%)</template>
              </a-input>
            </a-form-item>
            <a-form-item label="最低转入">
              <a-input v-model:value="yuebaoForm.yuebaoMinTransfer" style="width: 200px">
                <template #addonAfter>R$</template>
              </a-input>
            </a-form-item>
            <a-form-item :wrapper-col="{ offset: 4 }">
              <a-button type="primary" @click="saveYuebao">保存设置</a-button>
            </a-form-item>
          </a-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  SettingOutlined,
  MobileOutlined,
  WalletOutlined,
  DollarOutlined,
  PayCircleOutlined
} from '@ant-design/icons-vue'
import request from '@/utils/request'

const activeKey = ref('basic')

// 开关的计算属性
const autoRefundEnabled = computed({
  get: () => rechargeForm.autoRefund === 'true',
  set: (val: boolean) => { rechargeForm.autoRefund = val ? 'true' : 'false' }
})

const commissionEnabled = computed({
  get: () => commissionForm.commissionEnabled === 'true',
  set: (val: boolean) => { commissionForm.commissionEnabled = val ? 'true' : 'false' }
})

const yuebaoEnabled = computed({
  get: () => yuebaoForm.yuebaoEnabled === 'true',
  set: (val: boolean) => { yuebaoForm.yuebaoEnabled = val ? 'true' : 'false' }
})

const menuItems = [
  { key: 'basic', label: '基础设置', icon: SettingOutlined },
  { key: 'recharge', label: '充值设置', icon: MobileOutlined },
  { key: 'withdraw', label: '提现设置', icon: WalletOutlined },
  { key: 'commission', label: '佣金设置', icon: DollarOutlined },
  { key: 'yuebao', label: '余额宝设置', icon: PayCircleOutlined }
]

const basicForm = reactive({
  siteName: '话费充值平台',
  siteDesc: '',
  siteLogo: '',
  customerServiceUrl: '',
  shareTitle: '',
  shareDesc: '',
  shareImage: ''
})

const rechargeForm = reactive({
  minRechargeAmount: '10',
  maxRechargeAmount: '10000',
  orderTimeout: '30',
  autoRefund: 'true'
})

const withdrawForm = reactive({
  withdrawMinAmount: '100',
  withdrawMaxAmount: '50000',
  withdrawFeeRate: '0.006',
  withdrawDailyLimit: '3'
})

const commissionForm = reactive({
  commissionRateLevel1: '0.05',
  commissionRateLevel2: '0.03',
  commissionRateLevel3: '0.01',
  commissionEnabled: 'true'
})

const yuebaoForm = reactive({
  yuebaoEnabled: 'true',
  yuebaoAnnualRate: '0.0365',
  yuebaoMinTransfer: '1'
})

const fetchSettings = async () => {
  try {
    const res = await request.get('/api/admin/settings')
    if (res.data.code === 200) {
      const data = res.data.data
      Object.assign(basicForm, data.basic || {})
      Object.assign(rechargeForm, data.recharge || {})
      Object.assign(withdrawForm, data.withdraw || {})
      Object.assign(commissionForm, data.commission || {})
      Object.assign(yuebaoForm, data.yuebao || {})
    }
  } catch (e) {
    console.error(e)
  }
}

const saveBasic = async () => {
  try {
    await request.put('/api/admin/settings/basic', basicForm)
    message.success('保存成功')
  } catch {
    message.error('保存失败')
  }
}

const saveRecharge = async () => {
  try {
    await request.put('/api/admin/settings/recharge', rechargeForm)
    message.success('保存成功')
  } catch {
    message.error('保存失败')
  }
}

const saveWithdraw = async () => {
  try {
    await request.put('/api/admin/settings/withdraw', withdrawForm)
    message.success('保存成功')
  } catch {
    message.error('保存失败')
  }
}

const saveCommission = async () => {
  try {
    await request.put('/api/admin/settings/commission', commissionForm)
    message.success('保存成功')
  } catch {
    message.error('保存失败')
  }
}

const saveYuebao = async () => {
  try {
    await request.put('/api/admin/settings/yuebao', yuebaoForm)
    message.success('保存成功')
  } catch {
    message.error('保存失败')
  }
}

onMounted(() => {
  fetchSettings()
})
</script>

<style scoped lang="scss">
.settings-page {
  padding: 0;
}

.settings-container {
  display: flex;
  gap: 20px;
  min-height: calc(100vh - 180px);
}

.settings-menu {
  width: 200px;
  background: #fff;
  border-radius: 8px;
  padding: 16px 0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  flex-shrink: 0;

  .menu-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 12px 24px;
    cursor: pointer;
    color: #606266;
    transition: all 0.2s;

    &:hover {
      background: #f5f7fa;
      color: #1890ff;
    }

    &.active {
      background: #e6f7ff;
      color: #1890ff;
      border-right: 3px solid #1890ff;
    }
  }
}

.settings-content {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.settings-section {
  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 24px;
    padding-bottom: 12px;
    border-bottom: 1px solid #ebeef5;
  }
}

.form-help {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
