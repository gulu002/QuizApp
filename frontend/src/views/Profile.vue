<template>
  <div class="profile">
    <div class="user-card">
      <el-avatar :size="60" :src="userStore.user?.avatarUrl" />
      <div class="user-name">{{ userStore.user?.nickname || '刷题人' }}</div>
    </div>
    <el-card class="section-card">
      <template #header><span>个人信息</span></template>
      <el-form :model="profileForm" label-width="80px">
        <el-form-item label="昵称">
          <el-input v-model="profileForm.nickname" />
        </el-form-item>
        <el-form-item label="头像URL">
          <el-input v-model="profileForm.avatarUrl" placeholder="输入头像图片链接" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdateProfile">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card class="section-card">
      <template #header><span>修改密码</span></template>
      <el-form :model="passwordForm" label-width="80px">
        <el-form-item label="原密码">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <div class="logout-section">
      <el-button type="danger" @click="handleDeleteAccount">注销账号</el-button>
      <el-button @click="handleLogout">退出登录</el-button>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../stores/user'
import { authApi } from '../api'

const router = useRouter()
const userStore = useUserStore()

const profileForm = reactive({
  nickname: userStore.user?.nickname || '',
  avatarUrl: userStore.user?.avatarUrl || ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: ''
})

async function handleUpdateProfile() {
  if (!profileForm.nickname.trim()) {
    ElMessage.warning('昵称不能为空')
    return
  }
  try {
    await authApi.updateProfile(profileForm.nickname, profileForm.avatarUrl)
    userStore.setUser({
      ...userStore.user,
      nickname: profileForm.nickname,
      avatarUrl: profileForm.avatarUrl
    })
    ElMessage.success('个人信息已更新')
  } catch {
    // error handled by interceptor
  }
}

async function handleChangePassword() {
  if (!passwordForm.oldPassword) {
    ElMessage.warning('请输入原密码')
    return
  }
  if (!passwordForm.newPassword) {
    ElMessage.warning('请输入新密码')
    return
  }
  try {
    await authApi.changePassword(passwordForm.oldPassword, passwordForm.newPassword)
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
  } catch {
    // error handled by interceptor
  }
}

async function handleDeleteAccount() {
  try {
    await ElMessageBox.confirm('确定要注销账号吗？此操作不可恢复！', '警告', { type: 'warning', confirmButtonText: '确定注销' })
  } catch {
    return
  }
  try {
    await authApi.deleteAccount()
    userStore.logout()
    ElMessage.success('账号已注销')
    router.push('/login')
  } catch {
    // error handled by interceptor
  }
}

async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'info' })
  } catch {
    return
  }
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.user-card {
  text-align: center;
  padding: 30px;
  background: white;
  border-radius: 12px;
}
.user-name {
  font-size: 18px;
  font-weight: bold;
  margin-top: 12px;
}
.section-card {
  margin-top: 16px;
}
.logout-section {
  margin-top: 24px;
  display: flex;
  gap: 12px;
  justify-content: center;
}
</style>