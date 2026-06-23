<template>
  <div class="home">
    <div class="greeting">你好，{{ userStore.user?.nickname || '刷题人' }}</div>

    <el-row :gutter="12" class="stats-row">
      <el-col :span="8" v-for="stat in statsCards" :key="stat.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="section-card">
      <template #header><span>快捷入口</span></template>
      <el-row :gutter="12">
        <el-col :span="8" v-for="item in quickActions" :key="item.label">
          <div class="quick-action" @click="$router.push(item.path)">
            <el-icon :size="28"><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="section-card">
      <template #header><span>本周打卡</span></template>
      <div class="checkin-bar">
        <div
          v-for="(day, index) in weekDays"
          :key="index"
          class="checkin-day"
          :class="{ active: day.checked }"
        >
          <div class="checkin-dot"></div>
          <span>{{ day.label }}</span>
        </div>
      </div>
    </el-card>

    <el-card class="section-card" v-if="recentSessions.length">
      <template #header><span>最近练习</span></template>
      <div v-for="session in recentSessions" :key="session.sessionId" class="session-item">
        <span>{{ formatType(session.sessionType) }}</span>
        <span>{{ session.correctCount }}/{{ session.totalCount }}</span>
        <span class="time">{{ formatTime(session.startTime) }}</span>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { statsApi, practiceApi } from '../api'
import { Edit, Warning, Star } from '@element-plus/icons-vue'

const userStore = useUserStore()

const stats = ref({
  totalAnswers: 0,
  correctRate: 0,
  wrongBookCount: 0,
  favoriteCount: 0,
  streakDays: 0,
  totalDurationSec: 0
})

const recentSessions = ref([])

const statsCards = computed(() => [
  { label: '累计答题', value: stats.value.totalAnswers, key: 'totalAnswers' },
  { label: '正确率', value: stats.value.correctRate + '%', key: 'correctRate' },
  { label: '错题数', value: stats.value.wrongBookCount, key: 'wrongBookCount' },
  { label: '收藏数', value: stats.value.favoriteCount, key: 'favoriteCount' },
  { label: '连续天数', value: stats.value.streakDays, key: 'streakDays' },
  { label: '学习时长', value: formatDuration(stats.value.totalDurationSec), key: 'totalDurationSec' }
])

const quickActions = [
  { label: '开始练习', path: '/practice', icon: Edit },
  { label: '错题重练', path: '/wrong', icon: Warning },
  { label: '收藏练习', path: '/favorites', icon: Star }
]

const weekDays = computed(() => {
  const days = []
  const dayNames = ['日', '一', '二', '三', '四', '五', '六']
  const today = new Date()
  for (let i = 6; i >= 0; i--) {
    const d = new Date(today)
    d.setDate(d.getDate() - i)
    const dateStr = d.toISOString().split('T')[0]
    days.push({
      label: dayNames[d.getDay()],
      date: dateStr,
      checked: checkinDates.value.includes(dateStr)
    })
  }
  return days
})

const checkinDates = ref([])

function formatType(type) {
  const map = {
    ORDER: '顺序练习',
    RANDOM: '随机练习',
    EXAM: '模拟考试',
    WRONG: '错题重练',
    FAV: '收藏练习',
    TYPE: '题型专练'
  }
  return map[type] || type
}

function formatTime(timeStr) {
  if (!timeStr) return ''
  const d = new Date(timeStr)
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  return `${month}-${day} ${hours}:${minutes}`
}

function formatDuration(seconds) {
  if (!seconds || seconds <= 0) return '0分钟'
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  if (h > 0) return `${h}小时${m}分钟`
  return `${m}分钟`
}

async function loadStats() {
  try {
    const res = await statsApi.overview()
    if (res.data) {
      stats.value = res.data
    }
  } catch (e) {
    // ignore
  }
}

async function loadHistory() {
  try {
    const res = await practiceApi.getHistory(1, 5)
    if (res.data) {
      recentSessions.value = res.data.records || res.data || []
    }
  } catch (e) {
    // ignore
  }
}

async function loadCalendar() {
  try {
    const now = new Date()
    const res = await statsApi.calendar(now.getFullYear(), now.getMonth() + 1)
    if (res.data) {
      checkinDates.value = res.data.map(item => item.date) || []
    }
  } catch (e) {
    // ignore
  }
}

onMounted(() => {
  loadStats()
  loadHistory()
  loadCalendar()
})
</script>

<style scoped>
.home {
  padding: 16px;
}

.greeting {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 16px;
}

.stat-card {
  text-align: center;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.section-card {
  margin-top: 16px;
}

.quick-action {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px;
  cursor: pointer;
  border-radius: 8px;
  transition: background 0.2s;
  color: #606266;
}

.quick-action:hover {
  background: #ecf5ff;
  color: #409eff;
}

.quick-action span {
  margin-top: 6px;
  font-size: 13px;
}

.checkin-bar {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
}

.checkin-day {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  color: #c0c4cc;
}

.checkin-day.active {
  color: #409eff;
}

.checkin-dot {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #e8e8e8;
}

.checkin-day.active .checkin-dot {
  background: #409eff;
}

.checkin-day span {
  font-size: 12px;
}

.session-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
  font-size: 14px;
}

.session-item:last-child {
  border-bottom: none;
}

.session-item .time {
  color: #999;
  font-size: 12px;
}
</style>