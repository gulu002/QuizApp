<template>
  <div class="questions">
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索题干关键词" clearable @clear="search" @keyup.enter="search" style="width: 240px" />
      <el-select v-model="questionType" placeholder="全部题型" clearable @change="search" style="width: 120px">
        <el-option label="全部" value="" />
        <el-option label="单选题" value="SINGLE" />
        <el-option label="多选题" value="MULTI" />
        <el-option label="判断题" value="JUDGE" />
      </el-select>
      <el-select v-model="category" placeholder="全部分类" clearable @change="search" style="width: 140px">
        <el-option label="全部" value="" />
        <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
      </el-select>
      <el-button type="primary" @click="search">搜索</el-button>
    </div>

    <el-card v-for="q in questions" :key="q.id" class="question-card" @click="showDetail(q)">
      <div class="q-header">
        <el-tag :type="typeTag(q.questionType)" size="small">{{ typeLabel(q.questionType) }}</el-tag>
        <span class="q-source" v-if="q.sourceNumber">#{{ q.sourceNumber }}</span>
      </div>
      <div class="q-title">{{ q.title }}</div>
    </el-card>

    <el-pagination
      v-if="total > 0"
      v-model:current-page="page"
      :page-size="size"
      :total="total"
      layout="prev, pager, next"
      @current-change="loadData"
      class="pagination"
    />

    <el-dialog v-model="detailVisible" title="题目详情" width="700px">
      <QuestionDetail :question="currentQuestion" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { questionApi } from '../api'
import QuestionDetail from '../components/QuestionDetail.vue'

const keyword = ref('')
const questionType = ref('')
const category = ref('')
const page = ref(1)
const size = ref(10)
const total = ref(0)
const questions = ref([])
const categories = ref([])

const detailVisible = ref(false)
const currentQuestion = ref(null)

function typeTag(type) {
  const map = { SINGLE: '', MULTI: 'success', JUDGE: 'warning' }
  return map[type] || ''
}

function typeLabel(type) {
  const map = { SINGLE: '单选', MULTI: '多选', JUDGE: '判断' }
  return map[type] || type
}

async function loadData() {
  try {
    const params = {
      page: page.value,
      size: size.value
    }
    if (keyword.value) params.keyword = keyword.value
    if (questionType.value) params.questionType = questionType.value
    if (category.value) params.category = category.value

    const res = await questionApi.query(params)
    if (res.data) {
      questions.value = res.data.records || res.data || []
      total.value = res.data.total || 0
    }
  } catch (e) {
    // ignore
  }
}

async function loadCategories() {
  try {
    const res = await questionApi.getCategoryStats()
    if (res.data) {
      categories.value = res.data.map(item => item.category) || []
    }
  } catch (e) {
    // ignore
  }
}

function search() {
  page.value = 1
  loadData()
}

function showDetail(q) {
  currentQuestion.value = q
  detailVisible.value = true
}

onMounted(() => {
  loadData()
  loadCategories()
})
</script>

<style scoped>
.questions {
  padding: 16px;
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.question-card {
  margin-bottom: 12px;
  cursor: pointer;
}

.q-header {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.q-source {
  color: #909399;
  font-size: 12px;
}

.q-title {
  font-size: 14px;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.pagination {
  margin-top: 16px;
  justify-content: center;
}
</style>