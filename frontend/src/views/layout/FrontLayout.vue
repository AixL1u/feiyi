<template>
  <div class="front-layout">
    <header class="front-header">
      <div class="header-inner">
        <div class="logo" @click="$router.push('/')">
          <div class="logo-icon">
            <svg viewBox="0 0 40 40" width="36" height="36">
              <circle cx="20" cy="20" r="18" fill="url(#logoGrad)"/>
              <text x="20" y="26" text-anchor="middle" fill="#fff" font-size="16" font-weight="bold" font-family="serif">遗</text>
              <defs><linearGradient id="logoGrad" x1="0%" y1="0%" x2="100%" y2="100%"><stop offset="0%" style="stop-color:#c0392b"/><stop offset="100%" style="stop-color:#e74c3c"/></linearGradient></defs>
            </svg>
          </div>
          <div class="logo-text">
            <span class="logo-main">非遗传承</span>
            <span class="logo-sub">数字管理平台</span>
          </div>
        </div>
        <nav class="nav-menu">
          <router-link to="/" class="nav-item" exact-active-class="active">首页</router-link>
          <router-link to="/heritage" class="nav-item" active-class="active">非遗项目</router-link>
          <router-link to="/inheritors" class="nav-item" active-class="active">传承人</router-link>
          <router-link to="/activities" class="nav-item" active-class="active">活动资讯</router-link>
          <router-link to="/shop" class="nav-item" active-class="active">文创商城</router-link>
          <router-link to="/messages" class="nav-item" active-class="active">留言互动</router-link>
        </nav>
        <div class="header-actions">
          <el-input v-model="searchKeyword" placeholder="搜索非遗项目、传承人、商品..." class="search-input" @keyup.enter="doSearch" clearable>
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <template v-if="user">
            <el-dropdown @command="handleCommand">
              <div class="user-avatar-wrap">
                <el-avatar :size="32" :src="user.avatar || ''" style="background: var(--primary-color);">
                  {{ user.nickname?.charAt(0) || user.username?.charAt(0) }}
                </el-avatar>
                <span class="user-name">{{ user.nickname || user.username }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                  <el-dropdown-item command="address">收货地址</el-dropdown-item>
                  <el-dropdown-item v-if="user.role === 'ADMIN'" command="dashboard" divided>管理后台</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <router-link to="/cart" class="cart-btn">
              <el-badge :value="cartCount" :hidden="cartCount === 0" type="danger">
                <el-icon :size="22"><ShoppingCart /></el-icon>
              </el-badge>
            </router-link>
          </template>
          <template v-else>
            <router-link to="/login" class="btn-login">登录</router-link>
          </template>
        </div>
      </div>
    </header>
    <main class="front-main">
      <router-view />
    </main>
    <footer class="front-footer">
      <div class="footer-inner">
        <div class="footer-brand">
          <h3>非遗传承 数字管理平台</h3>
          <p>传承中华文化，守护民族记忆</p>
        </div>
        <div class="footer-links">
          <div class="footer-col">
            <h4>探索</h4>
            <router-link to="/heritage">非遗项目</router-link>
            <router-link to="/inheritors">传承人</router-link>
            <router-link to="/activities">活动资讯</router-link>
          </div>
          <div class="footer-col">
            <h4>服务</h4>
            <router-link to="/shop">文创商城</router-link>
            <router-link to="/messages">留言互动</router-link>
            <router-link to="/about">关于我们</router-link>
          </div>
        </div>
        <div class="footer-bottom">
          <p>非遗传承管理系统 - 让传统文化焕发新生</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const searchKeyword = ref('')

const user = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('user') || 'null')
  } catch { return null }
})

const cartCount = computed(() => {
  try {
    const cart = JSON.parse(localStorage.getItem('cart') || '[]')
    return cart.reduce((sum, item) => sum + item.quantity, 0)
  } catch { return 0 }
})

const doSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/search', query: { q: searchKeyword.value.trim() } })
  }
}

const handleCommand = (cmd) => {
  if (cmd === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    window.location.href = '/'
  } else if (cmd === 'profile') {
    router.push('/user/profile')
  } else if (cmd === 'orders') {
    router.push('/orders')
  } else if (cmd === 'address') {
    router.push('/address')
  } else if (cmd === 'dashboard') {
    router.push('/dashboard')
  }
}
</script>

<style scoped>
.front-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid var(--border-color);
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 1px 8px rgba(139, 69, 19, 0.06);
}
.header-inner {
  max-width: 1300px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  padding: 0 24px;
  height: 64px;
  gap: 20px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  flex-shrink: 0;
}
.logo-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}
.logo-main {
  font-size: 18px;
  font-weight: 700;
  color: var(--primary-color);
  font-family: var(--font-serif);
  letter-spacing: 2px;
}
.logo-sub {
  font-size: 10px;
  color: var(--text-light);
  letter-spacing: 1px;
}
.nav-menu {
  display: flex;
  gap: 4px;
  flex: 1;
  justify-content: center;
}
.nav-item {
  padding: 8px 16px;
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 14px;
  border-radius: var(--radius-sm);
  transition: all 0.3s;
  letter-spacing: 1px;
  position: relative;
}
.nav-item:hover,
.nav-item.active {
  color: var(--primary-color);
  background: var(--primary-lighter);
}
.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 2px;
  background: var(--primary-color);
  border-radius: 1px;
}
.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}
.search-input {
  width: 220px;
}
.search-input :deep(.el-input__wrapper) {
  border-radius: 20px;
  background: var(--bg-cream);
  box-shadow: none !important;
  border: 1px solid transparent;
}
.search-input :deep(.el-input__wrapper:hover),
.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color);
}
.user-avatar-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.user-name {
  font-size: 13px;
  color: var(--text-secondary);
}
.cart-btn {
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  text-decoration: none;
  transition: color 0.3s;
}
.cart-btn:hover {
  color: var(--primary-color);
}
.btn-login {
  padding: 6px 20px;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
  color: #fff;
  border-radius: 20px;
  text-decoration: none;
  font-size: 13px;
  letter-spacing: 1px;
  transition: all 0.3s;
}
.btn-login:hover {
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(192, 57, 43, 0.3);
}
.front-main {
  min-height: calc(100vh - 64px - 280px);
}
.front-footer {
  background: linear-gradient(135deg, #2c1810, #3d2317);
  color: #c4a882;
  margin-top: 60px;
}
.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 50px 24px 20px;
}
.footer-brand h3 {
  font-size: 20px;
  color: #f0d48a;
  font-family: var(--font-serif);
  letter-spacing: 3px;
  margin-bottom: 8px;
}
.footer-brand p {
  font-size: 13px;
  opacity: 0.7;
}
.footer-links {
  display: flex;
  gap: 80px;
  margin-top: 30px;
  padding-top: 30px;
  border-top: 1px solid rgba(255,255,255,0.1);
}
.footer-col h4 {
  color: #f0d48a;
  margin-bottom: 12px;
  font-size: 14px;
  letter-spacing: 1px;
}
.footer-col a {
  display: block;
  color: #c4a882;
  text-decoration: none;
  font-size: 13px;
  margin-bottom: 8px;
  transition: color 0.3s;
}
.footer-col a:hover {
  color: #f0d48a;
}
.footer-bottom {
  text-align: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid rgba(255,255,255,0.08);
  font-size: 12px;
  opacity: 0.5;
}
</style>
