<template>
  <div class="practice">
    <!-- 练习配置 -->
    <div v-if="!sessionId" class="practice-config">
      <h2>选择练习模式</h2>
      <el-radio-group v-model="config.sessionType" class="mode-group">
        <el-radio-button value="ORDER">顺序练习</el-radio-button>
        <el-radio-button value="RANDOM">随机练习</el-radio-button>
        <el-radio-button value="EXAM">模拟考试</el-radio-button>
        <el-radio-button value="WRONG">错题重练</el-radio-button>
        <el-radio-button value="FAV">收藏练习</el-radio-button>
        <el-radio-button value="TYPE">题型专练</el-radio-button>
      </el-radio-group>
      <div class="config-options" v-if="['RANDOM', 'EXAM', 'TYPE'].includes(config.sessionType)">
        <el-select v-if="config.sessionType === 'TYPE'" v-model="config.questionType" placeholder="选择题型">
          <el-option label="单选题" value="SINGLE" />
          <el-option label="多选题" value="MULTI" />
          <el-option label="判断题" value="JUDGE" />
        </el-select>
        <div class="count-select">
          <span>题目数量：</span>
          <el-radio-group v-model="config.questionCount" size="small">
            <el-radio-button :value="10">10</el-radio-button>
            <el-radio-button :value="20">20</el-radio-button>
            <el-radio-button :value="50">50</el-radio-button>
            <el-radio-button :value="0">自定义</el-radio-button>
          </el-radio-group>
          <el-input-number v-if="config.questionCount === 0" v-model="customCount" :min="1" :max="500" />
        </div>
        <div v-if="config.sessionType === 'EXAM'" class="time-limit">
          <span>时间限制：</span>
          <el-input-number v-model="config.timeLimitMin" :min="10" :max="180" :step="10" /> 分钟
        </div>
      </div>
      <el-button type="primary" size="large" @click="startPractice" :loading="starting">开始练习</el-button>
    </div>

    <!-- 答题界面 -->
    <div v-else class="practice-answer">
      <div class="progress-bar">
        <el-progress :percentage="Math.round(answeredCount / totalCount * 100)" />
        <span>{{ answeredCount }} / {{ totalCount }}</span>
      </div>
      <el-card class="question-card" v-if="currentQuestion">
        <div class="q-type">
          <el-tag>{{ typeLabel(currentQuestion.questionType) }}</el-tag>
        </div>
        <div class="q-title">{{ currentIndex + 1 }}. {{ currentQuestion.title }}</div>
        <div class="q-options">
          <template v-if="currentQuestion.questionType === 'SINGLE'">
            <el-radio-group v-model="currentAnswer" :disabled="submitted">
              <div v-for="opt in currentQuestion.options" :key="opt.label" class="option-item" :class="getOptionClass(opt.label)">
                <el-radio :value="opt.label">{{ opt.label }}. {{ opt.text }}</el-radio>
              </div>
            </el-radio-group>
            <el-button v-if="!submitted && currentAnswer" type="primary" @click="submitSingleAnswer" class="submit-btn">确认提交</el-button>
          </template>
          <template v-else-if="currentQuestion.questionType === 'MULTI'">
            <el-checkbox-group v-model="currentAnswers" :disabled="submitted">
              <div v-for="opt in currentQuestion.options" :key="opt.label" class="option-item" :class="getOptionClass(opt.label)">
                <el-checkbox :value="opt.label">{{ opt.label }}. {{ opt.text }}</el-checkbox>
              </div>
            </el-checkbox-group>
            <el-button v-if="!submitted" type="primary" @click="submitMultiAnswer" :disabled="!currentAnswers.length" class="submit-btn">确认提交</el-button>
          </template>
          <template v-else>
            <div class="judge-btns">
              <el-button :type="currentAnswer === 'A' ? 'primary' : ''" @click="submitJudgeAnswer('A')" :disabled="submitted">A. 正确</el-button>
              <el-button :type="currentAnswer === 'B' ? 'primary' : ''" @click="submitJudgeAnswer('B')" :disabled="submitted">B. 错误</el-button>
            </div>
          </template>
        </div>
        <div v-if="submitted" class="result-area">
          <el-alert :title="isCorrect ? '回答正确！' : '回答错误'" :type="isCorrect ? 'success' : 'error'" :closable="false" />
          <div class="correct-answer">正确答案：{{ result.correctAnswer }}</div>
          <div class="explanation" v-if="result.explanation">{{ result.explanation }}</div>
        </div>
      </el-card>
      <div class="nav-buttons">
        <el-button :disabled="currentIndex === 0" @click="prevQuestion">上一题</el-button>
        <el-button @click="showAnswerSheet = true">答题卡</el-button>
        <el-button v-if="currentIndex < totalCount - 1" type="primary" @click="nextQuestion">下一题</el-button>
        <el-button v-else type="warning" @click="finishPractice">交卷</el-button>
      </div>
    </div>

    <!-- 答题卡弹窗 -->
    <el-dialog v-model="showAnswerSheet" title="答题卡" width="350px">
      <div class="answer-sheet">
        <div
          v-for="(q, idx) in questionList"
          :key="q.id"
          class="sheet-item"
          :class="{ answered: answeredMap[q.id], current: idx === currentIndex }"
          @click="jumpTo(idx)"
        >
          {{ idx + 1 }}
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { practiceApi } from '../api'

const router = useRouter()

// ---- 练习配置 ----
const config = reactive({
  sessionType: 'ORDER',
  questionType: 'SINGLE',
  questionCount: 10,
  timeLimitMin: 30
})
const customCount = ref(10)
const starting = ref(false)

// ---- 答题状态 ----
const sessionId = ref(null)
const questionList = ref([])
const currentIndex = ref(0)
const submitted = ref(false)
const currentAnswer = ref('')
const currentAnswers = ref([])
const result = ref({ correctAnswer: '', explanation: '', isCorrect: false })
const answeredMap = ref({})
const showAnswerSheet = ref(false)
let examTimer = null

// ---- 计算属性 ----
const totalCount = computed(() => questionList.value.length)
const answeredCount = computed(() => Object.keys(answeredMap.value).length)
const currentQuestion = computed(() => questionList.value[currentIndex.value] || null)
const isCorrect = computed(() => result.value.isCorrect)

// ---- 工具函数 ----
function typeLabel(type) {
  const map = { SINGLE: '单选题', MULTI: '多选题', JUDGE: '判断题' }
  return map[type] || type
}

function getOptionClass(optLabel) {
  if (!submitted.value) return ''
  if (optLabel === result.value.correctAnswer) return 'correct'
  const userAnswer = currentQuestion.value.questionType === 'MULTI'
    ? currentAnswers.value.join('')
    : currentAnswer.value
  if (optLabel === userAnswer || (currentQuestion.value.questionType === 'MULTI' && currentAnswers.value.includes(optLabel))) {
    if (optLabel !== result.value.correctAnswer) return 'wrong'
  }
  return ''
}

// ---- 开始练习 ----
async function startPractice() {
  starting.value = true
  try {
    const params = {
      sessionType: config.sessionType,
      questionCount: config.questionCount === 0 ? customCount.value : config.questionCount
    }
    if (config.sessionType === 'TYPE') {
      params.questionType = config.questionType
    }
    if (config.sessionType === 'EXAM') {
      params.timeLimitMin = config.timeLimitMin
    }
    const res = await practiceApi.start(params)
    sessionId.value = res.data.sessionId
    await loadQuestions()
    loadCurrentQuestionState()
    if (config.sessionType === 'EXAM' && config.timeLimitMin) {
      startExamTimer(config.timeLimitMin * 60)
    }
  } catch (e) {
    // error handled by interceptor
  } finally {
    starting.value = false
  }
}

async function loadQuestions() {
  const res = await practiceApi.getSessionQuestions(sessionId.value)
  questionList.value = res.data
}

async function loadCurrentQuestionState() {
  const res = await practiceApi.getSessionProgress(sessionId.value)
  const answers = res.data.answers || {}
  answeredMap.value = {}
  for (const [qid, ans] of Object.entries(answers)) {
    answeredMap.value[qid] = true
  }
}

// ---- 提交答案 ----
async function submitSingleAnswer() {
  if (!currentAnswer.value) return
  await doSubmit(currentAnswer.value)
}

async function submitMultiAnswer() {
  if (!currentAnswers.value.length) return
  await doSubmit(currentAnswers.value.join(''))
}

async function submitJudgeAnswer(answer) {
  currentAnswer.value = answer
  await doSubmit(answer)
}

async function doSubmit(userAnswer) {
  try {
    const res = await practiceApi.submit({
      sessionId: sessionId.value,
      questionId: currentQuestion.value.id,
      answer: userAnswer
    })
    submitted.value = true
    result.value = {
      correctAnswer: res.data.correctAnswer,
      explanation: res.data.explanation,
      isCorrect: res.data.isCorrect
    }
    answeredMap.value[currentQuestion.value.id] = true
  } catch (e) {
    // error handled by interceptor
  }
}

// ---- 导航 ----
function prevQuestion() {
  if (currentIndex.value > 0) {
    currentIndex.value--
    loadQuestionState()
  }
}

function nextQuestion() {
  if (currentIndex.value < totalCount.value - 1) {
    currentIndex.value++
    loadQuestionState()
  }
}

function jumpTo(idx) {
  currentIndex.value = idx
  showAnswerSheet.value = false
  loadQuestionState()
}

function loadQuestionState() {
  submitted.value = false
  currentAnswer.value = ''
  currentAnswers.value = []
  result.value = { correctAnswer: '', explanation: '', isCorrect: false }
}

// ---- 交卷 ----
async function finishPractice() {
  try {
    await ElMessageBox.confirm('确定要交卷吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await practiceApi.finish({ sessionId: sessionId.value })
    clearExamTimer()
    ElMessage.success(`练习完成！得分：${res.data.score || 0}`)
    sessionId.value = null
    router.push('/home')
  } catch (e) {
    if (e !== 'cancel') {
      // error handled by interceptor
    }
  }
}

// ---- 考试计时 ----
function startExamTimer(seconds) {
  clearExamTimer()
  let remaining = seconds
  examTimer = setInterval(() => {
    remaining--
    if (remaining <= 0) {
      clearExamTimer()
      ElMessage.warning('考试时间已到，自动交卷！')
      finishPractice()
    }
  }, 1000)
}

function clearExamTimer() {
  if (examTimer) {
    clearInterval(examTimer)
    examTimer = null
  }
}

onBeforeUnmount(() => {
  clearExamTimer()
})
</script>

<style scoped>
.practice-config {
  text-align: center;
  padding: 40px;
}

.mode-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin: 20px 0;
}

.config-options {
  margin: 16px 0;
}

.count-select,
.time-limit {
  margin: 10px 0;
}

.submit-btn {
  margin-top: 12px;
}

.progress-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.question-card {
  margin-bottom: 16px;
}

.q-type {
  margin-bottom: 8px;
}

.q-title {
  font-size: 16px;
  font-weight: bold;
  margin: 16px 0;
}

.option-item {
  padding: 8px;
  margin: 4px 0;
  border-radius: 4px;
}

.option-item.correct {
  background: #f0f9eb;
}

.option-item.wrong {
  background: #fef0f0;
}

.judge-btns {
  display: flex;
  gap: 12px;
}

.result-area {
  margin-top: 16px;
}

.correct-answer {
  margin-top: 8px;
  font-weight: bold;
  color: #67c23a;
}

.explanation {
  margin-top: 8px;
  color: #666;
  line-height: 1.5;
}

.nav-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 16px;
}

.answer-sheet {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.sheet-item {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #f0f0f0;
  cursor: pointer;
}

.sheet-item.answered {
  background: #67c23a;
  color: white;
}

.sheet-item.current {
  border: 2px solid #409eff;
}
</style>