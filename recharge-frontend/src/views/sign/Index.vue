<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="flex items-center bg-primary text-white p-4">
      <LeftOutlined class="text-xl cursor-pointer" @click="router.back()" />
      <span class="flex-1 text-center text-lg font-bold">每日签到</span>
      <span class="w-5"></span>
    </div>

    <!-- 签到信息卡片 -->
    <div class="sign-header">
      <div class="sign-stats">
        <div class="stat-item">
          <div class="stat-value">{{ signInfo.continuousDays }}</div>
          <div class="stat-label">连续签到(天)</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-value">{{ signInfo.totalDays }}</div>
          <div class="stat-label">累计签到(天)</div>
        </div>
      </div>

      <!-- 签到按钮 -->
      <div class="sign-btn-wrapper">
        <div
          class="sign-btn"
          :class="{ signed: signInfo.isSigned }"
          @click="handleSign"
        >
          <template v-if="signInfo.isSigned">
            <CheckOutlined class="text-2xl" />
            <span class="text-sm mt-1">已签到</span>
          </template>
          <template v-else>
            <GiftOutlined class="text-2xl" />
            <span class="text-sm mt-1">签到领奖</span>
          </template>
        </div>
        <div class="sign-tip">
          {{ signInfo.isSigned ? '明天继续加油哦' : `签到可获得 ¥${signInfo.todayReward.toFixed(2)}` }}
        </div>
      </div>
    </div>

    <!-- 签到日历 -->
    <div class="card-box m-3">
      <div class="flex justify-between items-center mb-4">
        <span class="text-base font-bold text-foreground">本月签到</span>
        <span class="text-sm text-muted-foreground">{{ currentMonth }}</span>
      </div>

      <!-- 星期头部 -->
      <div class="calendar-header">
        <div v-for="day in weekDays" :key="day" class="calendar-weekday">{{ day }}</div>
      </div>

      <!-- 日期网格 -->
      <div class="calendar-grid">
        <div
          v-for="(day, index) in calendarDays"
          :key="index"
          class="calendar-day"
          :class="{
            empty: !day,
            signed: day && isSignedDay(day),
            today: day && isToday(day)
          }"
        >
          <template v-if="day">
            <span class="day-num">{{ day }}</span>
            <CheckCircleFilled v-if="isSignedDay(day)" class="sign-icon" />
          </template>
        </div>
      </div>
    </div>

    <!-- 签到规则 -->
    <div class="card-box m-3">
      <div class="text-base font-bold text-foreground mb-4">签到规则</div>
      <div class="rules-list">
        <div class="rule-item">
          <span class="rule-dot"></span>
          <span>每日签到可获得随机奖励 ¥0.1 - ¥1.0</span>
        </div>
        <div class="rule-item">
          <span class="rule-dot"></span>
          <span>连续签到7天额外奖励 ¥5.0</span>
        </div>
        <div class="rule-item">
          <span class="rule-dot"></span>
          <span>连续签到30天额外奖励 ¥20.0</span>
        </div>
        <div class="rule-item">
          <span class="rule-dot"></span>
          <span>奖励将自动发放到您的账户余额</span>
        </div>
      </div>
    </div>

    <!-- 签到成功弹窗 -->
    <a-modal
      v-model:open="showRewardModal"
      :footer="null"
      :closable="false"
      centered
      width="300px"
    >
      <div class="reward-modal">
        <div class="reward-icon">
          <GiftOutlined />
        </div>
        <div class="reward-title">签到成功</div>
        <div class="reward-amount">+¥{{ lastReward.toFixed(2) }}</div>
        <div class="reward-tip">已连续签到 {{ signInfo.continuousDays }} 天</div>
        <a-button type="primary" block @click="showRewardModal = false">
          知道了
        </a-button>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { LeftOutlined, GiftOutlined, CheckOutlined, CheckCircleFilled } from '@ant-design/icons-vue'
import { getSignInfo, doSign } from '@/api/sign'

const router = useRouter()

const weekDays = ['日', '一', '二', '三', '四', '五', '六']

const signInfo = ref({
  isSigned: false,
  continuousDays: 0,
  totalDays: 0,
  todayReward: 0.5,
  signRecords: [] as { date: string; signed: boolean }[]
})

const showRewardModal = ref(false)
const lastReward = ref(0)

const currentMonth = computed(() => {
  const now = new Date()
  return `${now.getFullYear()}年${now.getMonth() + 1}月`
})

const calendarDays = computed(() => {
  const now = new Date()
  const year = now.getFullYear()
  const month = now.getMonth()
  const firstDay = new Date(year, month, 1).getDay()
  const daysInMonth = new Date(year, month + 1, 0).getDate()

  const days: (number | null)[] = []
  for (let i = 0; i < firstDay; i++) {
    days.push(null)
  }
  for (let i = 1; i <= daysInMonth; i++) {
    days.push(i)
  }
  return days
})

const isToday = (day: number) => {
  const now = new Date()
  return day === now.getDate()
}

const isSignedDay = (day: number) => {
  const now = new Date()
  const dateStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`
  return signInfo.value.signRecords.some(r => r.date === dateStr && r.signed)
}

const loadSignInfo = async () => {
  try {
    const data = await getSignInfo()
    signInfo.value = data
  } catch {
    // 错误处理
  }
}

const handleSign = async () => {
  if (signInfo.value.isSigned) {
    message.info('今日已签到')
    return
  }

  try {
    const result = await doSign()
    lastReward.value = result.reward
    signInfo.value.isSigned = true
    signInfo.value.continuousDays = result.continuousDays
    signInfo.value.totalDays += 1
    showRewardModal.value = true
  } catch {
    // 错误处理
  }
}

onMounted(() => {
  loadSignInfo()
})
</script>

<style scoped lang="scss">
.sign-header {
  background: linear-gradient(135deg, var(--primary) 0%, hsl(180, 70%, 45%) 100%);
  padding: 24px;
  color: white;
}

.sign-stats {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 24px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
}

.stat-label {
  font-size: 12px;
  opacity: 0.8;
  margin-top: 4px;
}

.stat-divider {
  width: 1px;
  background: rgba(255, 255, 255, 0.3);
}

.sign-btn-wrapper {
  text-align: center;
}

.sign-btn {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  border: 3px solid white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 0 auto 12px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover:not(.signed) {
    transform: scale(1.05);
    background: rgba(255, 255, 255, 0.3);
  }

  &.signed {
    background: rgba(255, 255, 255, 0.9);
    color: var(--primary);
    cursor: default;
  }
}

.sign-tip {
  font-size: 14px;
  opacity: 0.9;
}

.calendar-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
  margin-bottom: 8px;
}

.calendar-weekday {
  text-align: center;
  font-size: 12px;
  color: #999;
  padding: 8px 0;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.calendar-day {
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  position: relative;
  font-size: 14px;
  color: #333;

  &.empty {
    background: transparent;
  }

  &.today {
    background: var(--primary);
    color: white;
  }

  &.signed:not(.today) {
    background: hsl(var(--primary-hue, 180) 70% 95%);
  }

  .sign-icon {
    position: absolute;
    bottom: 2px;
    font-size: 10px;
    color: var(--primary);
  }

  &.today .sign-icon {
    color: white;
  }
}

.rules-list {
  space-y: 12px;
}

.rule-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 8px 0;
  font-size: 13px;
  color: #666;
}

.rule-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--primary);
  margin-top: 6px;
  flex-shrink: 0;
}

.reward-modal {
  text-align: center;
  padding: 20px 0;
}

.reward-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ffd700 0%, #ff9500 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  font-size: 36px;
  color: white;
}

.reward-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.reward-amount {
  font-size: 32px;
  font-weight: bold;
  color: #ff6b00;
  margin: 12px 0;
}

.reward-tip {
  font-size: 14px;
  color: #999;
  margin-bottom: 20px;
}
</style>
