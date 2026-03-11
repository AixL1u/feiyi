<template>
  <div class="admin-layout">
    <el-container style="height: 100vh">
      <el-aside :width="isCollapse ? '64px' : '220px'" class="admin-aside">
        <div class="aside-logo" @click="$router.push('/dashboard')">
          <svg viewBox="0 0 40 40" width="32" height="32">
            <circle cx="20" cy="20" r="18" fill="#c0392b"/>
            <text x="20" y="26" text-anchor="middle" fill="#fff" font-size="16" font-weight="bold" font-family="serif">遗</text>
          </svg>
          <span v-show="!isCollapse" class="aside-title">管理后台</span>
        </div>
        <el-menu :default-active="activeMenu" :collapse="isCollapse" router background-color="#2c1810" text-color="#c4a882" active-text-color="#f0d48a">
          <el-menu-item index="/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据概览</span>
          </el-menu-item>
          <el-sub-menu index="content">
            <template #title><el-icon><Collection /></el-icon><span>内容管理</span></template>
            <el-menu-item index="/dashboard/heritage">非遗项目</el-menu-item>
            <el-menu-item index="/dashboard/category">项目分类</el-menu-item>
            <el-menu-item index="/dashboard/inheritor">传承人</el-menu-item>
            <el-menu-item index="/dashboard/activity">活动管理</el-menu-item>
            <el-menu-item index="/dashboard/banner">轮播图</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="shop">
            <template #title><el-icon><ShoppingBag /></el-icon><span>商城管理</span></template>
            <el-menu-item index="/dashboard/product">商品管理</el-menu-item>
            <el-menu-item index="/dashboard/product-category">商品分类</el-menu-item>
            <el-menu-item index="/dashboard/order">订单管理</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="interact">
            <template #title><el-icon><ChatDotSquare /></el-icon><span>互动管理</span></template>
            <el-menu-item index="/dashboard/reservation">预约管理</el-menu-item>
            <el-menu-item index="/dashboard/message">留言管理</el-menu-item>
          </el-sub-menu>
          <el-menu-item index="/dashboard/user">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/log">
            <el-icon><Document /></el-icon>
            <span>操作日志</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header class="admin-header">
          <div class="header-left">
            <el-icon class="collapse-btn" @click="isCollapse = !isCollapse" :size="20">
              <Fold v-if="!isCollapse" /><Expand v-else />
            </el-icon>
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/dashboard' }">后台</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-button text @click="$router.push('/')">
              <el-icon><Monitor /></el-icon> 前台首页
            </el-button>
            <el-dropdown @command="handleCmd">
              <div class="admin-user">
                <el-avatar :size="28" style="background: var(--primary-color);">{{ user?.nickname?.charAt(0) || 'A' }}</el-avatar>
                <span>{{ user?.nickname || '管理员' }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        <el-main class="admin-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)

const activeMenu = computed(() => route.path)
const user = computed(() => {
  try { return JSON.parse(localStorage.getItem('user') || '{}') } catch { return {} }
})

const handleCmd = (cmd) => {
  if (cmd === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    router.push('/login')
  }
}
</script>

<style scoped>
.admin-aside {
  background: #2c1810;
  transition: width 0.3s;
  overflow: hidden;
}
.aside-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px;
  cursor: pointer;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
.aside-title {
  color: #f0d48a;
  font-size: 16px;
  font-weight: 600;
  font-family: var(--font-serif);
  letter-spacing: 2px;
  white-space: nowrap;
}
.admin-aside :deep(.el-menu) {
  border-right: none;
}
.admin-header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--border-color);
  padding: 0 20px;
  height: 56px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.collapse-btn {
  cursor: pointer;
  color: var(--text-secondary);
}
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.admin-user {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 13px;
  color: var(--text-secondary);
}
.admin-main {
  background: var(--bg-cream);
  padding: 20px;
}
</style>
