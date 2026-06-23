<template>
  <div class="wrong-questions">
    <div class="header">
      <h2>错题本 ({{ total }})</h2>
      <el-button type="danger" size="small" @click="handleClearAll" :disabled="!total">清空全部</el-button>
    </div>

    <el-card v-for="item in list" :key="item.id" class="wrong-card">
      <div class="wrong-header">
        <el-tag :type="typeTag(item.questionType)" size="small">{{ typeLabel(item.questionType) }}</el-tag>
        <span class="wrong-count">错误 {{ item.wrongCount }} 次</span>
        <span class="wrong-time">{{ item.lastWrongTime }}</span>
      </div>
      <div class="wrong-title">{{ item.title }}</div>
      <div class="wrong-actions">
        <el-button size="small" @click="viewDetail(item.questionId)">查看详情</el-button>
        <el-button size="small" type="danger" @click="handleRemove(item.questionId)">移除</el-button>
      </div>
    </el-card>

    <el-empty v-if="!loading && total === 0" description="暂无错题" />

    <el-pagination
      v-if="total > 0"
      v-model:current-page="page"
      :page-size="size"
      :total="total"
      layout="prev, pager, next"
      @current-change="loadData"
      class="pagination"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { wrongApi } from '../api'

const router = useRouter()

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)

function typeLabel(type) {
  const map = { SINGLE: '单选题', MULTI: '多选题', JUDGE: '判断题' }
  return map[type] || type
}

function typeTag(type) {
  const map = { SINGLE: 'primary', MULTI: 'success', JUDGE: 'warning' }
  return map[type] || 'info'
}

async function loadData() {
  loading.value = true
  try {
    const res = await wrongApi.getList(page.value, size.value)
    list.value = res.data.records || res.data.list || []
    total.value = res.data.total || 0
  } catch (e) {
    // error handled by interceptor
  } finally {
    loading.value = false
  }
}

async function handleRemove(questionId) {
  try {
    await ElMessageBox.confirm('确定要移除这道错题吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await wrongApi.remove(questionId)
    ElMessage.success('移除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      // error handled by interceptor
    }
  }
}

async function handleClearAll() {
  try {
    await ElMessageBox.confirm('确定要清空全部错题吗？此操作不可恢复。', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await wrongApi.clearAll()
    ElMessage.success('已清空全部错题')
    page.value = 1
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      // error handled by interceptor
    }
  }
}

function viewDetail(questionId) {
  router.push(`/questions/${questionId}`)
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

.wrong-card {
  margin-bottom: 12px;
}

.wrong-header {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 8px;
}

.wrong-count {
  color: #f56c6c;
  font-size: 13px;
}

.wrong-time {
  color: #999;
  font-size: 12px;
}

.wrong-title {
  margin-bottom: 12px;
  line-height: 1.5;
}

.wrong-actions {
  display: flex;
  gap: 8px;
}

.pagination {
  margin-top: 16px;
  justify-content: center;
}
</style>