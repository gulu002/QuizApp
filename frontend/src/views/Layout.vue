<template>
  <el-container class="layout">
    <el-header class="header">
      <span class="header-title">嘉年华刷题</span>
      <el-avatar :size="32" :src="userStore.user?.avatarUrl" />
    </el-header>
    <el-main class="main">
      <router-view />
    </el-main>
    <div class="tabbar">
      <div
        v-for="tab in tabs"
        :key="tab.path"
        class="tabbar-item"
        :class="{ active: route.path === tab.path }"
        @click="$router.push(tab.path)"
      >
        <el-icon :size="22"><component :is="tab.icon" /></el-icon>
        <span>{{ tab.label }}</span>
      </div>
    </div>
  </el-container>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { HomeFilled, Document, Edit, Warning, User } from '@element-plus/icons-vue'

const route = useRoute()
const userStore = useUserStore()

const tabs = [
  { label: '首页', icon: HomeFilled, path: '/home' },
  { label: '题库', icon: Document, path: '/questions' },
  { label: '练习', icon: Edit, path: '/practice' },
  { label: '错题', icon: Warning, path: '/wrong' },
  { label: '我的', icon: User, path: '/profile' }
]
</script>

<style scoped>
.layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.main {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f5f7fa;
}

.tabbar {
  display: flex;
  background: #fff;
  border-top: 1px solid #ebeef5;
  justify-content: space-around;
  padding: 8px 0;
}

.tabbar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 12px;
  color: #999;
  cursor: pointer;
  gap: 2px;
}

.tabbar-item.active {
  color: #409eff;
}
</style>