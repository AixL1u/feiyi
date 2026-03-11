<template>
  <div class="login-page">
    <div class="login-bg-particles">
      <div v-for="i in 10" :key="i" class="particle" :style="{ left: `${(i*9)%100}%`, top: `${(i*17)%100}%`, width: `${8+(i%3)*4}px`, height: `${8+(i%3)*4}px`, animationDelay: `${i*0.6}s`, animationDuration: `${4+(i%3)}s` }"></div>
    </div>
    <div class="register-box">
      <div class="form-header">
        <div class="reg-logo" @click="$router.push('/')">
          <svg viewBox="0 0 40 40" width="40" height="40"><circle cx="20" cy="20" r="18" fill="#c0392b"/><text x="20" y="26" text-anchor="middle" fill="#fff" font-size="16" font-weight="bold" font-family="serif">遗</text></svg>
        </div>
        <h2>注册账号</h2>
        <p>加入非遗传承数字管理平台</p>
      </div>
      <el-form :model="form" class="login-form">
        <el-form-item><el-input v-model="form.username" placeholder="用户名" size="large" prefix-icon="User" /></el-form-item>
        <el-form-item><el-input v-model="form.nickname" placeholder="昵称" size="large" prefix-icon="UserFilled" /></el-form-item>
        <el-form-item><el-input v-model="form.password" type="password" placeholder="密码" size="large" prefix-icon="Lock" show-password /></el-form-item>
        <el-form-item><el-input v-model="confirmPassword" type="password" placeholder="确认密码" size="large" prefix-icon="Lock" show-password /></el-form-item>
        <el-form-item><el-button type="primary" size="large" style="width:100%;border-radius:8px;letter-spacing:4px;background:linear-gradient(135deg,#c0392b,#e74c3c)!important;border:none!important;height:44px;" @click="handleRegister" :loading="loading">注 册</el-button></el-form-item>
      </el-form>
      <div class="form-footer">已有账号？<router-link to="/login">立即登录</router-link></div>
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
const form = ref({ username: '', password: '', nickname: '' })
const confirmPassword = ref('')

const handleRegister = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (form.value.password !== confirmPassword.value) {
    ElMessage.warning('两次密码不一致')
    return
  }
  loading.value = true
  try {
    await authApi.register(form.value)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {} finally { loading.value = false }
}
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
.login-bg-particles { position: absolute; inset: 0; pointer-events: none; }
.particle {
  position: absolute; border-radius: 50%;
  background: radial-gradient(circle, rgba(192,57,43,0.15), rgba(212,160,74,0.1));
  animation: float-particle 4s ease-in-out infinite;
}
.register-box {
  width: 420px; background: rgba(255,255,255,0.92); backdrop-filter: blur(20px);
  border-radius: 20px; padding: 40px; box-shadow: 0 20px 60px rgba(139,69,19,0.12);
  border: 1px solid rgba(232,213,196,0.4); position: relative; z-index: 1;
}
.reg-logo { text-align: center; margin-bottom: 16px; cursor: pointer; }
.form-header { text-align: center; margin-bottom: 28px; }
.form-header h2 { font-size: 22px; color: var(--text-primary); font-family: var(--font-serif); letter-spacing: 2px; }
.form-header p { color: var(--text-light); font-size: 13px; margin-top: 6px; }
.form-footer { text-align: center; margin-top: 16px; font-size: 13px; color: var(--text-light); }
.form-footer a { color: var(--primary-color); text-decoration: none; }
</style>
