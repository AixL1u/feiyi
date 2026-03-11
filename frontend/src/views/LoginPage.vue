<template>
  <div class="login-page">
    <div class="login-bg-particles">
      <div v-for="i in 15" :key="i" class="particle" :style="particleStyle(i)"></div>
    </div>
    <div class="login-container">
      <div class="login-left">
        <div class="login-art">
          <div class="art-circle c1"></div>
          <div class="art-circle c2"></div>
          <div class="art-circle c3"></div>
          <div class="art-text">
            <h1>非遗传承</h1>
            <p>传承千年文脉 守护文化根脉</p>
            <div class="art-line"></div>
            <span class="art-eng">Intangible Cultural Heritage</span>
          </div>
        </div>
      </div>
      <div class="login-right">
        <div class="login-form-wrap">
          <div class="form-header">
            <h2>欢迎回来</h2>
            <p>登录非遗传承数字管理平台</p>
          </div>
          <el-form :model="form" class="login-form" @submit.prevent="handleLogin">
            <el-form-item>
              <el-input v-model="form.username" placeholder="请输入用户名" size="large" prefix-icon="User" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" prefix-icon="Lock" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="large" class="login-btn" @click="handleLogin" :loading="loading">登 录</el-button>
            </el-form-item>
          </el-form>
          <div class="quick-login">
            <div class="quick-title"><span>快捷登录</span></div>
            <div class="quick-buttons">
              <button class="quick-btn admin-btn" @click="quickLogin('admin', 'admin123')">
                <el-icon><Setting /></el-icon>
                <span>管理员登录</span>
              </button>
              <button class="quick-btn inheritor-btn" @click="quickLogin('inheritor1', 'admin123')">
                <el-icon><Medal /></el-icon>
                <span>传承人登录</span>
              </button>
              <button class="quick-btn user-btn" @click="quickLogin('user1', 'admin123')">
                <el-icon><UserFilled /></el-icon>
                <span>普通用户登录</span>
              </button>
            </div>
          </div>
          <div class="form-footer">
            还没有账号？<router-link to="/register">立即注册</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const form = ref({ username: '', password: '' })

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await authApi.login(form.value)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data.user))
    ElMessage.success('登录成功')
    if (res.data.user.role === 'ADMIN') {
      router.push('/dashboard')
    } else {
      router.push('/')
    }
  } catch (e) {
    // error handled by interceptor
  } finally {
    loading.value = false
  }
}

const quickLogin = (username, password) => {
  form.value.username = username
  form.value.password = password
  handleLogin()
}

const particleStyle = (i) => ({
  left: `${(i * 7) % 100}%`,
  top: `${(i * 13) % 100}%`,
  width: `${6 + (i % 4) * 4}px`,
  height: `${6 + (i % 4) * 4}px`,
  animationDelay: `${i * 0.5}s`,
  animationDuration: `${3 + (i % 3) * 2}s`,
})
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #fdf6f0 0%, #f9ebea 50%, #faf3eb 100%);
  position: relative;
  overflow: hidden;
}
.login-bg-particles {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.particle {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(192,57,43,0.15), rgba(212,160,74,0.1));
  animation: float-particle 4s ease-in-out infinite;
}
.login-container {
  display: flex;
  width: 900px;
  min-height: 540px;
  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(139, 69, 19, 0.12);
  overflow: hidden;
  position: relative;
  z-index: 1;
  border: 1px solid rgba(232, 213, 196, 0.4);
}
.login-left {
  width: 380px;
  background: linear-gradient(135deg, #2c1810, #4a2820, #6b3a2e);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}
.login-art {
  text-align: center;
  position: relative;
  z-index: 2;
}
.art-circle {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(240, 212, 138, 0.15);
}
.c1 { width: 200px; height: 200px; top: -60px; left: -60px; }
.c2 { width: 300px; height: 300px; top: -110px; left: -110px; }
.c3 { width: 150px; height: 150px; bottom: -40px; right: -40px; }
.art-text h1 {
  font-size: 36px;
  color: #f0d48a;
  font-family: var(--font-serif);
  letter-spacing: 8px;
  margin-bottom: 12px;
  text-shadow: 0 2px 10px rgba(0,0,0,0.3);
}
.art-text p {
  color: #c4a882;
  font-size: 13px;
  letter-spacing: 3px;
}
.art-line {
  width: 40px;
  height: 2px;
  background: linear-gradient(90deg, var(--accent-color), transparent);
  margin: 16px auto;
}
.art-eng {
  color: rgba(196, 168, 130, 0.5);
  font-size: 11px;
  letter-spacing: 2px;
}
.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}
.login-form-wrap {
  width: 100%;
  max-width: 360px;
}
.form-header {
  margin-bottom: 30px;
}
.form-header h2 {
  font-size: 24px;
  color: var(--text-primary);
  font-family: var(--font-serif);
  letter-spacing: 2px;
}
.form-header p {
  color: var(--text-light);
  font-size: 13px;
  margin-top: 6px;
}
.login-btn {
  width: 100%;
  border-radius: 8px;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-light)) !important;
  border: none !important;
  letter-spacing: 4px;
  font-size: 15px;
  height: 44px;
}
.quick-login {
  margin-top: 24px;
}
.quick-title {
  text-align: center;
  position: relative;
  margin-bottom: 16px;
}
.quick-title::before,
.quick-title::after {
  content: '';
  position: absolute;
  top: 50%;
  width: calc(50% - 50px);
  height: 1px;
  background: var(--border-color);
}
.quick-title::before { left: 0; }
.quick-title::after { right: 0; }
.quick-title span {
  background: #fff;
  padding: 0 12px;
  font-size: 12px;
  color: var(--text-light);
}
.quick-buttons {
  display: flex;
  gap: 10px;
}
.quick-btn {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 12px 8px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  background: var(--bg-cream);
  cursor: pointer;
  transition: all 0.3s;
  font-size: 11px;
  color: var(--text-secondary);
  font-family: var(--font-serif);
}
.quick-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
  background: var(--primary-lighter);
  transform: translateY(-2px);
  box-shadow: var(--shadow-soft);
}
.quick-btn .el-icon {
  font-size: 20px;
}
.admin-btn:hover { border-color: #c0392b; color: #c0392b; }
.inheritor-btn:hover { border-color: #d4a04a; color: #d4a04a; }
.user-btn:hover { border-color: #27ae60; color: #27ae60; }
.form-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 13px;
  color: var(--text-light);
}
.form-footer a {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 500;
}
</style>
