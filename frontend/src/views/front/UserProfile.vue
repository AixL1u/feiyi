<template>
  <div class="page-container">
    <div class="page-header"><h1>个人中心</h1></div>
    <div class="profile-card heritage-card" style="padding:30px;">
      <el-form :model="form" label-width="80px" style="max-width:500px;">
        <el-form-item label="头像">
          <div class="avatar-upload">
            <el-avatar :size="64" :src="form.avatar || ''" style="background:var(--primary-color);">{{ form.nickname?.charAt(0) }}</el-avatar>
            <el-upload action="/api/upload" :show-file-list="false" :on-success="onAvatarSuccess" accept="image/*">
              <el-button size="small" text type="primary">更换头像</el-button>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="用户名"><el-input :value="form.username" disabled /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item><el-button type="primary" @click="saveProfile">保存修改</el-button></el-form-item>
      </el-form>
    </div>
    <div class="profile-card heritage-card" style="padding:30px;margin-top:20px;">
      <h3 style="margin-bottom:16px;font-family:var(--font-serif);">修改密码</h3>
      <el-form :model="pwdForm" label-width="80px" style="max-width:500px;">
        <el-form-item label="原密码"><el-input v-model="pwdForm.oldPassword" type="password" show-password /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="pwdForm.newPassword" type="password" show-password /></el-form-item>
        <el-form-item><el-button type="primary" @click="changePwd">修改密码</el-button></el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { userApi, authApi } from '@/api'
import { ElMessage } from 'element-plus'

const form = ref({ username: '', nickname: '', phone: '', email: '', avatar: '' })
const pwdForm = ref({ oldPassword: '', newPassword: '' })

onMounted(async () => {
  try {
    const res = await authApi.getUserInfo()
    Object.assign(form.value, res.data)
  } catch (e) {}
})

const onAvatarSuccess = (res) => {
  if (res.code === 200) form.value.avatar = res.data
}

const saveProfile = async () => {
  try {
    await userApi.updateProfile(form.value)
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    Object.assign(user, { nickname: form.value.nickname, avatar: form.value.avatar, phone: form.value.phone, email: form.value.email })
    localStorage.setItem('user', JSON.stringify(user))
    ElMessage.success('保存成功')
  } catch (e) {}
}

const changePwd = async () => {
  if (!pwdForm.value.oldPassword || !pwdForm.value.newPassword) { ElMessage.warning('请填写完整'); return }
  try {
    await userApi.updatePassword(pwdForm.value)
    pwdForm.value = { oldPassword: '', newPassword: '' }
  } catch (e) {}
}
</script>

<style scoped>
.page-header { margin-bottom: 20px; }
.page-header h1 { font-size: 28px; font-family: var(--font-serif); letter-spacing: 2px; }
.avatar-upload { display: flex; align-items: center; gap: 16px; }
</style>
