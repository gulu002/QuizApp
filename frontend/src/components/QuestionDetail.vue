<template>
  <div class="question-detail" v-if="question">
    <div class="qd-header">
      <el-tag :type="typeTag(question.questionType)" size="small">{{ typeLabel(question.questionType) }}</el-tag>
      <span v-if="question.difficulty" class="qd-difficulty">
        难度：{{ '★'.repeat(question.difficulty) }}{{ '☆'.repeat(5 - question.difficulty) }}
      </span>
      <span v-if="question.sourceNumber" class="qd-source">来源题号：#{{ question.sourceNumber }}</span>
    </div>

    <div class="qd-title">{{ question.title }}</div>

    <div class="qd-options" v-if="question.options && question.options.length">
      <!-- 单选题 / 判断题 -->
      <el-radio-group v-if="question.questionType === 'SINGLE' || question.questionType === 'JUDGE'" :model-value="question.correctAnswer" disabled>
        <div v-for="opt in question.options" :key="opt.label" class="qd-option-item">
          <el-radio :value="opt.label">
            <span :class="{ 'correct-answer': opt.label === question.correctAnswer }">{{ opt.label }}. {{ opt.text }}</span>
          </el-radio>
        </div>
      </el-radio-group>

      <!-- 多选题 -->
      <el-checkbox-group v-else-if="question.questionType === 'MULTI'" :model-value="correctAnswerArray" disabled>
        <div v-for="opt in question.options" :key="opt.label" class="qd-option-item">
          <el-checkbox :value="opt.label">
            <span :class="{ 'correct-answer': isCorrectOption(opt.label) }">{{ opt.label }}. {{ opt.text }}</span>
          </el-checkbox>
        </div>
      </el-checkbox-group>
    </div>

    <div class="qd-answer" v-if="question.correctAnswer">
      <span class="qd-label">正确答案：</span>
      <span class="qd-value">{{ question.correctAnswer }}</span>
    </div>

    <div class="qd-explanation" v-if="question.explanation">
      <span class="qd-label">解析：</span>
      <span class="qd-value">{{ question.explanation }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  question: {
    type: Object,
    default: null
  }
})

function typeTag(type) {
  const map = { SINGLE: '', MULTI: 'success', JUDGE: 'warning' }
  return map[type] || ''
}

function typeLabel(type) {
  const map = { SINGLE: '单选', MULTI: '多选', JUDGE: '判断' }
  return map[type] || type
}

const correctAnswerArray = computed(() => {
  if (!props.question?.correctAnswer) return []
  return props.question.correctAnswer.split('')
})

function isCorrectOption(label) {
  return correctAnswerArray.value.includes(label)
}
</script>

<style scoped>
.question-detail {
  padding: 8px 0;
}

.qd-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.qd-difficulty {
  font-size: 13px;
  color: #e6a23c;
}

.qd-source {
  font-size: 13px;
  color: #909399;
}

.qd-title {
  font-size: 16px;
  font-weight: 600;
  line-height: 1.8;
  margin-bottom: 20px;
  color: #303133;
}

.qd-options {
  margin-bottom: 16px;
}

.qd-option-item {
  margin-bottom: 10px;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 6px;
}

.correct-answer {
  font-weight: 600;
  color: #67c23a;
}

.qd-answer,
.qd-explanation {
  margin-top: 12px;
  padding: 12px;
  background: #f0f9eb;
  border-radius: 6px;
  line-height: 1.8;
}

.qd-label {
  font-weight: 600;
  color: #303133;
}

.qd-value {
  color: #606266;
}
</style>