import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi } from '../api'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  function setToken(t) {
    token.value = t
    localStorage.setItem('token', t)
  }

  function setUser(u) {
    user.value = u
    localStorage.setItem('user', JSON.stringify(u))
  }

  async function login(phone, password) {
    const res = await authApi.login({ phone, password })
    setToken(res.data.token)
    setUser({ userId: res.data.userId, nickname: res.data.nickname, avatarUrl: res.data.avatarUrl })
    return res
  }

  async function register(phone, password, nickname) {
    const res = await authApi.register({ phone, password, nickname })
    setToken(res.data.token)
    setUser({ userId: res.data.userId, nickname: res.data.nickname, avatarUrl: res.data.avatarUrl })
    return res
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  async function fetchProfile() {
    const res = await authApi.getProfile()
    if (res.data) {
      setUser({ userId: res.data.id, nickname: res.data.nickname, avatarUrl: res.data.avatarUrl })
    }
  }

  return { token, user, login, register, logout, fetchProfile, setToken, setUser }
})