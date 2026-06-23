import request from './request'

// 认证
export const authApi = {
  login: (data) => request.post('/auth/login', data),
  register: (data) => request.post('/auth/register', data),
  getProfile: () => request.get('/auth/profile'),
  updateProfile: (nickname, avatarUrl) => request.put('/auth/profile', null, { params: { nickname, avatarUrl } }),
  changePassword: (oldPassword, newPassword) => request.put('/auth/password', null, { params: { oldPassword, newPassword } }),
  deleteAccount: () => request.delete('/auth/account')
}

// 题目
export const questionApi = {
  query: (params) => request.get('/questions', { params }),
  getDetail: (id) => request.get(`/questions/${id}`),
  countByType: (questionType) => request.get('/questions/count', { params: { questionType } }),
  getCategoryStats: () => request.get('/questions/categories/stats')
}

// 练习
export const practiceApi = {
  start: (data) => request.post('/practice/start', data),
  submit: (data) => request.post('/practice/submit', data),
  finish: (data) => request.post('/practice/finish', data),
  getSessionProgress: (sessionId) => request.get(`/practice/session/${sessionId}`),
  getSessionQuestions: (sessionId) => request.get(`/practice/session/${sessionId}/questions`),
  getHistory: (page, size) => request.get('/practice/history', { params: { page, size } })
}

// 错题
export const wrongApi = {
  getList: (page, size) => request.get('/wrong-questions', { params: { page, size } }),
  remove: (questionId) => request.delete(`/wrong-questions/${questionId}`),
  batchRemove: (questionIds) => request.delete('/wrong-questions/batch', { data: questionIds }),
  clearAll: () => request.delete('/wrong-questions/clear'),
  getCount: () => request.get('/wrong-questions/count')
}

// 收藏
export const favoriteApi = {
  toggle: (questionId) => request.post(`/favorites/toggle/${questionId}`),
  check: (questionId) => request.get(`/favorites/check/${questionId}`),
  getList: (page, size) => request.get('/favorites', { params: { page, size } }),
  batchRemove: (questionIds) => request.delete('/favorites/batch', { data: questionIds }),
  getCount: () => request.get('/favorites/count')
}

// 统计
export const statsApi = {
  overview: () => request.get('/stats/overview'),
  byType: () => request.get('/stats/by-type'),
  byCategory: () => request.get('/stats/by-category'),
  calendar: (year, month) => request.get('/stats/calendar', { params: { year, month } }),
  weakness: () => request.get('/stats/weakness')
}