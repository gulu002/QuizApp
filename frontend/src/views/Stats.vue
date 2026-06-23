<template>
  <div class="stats">
    <h2>学习统计</h2>
    <el-row :gutter="12" class="stats-row">
      <el-col :span="8" v-for="stat in statsCards" :key="stat.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-card class="section-card">
      <template #header><span>题型正确率</span></template>
      <div v-for="item in statsByType" :key="item.questionType" class="type-stat">
        <span>{{ typeLabel(item.questionType) }}</span>
        <el-progress :percentage="item.correctRate" :color="progressColor(item.correctRate)" />
        <span>{{ item.correctCount }}/{{ item.totalCount }}</span>
      </div>
      <el-empty v-if="!statsByType.length" description="暂无数据" />
    </el-card>
    <el-card class="section-card">
      <template #header><span>学科正确率</span></template>
      <div v-for="item in statsByCategory" :key="item.questionType" class="type-stat">
        <span>{{ item.questionType }}</span>
        <el-progress :percentage="item.correctRate" :color="progressColor(item.correctRate)" />
        <span>{{ item.correctCount }}/{{ item.totalCount }}</span>
      </div>
      <el-empty v-if="!statsByCategory.length" description="暂无数据" />
    </el-card>
    <el-card class="section-card" v-if="weakness.length">
      <template #header><span>薄弱环节</span></template>
      <div v-for="item in weakness" :key="item.categoryName" class="weakness-item">
        <span>{{ item.categoryName }}</span>
        <el-tag type="danger">{{ item.correctRate }}%</el-tag>
        <span class="weakness-detail">{{ item.correctCount }}/{{ item.totalCount }}</span>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { statsApi } from '../api'

const statsCards = ref([
  { label: '总答题数', value: 0 },
  { label: '正确数', value: 0 },
  { label: '正确率', value: '0%' }
])

const statsByType = ref([])
const statsByCategory = ref([])
const weakness = ref([])

const typeMap = { SINGLE: '单选', MULTI: '多选', JUDGE: '判断' }

function typeLabel(type) {
  return typeMap[type] || type
}

function progressColor(rate) {
  if (rate < 60) return '#f56c6c'
  if (rate < 80) return '#e6a23c'
  return '#67c23a'
}

async function loadOverview() {
  try {
    const res = await statsApi.overview()
    const data = res.data
    if (data) {
      statsCards.value = [
        { label: '总答题数', value: data.totalCount ?? 0 },
        { label: '正确数', value: data.correctCount ?? 0 },
        { label: '正确率', value: (data.correctRate != null ? data.correctRate + '%' : '0%') }
      ]
    }
  } catch {
    // ignore
  }
}

async function loadByType() {
  try {
    const res = await statsApi.byType()
    statsByType.value = res.data || []
  } catch {
    // ignore
  }
}

async function loadByCategory() {
  try {
    const res = await statsApi.byCategory()
    statsByCategory.value = res.data || []
  } catch {
    // ignore
  }
}

async function loadWeakness() {
  try {
    const res = await statsApi.weakness()
    weakness.value = res.data || []
  } catch {
    // ignore
  }
}

onMounted(() => {
  loadOverview()
  loadByType()
  loadByCategory()
  loadWeakness()
})
</script>

<style scoped>
.stats-row {
  margin-bottom: 16px;
}
.stat-card {
  text-align: center;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}
.stat-label {
  font-size: 12px;
  color: #999;
}
.section-card {
  margin-bottom: 16px;
}
.type-stat {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}
.type-stat .el-progress {
  flex: 1;
}
.weakness-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}
.weakness-detail {
  color: #999;
  font-size: 12px;
}
</style>