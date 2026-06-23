<template>
  <div class="favorites">
    <div class="header">
      <h2>收藏夹 ({{ total }})</h2>
      <el-button v-if="selectedIds.length" type="danger" size="small" @click="handleBatchRemove">批量取消 ({{ selectedIds.length }})</el-button>
    </div>
    <el-card v-for="item in list" :key="item.id" class="fav-card">
      <div class="fav-header">
        <el-checkbox :model-value="selectedIds.includes(item.questionId)" @change="onSelect(item.questionId)" />
        <el-tag :type="typeTag(item.questionType)" size="small">{{ typeLabel(item.questionType) }}</el-tag>
        <span class="fav-time">{{ item.createTime }}</span>
      </div>
      <div class="fav-title">{{ item.title }}</div>
      <div class="fav-actions">
        <el-button size="small" @click="handleRemove(item.questionId)">取消收藏</el-button>
      </div>
    </el-card>
    <el-empty v-if="!loading && list.length === 0" description="暂无收藏" />
    <el-pagination v-if="total > 0" v-model:current-page="page" :page-size="size" :total="total" layout="prev, pager, next" @current-change="loadData" class="pagination" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { favoriteApi } from '../api'

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const selectedIds = ref([])
const loading = ref(false)

const typeMap = { SINGLE: '单选', MULTI: '多选', JUDGE: '判断' }
const typeTagMap = { SINGLE: '', MULTI: 'success', JUDGE: 'warning' }

function typeLabel(type) {
  return typeMap[type] || type
}

function typeTag(type) {
  return typeTagMap[type] || ''
}

function onSelect(questionId) {
  const idx = selectedIds.value.indexOf(questionId)
  if (idx > -1) {
    selectedIds.value.splice(idx, 1)
  } else {
    selectedIds.value.push(questionId)
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await favoriteApi.getList(page.value, size.value)
    list.value = res.data.records || res.data || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

async function handleRemove(questionId) {
  try {
    await ElMessageBox.confirm('确定取消收藏该题目？', '提示', { type: 'warning' })
  } catch {
    return
  }
  await favoriteApi.toggle(questionId)
  ElMessage.success('已取消收藏')
  selectedIds.value = selectedIds.value.filter(id => id !== questionId)
  await loadData()
}

async function handleBatchRemove() {
  try {
    await ElMessageBox.confirm(`确定批量取消 ${selectedIds.value.length} 条收藏？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  await favoriteApi.batchRemove(selectedIds.value)
  ElMessage.success('已批量取消收藏')
  selectedIds.value = []
  await loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.fav-card {
  margin-bottom: 12px;
}
.fav-header {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 8px;
}
.fav-time {
  color: #999;
  font-size: 12px;
}
.fav-title {
  margin-bottom: 12px;
  line-height: 1.5;
}
.fav-actions {
  display: flex;
  gap: 8px;
}
.pagination {
  margin-top: 16px;
  justify-content: center;
}
</style>